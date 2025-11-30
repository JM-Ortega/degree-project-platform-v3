package co.edu.unicauca.academicprojectservice.adapter.in.messaging;

import co.edu.unicauca.academicprojectservice.port.in.messaging.AnteproyectoEvaluadoresUseCase;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.ProyectoDTO;
import co.edu.unicauca.shared.contracts.events.departmenthead.AnteproyectoConEvaluadoresEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RabbitListener(queues = "${messaging.queues.project}")
public class DepartmentHeadAnteproyectoListener {

    private final AnteproyectoEvaluadoresUseCase useCase;

    public DepartmentHeadAnteproyectoListener(AnteproyectoEvaluadoresUseCase useCase) {
        this.useCase = useCase;
    }

    @RabbitHandler
    public void onAnteproyectoConEvaluadores(AnteproyectoConEvaluadoresEvent event,
                                             @Header("amqp_receivedRoutingKey") String rk) {
        try {
            log.info("[Academic] AnteproyectoConEvaluadoresEvent recibido. rk={} proyectoId={} anteId={}",
                    rk, event.proyectoId(), event.anteproyectoId());

            useCase.procesarAsignacionEvaluadores(event);
        } catch (Exception e) {
            log.error("[Academic] Error procesando AnteproyectoConEvaluadoresEvent: {}", e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Error procesando AnteproyectoConEvaluadoresEvent", e);
        }
    }

    @RabbitHandler
    public void onProyectoDTO(ProyectoDTO dto,
                              @Header("amqp_receivedRoutingKey") String rk) {
        // Por si en el futuro llega PROJECT_CREATED a esta misma cola
        log.debug("[Academic] ProyectoDTO recibido en projectQueue (ignorado). rk={} id={}", rk, dto.getId());
    }

    @RabbitHandler
    public void onUnknown(Map<?, ?> payload,
                          @Header("amqp_receivedRoutingKey") String rk) {
        log.warn("[Academic] Mensaje desconocido en projectQueue rk={} payload={}", rk, payload);
    }
}
