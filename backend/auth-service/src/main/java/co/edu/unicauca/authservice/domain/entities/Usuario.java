package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa un usuario autenticable del sistema.
 *
 * <p>
 * Administra la identidad base del usuario dentro de la plataforma:
 * email institucional, roles asignados y el identificador asociado
 * en Keycloak.
 * </p>
 *
 * <p>
 * Un usuario puede tener múltiples roles globales del sistema
 * (por ejemplo, ESTUDIANTE, DOCENTE o COORDINADOR).
 * </p>
 */
@Entity
@Table(name = "usuarios")
@Schema(description = "Entidad que representa un usuario autenticable del sistema.")
public class Usuario {

    @Id
    @Column(length = 36, updatable = false, nullable = false)
    @Schema(description = "Identificador único del usuario (UUID).",
            example = "d3b07384-d9a3-4a7a-9a44-61a4c1234b8f")
    private String id;

    @Column(nullable = false, unique = true, length = 120)
    @Schema(description = "Correo institucional del usuario.",
            example = "juan.perez@unicauca.edu.co")
    private String email;

    /**
     * Identificador del usuario en Keycloak.
     *
     * <p>
     * Se utiliza para mantener sincronización entre la base interna
     * y Keycloak (actualización de contraseña, asignación de roles,
     * bloqueo, eliminación, etc.).
     * </p>
     */
    @Column(name = "keycloak_id", length = 40, unique = true)
    @Schema(description = "Identificador único del usuario en Keycloak.",
            example = "edbe6452-d6f7-4b3f-89a4-04c21f9b1582")
    private String keycloakId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 40)
    @Schema(description = "Lista de roles globales asociados al usuario.")
    private List<Rol> roles = new ArrayList<>();

    protected Usuario() {
        // Requerido por JPA
    }

    public Usuario(String email, List<Rol> roles) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.roles = roles;
    }

    // Getters y setters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public List<Rol> getRoles() { return roles; }

    public void setEmail(String email) { this.email = email; }
    public void setRoles(List<Rol> roles) { this.roles = roles; }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }
}
