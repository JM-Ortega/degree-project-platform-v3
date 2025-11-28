package co.edu.unicauca.departmentheadservice.infra.messaging;

import co.edu.unicauca.departmentheadservice.access.AnteproyectoRepository;
import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.shared.contracts.events.academic.AnteproyectoSinEvaluadoresEvent;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RabbitListener(queues = "${messaging.queues.department}") // UNA sola cola, dos bindings
public class DepartmentHeadEventListener {

    private final AnteproyectoRepository anteproyectoRepository;
    private final DocenteRepository docenteRepository;

    public DepartmentHeadEventListener(AnteproyectoRepository anteproyectoRepository,
                                       DocenteRepository docenteRepository) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.docenteRepository = docenteRepository;
    }

    /** Llega auth.user.created */
    @RabbitHandler
    public void onUserCreated(UserCreatedEvent event,
                              @Header("amqp_receivedRoutingKey") String rk) {
        if (event == null || event.id() == null) {
            log.warn("[DeptHead] Ignorado UserCreatedEvent inválido. rk={}", rk);
            return;
        }

        boolean esDocente = event.roles() != null &&
                event.roles().stream().anyMatch(rol -> "DOCENTE".equalsIgnoreCase(rol.name()));
        if (!esDocente) {
            log.debug("[DeptHead] Usuario descartado (no docente): {} rk={}", event.email(), rk);
            return;
        }

        try {
            Docente docente = new Docente(event.id(), event.nombre(), event.email());
            docenteRepository.save(docente);
            log.info("[DeptHead] Docente almacenado: {} ({})", docente.getNombre(), docente.getEmail());
        } catch (Exception e) {
            log.error("[DeptHead] Error almacenando docente {}: {}", event.email(), e.getMessage(), e);
            throw e; // permite reintento/DLQ
        }
    }

    /** Llega academic.anteproyecto.created */
    @RabbitHandler
    public void onAnteproyecto(AnteproyectoSinEvaluadoresEvent event,
                               @Header("amqp_receivedRoutingKey") String rk) {
        if (event == null || event.anteproyectoId() == null) {
            log.warn("[DeptHead] Ignorado Anteproyecto inválido. rk={}", rk);
            return;
        }

        try {
            // Idempotencia
            if (anteproyectoRepository.existsByAnteproyectoId(event.anteproyectoId())) {
                log.debug("[DeptHead] Ignorado: anteproyectoId={} ya registrado", event.anteproyectoId());
                return;
            }

            List<Docente> evaluadores = List.of();

            Anteproyecto ante = new Anteproyecto(
                    event.anteproyectoId(),
                    event.proyectoId(),
                    event.titulo(),
                    event.descripcion(),
                    event.fechaCreacion(),
                    evaluadores,
                    event.estudianteCorreo(),
                    event.directorCorreo(),
                    event.departamento()
            );

            anteproyectoRepository.save(ante);
            log.info("[DeptHead] Anteproyecto almacenado. anteId={} titulo={}",
                    event.anteproyectoId(), event.titulo());
        } catch (Exception e) {
            log.error("[DeptHead] Error guardando anteproyecto projId={} anteId={}: {}",
                    event.proyectoId(), event.anteproyectoId(), e.getMessage(), e);
            throw e; // reintentos/DLQ
        }
    }

    /** Catch-all: si llega algo sin __TypeId__ o de tipo desconocido */
    @RabbitHandler
    public void onUnknown(Map<?, ?> payload,
                          @Header("amqp_receivedRoutingKey") String rk) {
        log.warn("[DeptHead] Payload desconocido. rk={} payload={}", rk, payload);
    }
}
