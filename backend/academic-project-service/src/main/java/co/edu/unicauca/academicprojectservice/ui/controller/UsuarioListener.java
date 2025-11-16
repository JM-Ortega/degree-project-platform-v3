package co.edu.unicauca.academicprojectservice.Old.Controller;

import co.edu.unicauca.academicprojectservice.Old.Entity.Coordinador;
import co.edu.unicauca.academicprojectservice.domain.model.Docente;
import co.edu.unicauca.academicprojectservice.domain.model.Estudiante;
import co.edu.unicauca.academicprojectservice.Old.Entity.JefeDeDepartamento;
import co.edu.unicauca.academicprojectservice.Old.Repository.CoordinadorRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.Old.Repository.JefeDeDepartamentoRepository;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.model.Rol;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Escucha los eventos de creación de usuario y sincroniza la información
 * en el microservicio de proyectos académicos.
 */
@Component
public class UsuarioListener {

    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;
    private final CoordinadorRepository coordinadorRepository;
    private final JefeDeDepartamentoRepository jefeDeDepartamentoRepository;

    public UsuarioListener(DocenteRepository docenteRepository,
                           EstudianteRepository estudianteRepository,
                           CoordinadorRepository coordinadorRepository,
                           JefeDeDepartamentoRepository jefeDeDepartamentoRepository) {
        this.docenteRepository = docenteRepository;
        this.estudianteRepository = estudianteRepository;
        this.coordinadorRepository = coordinadorRepository;
        this.jefeDeDepartamentoRepository = jefeDeDepartamentoRepository;
    }

    /**
     * Consume el evento compartido de creación de usuario y realiza la
     * actualización correspondiente según el rol del usuario.
     */
    @RabbitListener(queues = "${messaging.queues.auth}")
    @Transactional
    public void onUserCreated(UserCreatedEvent evt) {
        try {
            if (evt == null) return;

            final String email = evt.email() == null ? null : evt.email().trim().toLowerCase();
            if (email == null || email.isBlank()) return;

            final List<Rol> roles = evt.roles();
            if (roles == null || roles.isEmpty()) return;

            String nombres = evt.nombre();
            String apellidos = evt.apellido();
            if (nombres != null) {
                int idx = nombres.lastIndexOf(' ');
                if (idx > 0) {
                    apellidos = nombres.substring(idx + 1).trim();
                    nombres = nombres.substring(0, idx).trim();
                }
            }

            for (Rol r : roles) {
                switch (r) {
                    case DOCENTE -> procesarDocente(email, nombres, apellidos, evt);
                    case ESTUDIANTE -> procesarEstudiante(email, nombres, apellidos, evt);
                    case COORDINADOR -> procesarCoordinador(email);
                    case JEFE_DE_DEPARTAMENTO -> procesarJefeDepartamento(email);
                    default -> System.out.println("[RabbitMQ] Rol no manejado: {}");
                }
            }

        } catch (Exception ex) {
            throw new AmqpRejectAndDontRequeueException("Error procesando UserCreatedEvent", ex);
        }
    }

    /** Registra o actualiza la información del jefe de departamento. */
    private void procesarJefeDepartamento(String email) {
        Optional<JefeDeDepartamento> existente = jefeDeDepartamentoRepository.findByCorreo(email);
        JefeDeDepartamento jefe = existente.orElse(new JefeDeDepartamento());
        jefe.setCorreo(email);
        jefeDeDepartamentoRepository.save(jefe);
    }

    /** Registra o actualiza la información del coordinador. */
    private void procesarCoordinador(String email) {
        Optional<Coordinador> existente = coordinadorRepository.findByCorreo(email);
        Coordinador c = existente.orElse(new Coordinador());
        c.setCorreo(email);
        coordinadorRepository.save(c);
    }

    /** Registra o actualiza la información del docente. */
    private void procesarDocente(String email, String nombres, String apellidos, UserCreatedEvent evt) {
        Optional<Docente> existente = docenteRepository.findByCorreo(email);
        Docente d = existente.orElse(new Docente());
        d.setCorreo(email);
        d.setNombres(nombres);
        d.setApellidos(apellidos);
        d.setDepartamento(evt.departamento());
        docenteRepository.save(d);
    }

    /** Registra o actualiza la información del estudiante. */
    private void procesarEstudiante(String email, String nombres, String apellidos, UserCreatedEvent evt) {
        Optional<Estudiante> existente = estudianteRepository.findByCorreoIgnoreCase(email);
        Estudiante e = existente.orElse(new Estudiante());
        e.setCorreo(email);
        e.setNombres(nombres);
        e.setApellidos(apellidos);
        e.setPrograma(evt.programa());
        estudianteRepository.save(e);
    }
}
