package co.edu.unicauca.authservice.infra.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Cliente de infraestructura para gestionar usuarios en Keycloak.
 *
 * Solo se encarga de llamar al Admin API. La lógica de negocio
 * (qué roles asignar, cuándo crear/eliminar, etc.) se decide
 * en los servicios de aplicación.
 */
@Service
@RequiredArgsConstructor
public class KeycloakUserClient {

    private final Keycloak keycloakAdminClient;

    /**
     * Realm donde viven los usuarios funcionales del sistema.
     * Por defecto "unicauca".
     */
    @Value("${keycloak.target-realm:unicauca}")
    private String targetRealm;

    /**
     * Client ID que contiene los roles de negocio (DOCENTE, ESTUDIANTE, etc.).
     * En tu realm exportado es "sistema-desktop".
     */
    @Value("${keycloak.desktop-client-id:sistema-desktop}")
    private String desktopClientId;

    /**
     * Crea un usuario en Keycloak, asigna la contraseña y, si se proveen,
     * asigna los roles de cliente correspondientes.
     *
     * @param email      email/username del usuario
     * @param firstName  nombres
     * @param lastName   apellidos
     * @param rawPassword contraseña en texto plano (Keycloak la guardará hasheada)
     * @param roleNames  nombres de roles de negocio (por ejemplo: ["ESTUDIANTE", "DOCENTE"])
     * @return id del usuario en Keycloak (UUID)
     */
    public String createUser(String email,
                             String firstName,
                             String lastName,
                             String rawPassword,
                             List<String> roleNames) {

        RealmResource realm = keycloakAdminClient.realm(targetRealm);
        UsersResource users = realm.users();

        // 1. Construir representación del usuario
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        // 2. Crear usuario
        Response response = users.create(user);
        if (response.getStatus() >= 300) {
            String msg = "Error creando usuario en Keycloak. Status: "
                    + response.getStatus() + " - " + response.getStatusInfo();
            response.close();
            throw new IllegalStateException(msg);
        }

        String keycloakId = CreatedResponseUtil.getCreatedId(response);
        response.close();

        // 3. Asignar contraseña
        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(false);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(rawPassword);

        users.get(keycloakId).resetPassword(password);

        // 4. Asignar roles de cliente (si se envían)
        if (roleNames != null && !roleNames.isEmpty()) {
            assignClientRoles(realm, keycloakId, roleNames);
        }

        return keycloakId;
    }

    /**
     * Asigna una lista de roles de CLIENTE (no realm roles) al usuario dado.
     */
    private void assignClientRoles(RealmResource realm,
                                   String keycloakUserId,
                                   List<String> roleNames) {

        // Buscar el cliente "sistema-desktop"
        var clientRepList = realm.clients().findByClientId(desktopClientId);
        if (clientRepList == null || clientRepList.isEmpty()) {
            throw new IllegalStateException("No se encontró el clientId en Keycloak: " + desktopClientId);
        }

        var clientRep = clientRepList.getFirst();
        ClientResource clientResource = realm.clients().get(clientRep.getId());

        // Obtener representaciones de roles
        List<RoleRepresentation> roles = roleNames.stream()
                .map(name -> clientResource.roles().get(name).toRepresentation())
                .toList();

        // Asignar roles de cliente al usuario
        realm.users()
                .get(keycloakUserId)
                .roles()
                .clientLevel(clientRep.getId())
                .add(roles);
    }
}
