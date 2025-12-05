package co.edu.unicauca.authservice;

import co.edu.unicauca.authservice.access.PersonaRepository;
import co.edu.unicauca.authservice.access.UsuarioRepository;
import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.domain.entities.Usuario;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.authservice.infra.keycloak.KeycloakUserClient;
import co.edu.unicauca.authservice.infra.messaging.NotificationPublisher;
import co.edu.unicauca.authservice.infra.messaging.UserEventsPublisher;
import co.edu.unicauca.authservice.services.CodigoPersonaGenerator;
import co.edu.unicauca.authservice.services.PersonaFactory;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Carga usuarios de demo en BD + Keycloak
 * como si hubieran pasado por el flujo normal de registro.
 */
@Component
//@Profile({"dev", "local"})
public class AuthDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AuthDataLoader.class);

    private final UsuarioRepository usuarioRepo;
    private final PersonaRepository personaRepo;
    private final PersonaFactory personaFactory;
    private final CodigoPersonaGenerator codigoPersonaGenerator;
    private final KeycloakUserClient keycloakUserClient;
    private final UserEventsPublisher userEventsPublisher;
    private final NotificationPublisher notificationPublisher;

    public AuthDataLoader(UsuarioRepository usuarioRepo,
                          PersonaRepository personaRepo,
                          PersonaFactory personaFactory,
                          CodigoPersonaGenerator codigoPersonaGenerator,
                          KeycloakUserClient keycloakUserClient,
                          UserEventsPublisher userEventsPublisher,
                          NotificationPublisher notificationPublisher) {
        this.usuarioRepo = usuarioRepo;
        this.personaRepo = personaRepo;
        this.personaFactory = personaFactory;
        this.codigoPersonaGenerator = codigoPersonaGenerator;
        this.keycloakUserClient = keycloakUserClient;
        this.userEventsPublisher = userEventsPublisher;
        this.notificationPublisher = notificationPublisher;
    }

    @Override
    public void run(String... args) {
        log.info("=== Iniciando carga de datos demo para AuthService ===");

        final String password = "Uni123456*";

        crearUsuarioDemo(
                "estudiante.demo@unicauca.edu.co",
                "Camila",
                "López",
                Programa.INGENIERIA_DE_SISTEMAS,
                null,
                List.of(Rol.ESTUDIANTE),
                password
        );

        crearUsuarioDemo(
                "docente.demo@unicauca.edu.co",
                "Andrés",
                "García",
                Programa.INGENIERIA_DE_SISTEMAS,
                Departamento.SISTEMAS,
                List.of(Rol.DOCENTE),
                password
        );

        crearUsuarioDemo(
                "coordinador.demo@unicauca.edu.co",
                "María",
                "Pérez",
                Programa.INGENIERIA_DE_SISTEMAS,
                null,
                List.of(Rol.COORDINADOR),
                password
        );

        crearUsuarioDemo(
                "jefe.demo@unicauca.edu.co",
                "Jorge",
                "Ramírez",
                Programa.INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES,
                Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL,
                List.of(Rol.JEFE_DE_DEPARTAMENTO),
                password
        );

        crearUsuarioDemo(
                "multi.demo@unicauca.edu.co",
                "Laura",
                "Hernández",
                Programa.INGENIERIA_DE_SISTEMAS,
                Departamento.SISTEMAS,
                List.of(
                        Rol.ESTUDIANTE,
                        Rol.DOCENTE,
                        Rol.COORDINADOR,
                        Rol.JEFE_DE_DEPARTAMENTO
                ),
                password
        );

        log.info("=== Datos demo creados correctamente ===");
    }

    /**
     * Crea usuario demo en Keycloak + BD + eventos.
     */
    private void crearUsuarioDemo(String email,
                                  String nombres,
                                  String apellidos,
                                  Programa programa,
                                  Departamento departamento,
                                  List<Rol> roles,
                                  String passwordPlano) {

        final String emailNorm = email.trim().toLowerCase();

        if (usuarioRepo.existsByEmail(emailNorm)) {
            log.info("Omitido (ya existe en BD): {}", emailNorm);
            return;
        }

        try {
            RegistroPersonaDto dto = new RegistroPersonaDto(
                    nombres,
                    apellidos,
                    emailNorm,
                    passwordPlano,
                    null, // celular
                    programa,
                    roles,
                    departamento
            );

            List<String> roleNames = roles.stream()
                    .map(Rol::name)
                    .toList();

            String keycloakId = keycloakUserClient.createUser(
                    emailNorm,
                    nombres,
                    apellidos,
                    passwordPlano,
                    roleNames
            );

            Usuario usuario = new Usuario(emailNorm, roles);
            usuario.setKeycloakId(keycloakId);

            Persona persona = personaFactory.crearDesdeDto(dto, usuario);
            persona.setCodigo(codigoPersonaGenerator.generar());

            personaRepo.save(persona);

            publicarEventos(persona, usuario);

            log.info("Usuario DEMO creado en BD + Keycloak: {}", emailNorm);

        } catch (Exception e) {
            log.error("Error creando usuario demo {}: {}", emailNorm, e.getMessage(), e);
        }
    }

    private void publicarEventos(Persona persona, Usuario usuario) {
        try {
            Departamento departamento = switch (persona) {
                case co.edu.unicauca.authservice.domain.entities.Docente d -> d.getDepartamento();
                case co.edu.unicauca.authservice.domain.entities.JefeDeDepartamento j -> j.getDepartamento();
                default -> null;
            };

            UserCreatedEvent event = new UserCreatedEvent(
                    persona.getId(),
                    persona.getNombres(),
                    persona.getApellidos(),
                    usuario.getEmail(),
                    null,
                    persona.getPrograma(),
                    departamento,
                    usuario.getRoles()
            );

            userEventsPublisher.publishUserCreatedEvent(event);

            notificationPublisher.publishNotification(
                    "auth.user.created",
                    List.of(usuario.getEmail()),
                    "Bienvenido a la plataforma",
                    "Tu usuario demo ha sido creado correctamente.",
                    persona.getPrograma()
            );

        } catch (Exception e) {
            log.error("Error publicando eventos para {}: {}", usuario.getEmail(), e.getMessage(), e);
        }
    }
}
