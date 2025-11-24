package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.access.PersonaRepository;
import co.edu.unicauca.authservice.access.UsuarioRepository;
import co.edu.unicauca.authservice.domain.entities.Docente;
import co.edu.unicauca.authservice.domain.entities.JefeDeDepartamento;
import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.domain.entities.Usuario;
import co.edu.unicauca.authservice.dto.KeycloakTokenResponse;
import co.edu.unicauca.authservice.dto.LoginRequest;
import co.edu.unicauca.authservice.dto.LoginResponse;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.authservice.infra.keycloak.KeycloakUserClient;
import co.edu.unicauca.authservice.infra.messaging.NotificationPublisher;
import co.edu.unicauca.authservice.infra.messaging.UserEventsPublisher;
import co.edu.unicauca.shared.contracts.dto.SessionInfo;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Rol;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * Servicio de aplicación para el registro y sincronización de usuarios.
 * <p>
 * El login real se hace ahora contra Keycloak (OIDC). Este servicio:
 * <ul>
 *     <li>Valida y crea Persona/Usuario en la base interna.</li>
 *     <li>Sincroniza el usuario en Keycloak.</li>
 *     <li>Publica eventos para otros microservicios.</li>
 * </ul>
 * </p>
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    /**
     * Roles que se permiten en el registro público.
     */
    private static final List<Rol> ROLES_AUTOREGISTRO = List.of(
            Rol.ESTUDIANTE,
            Rol.DOCENTE
    );

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final CodigoPersonaGenerator codigoPersonaGenerator;
    private final PersonaFactory personaFactory;
    private final UserEventsPublisher userEventsPublisher;
    private final NotificationPublisher notificationPublisher;
    private final KeycloakUserClient keycloakUserClient;
    private final WebClient webClient;
    @Value("${keycloak.desktop-client-id:sistema-desktop}")
    private String desktopClientId;
    @Value("${keycloak.token-url}")
    private String keycloakTokenUrl;


    public AuthService(UsuarioRepository usuarioRepository,
                       PersonaRepository personaRepository,
                       CodigoPersonaGenerator codigoPersonaGenerator,
                       PersonaFactory personaFactory,
                       UserEventsPublisher userEventsPublisher,
                       NotificationPublisher notificationPublisher,
                       KeycloakUserClient keycloakUserClient,
                       WebClient webClient) {

        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.codigoPersonaGenerator = codigoPersonaGenerator;
        this.personaFactory = personaFactory;
        this.userEventsPublisher = userEventsPublisher;
        this.notificationPublisher = notificationPublisher;
        this.keycloakUserClient = keycloakUserClient;
        this.webClient = webClient;
    }


    /**
     * Registra una nueva persona y usuario en el sistema y en Keycloak.
     *
     * Flujo:
     * 1) Normaliza/valida correo y roles.
     * 2) Crea Usuario + Persona en la base de datos.
     * 3) Crea el usuario en Keycloak (contraseña incluida).
     * 4) Guarda el keycloakId en la entidad Usuario.
     * 5) Publica eventos a otros microservicios.
     */
    @Transactional
    public Persona registrarPersona(RegistroPersonaDto dto) {

        // 1. normalizar correo para TODO el método
        final String emailNormalizado = dto.email().trim().toLowerCase();

        // 2. Verificar correo único sobre el correo normalizado
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        // 3. Validar roles permitidos para autoregistro
        boolean tieneRolNoPermitido = dto.roles().stream()
                .anyMatch(rol -> !ROLES_AUTOREGISTRO.contains(rol));

        if (tieneRolNoPermitido) {
            throw new IllegalArgumentException("No está autorizado para registrarse con ese rol");
        }

        // 4. Crear usuario interno (sin password, Keycloak se encarga)
        Usuario usuario = new Usuario(emailNormalizado, dto.roles());

        // 5. Crear persona concreta (usa el usuario ya creado)
        Persona persona = personaFactory.crearDesdeDto(dto, usuario);

        // 6. Generar código institucional y guardar en DB
        persona.setCodigo(codigoPersonaGenerator.generar());
        personaRepository.save(persona); // cascada guarda Usuario

        // 7. Crear usuario en Keycloak y obtener su id
        List<String> roleNames = dto.roles().stream()
                .map(Rol::name)    // debe coincidir con los nombres de roles en Keycloak
                .toList();

        String keycloakId = keycloakUserClient.createUser(
                emailNormalizado,
                dto.nombres(),
                dto.apellidos(),
                dto.password(),     // texto plano, Keycloak la hashea
                roleNames
        );

        // 8. Guardar el keycloakId en el usuario interno
        usuario.setKeycloakId(keycloakId);
        usuarioRepository.save(usuario);

        // 9. Publicar eventos a los demás microservicios
        try {
            var departamento = obtenerDepartamentoSiAplica(persona);

            UserCreatedEvent userEvent = new UserCreatedEvent(
                    persona.getId(),
                    persona.getNombres() + " " + persona.getApellidos(),
                    usuario.getEmail(),           // ya es el normalizado
                    persona.getPrograma(),
                    departamento,
                    usuario.getRoles()
            );
            userEventsPublisher.publishUserCreatedEvent(userEvent);

            String type = "auth.user.created";
            String subject = "Bienvenido a la plataforma";
            String message = "Tu cuenta ha sido creada correctamente.";

            var emails = List.of(usuario.getEmail());
            var celulares = (persona.getCelular() != null && !persona.getCelular().isBlank())
                    ? List.of(persona.getCelular())
                    : List.<String>of();

            notificationPublisher.publishNotification(
                    type,
                    emails,
                    celulares,
                    subject,
                    message
            );

            log.info("Eventos publicados correctamente para usuario {}", usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al publicar eventos para el usuario {}: {}", emailNormalizado, e.getMessage(), e);
        }

        return persona;
    }

    /**
     * Autentica a un usuario contra Keycloak verificando credenciales y rol.
     */
    public LoginResponse login(LoginRequest request) {

        // normalizar email
        final String emailNormalizado = request.email().trim().toLowerCase();

        // ---- 1) Llamar al endpoint de token de Keycloak ----
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", desktopClientId); // "sistema-desktop"
        form.add("grant_type", "password");
        form.add("username", emailNormalizado);
        form.add("password", request.password());

        KeycloakTokenResponse kc = webClient.post()
                .uri(keycloakTokenUrl) // http://localhost:8181/realms/.../token
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new IllegalArgumentException("Usuario o contraseña incorrectos"))
                )
                .bodyToMono(KeycloakTokenResponse.class)
                .block();

        // ---- 2) Decodificar el access_token ----
        DecodedJWT jwt = JWT.decode(kc.accessToken());

        // Obtener roles de Keycloak (del cliente sistema-desktop)
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access").asMap();
        Map<String, Object> desktopAccess = (Map<String, Object>) resourceAccess.get(desktopClientId);

        List<String> roles = (List<String>) desktopAccess.get("roles");

        // ---- 3) Verificar rol solicitado ----
        if (!roles.contains(request.rol().name())) {
            throw new IllegalArgumentException("El usuario no tiene el rol solicitado.");
        }

        // ---- 4) Construir SessionInfo ----
        String nombre = jwt.getClaim("name").asString();
        if (nombre == null || nombre.isBlank()) {
            nombre = emailNormalizado;
        }

        SessionInfo session = new SessionInfo(
                jwt.getClaim("email").asString(),
                nombre,
                request.rol()
        );

        // ---- 5) Devolver LoginResponse final ----
        return new LoginResponse(
                kc.accessToken(),
                kc.refreshToken(),
                kc.expiresIn(),
                kc.tokenType(),
                session,
                roles
        );
    }


    /**
     * Determina el departamento asociado a una persona, si aplica según su tipo concreto.
     */
    private Departamento obtenerDepartamentoSiAplica(Persona persona) {
        return switch (persona) {
            case Docente d -> d.getDepartamento();
            case JefeDeDepartamento j -> j.getDepartamento();
            default -> null;
        };
    }
}
