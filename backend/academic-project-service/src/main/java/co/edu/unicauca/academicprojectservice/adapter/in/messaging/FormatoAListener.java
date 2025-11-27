package co.edu.unicauca.academicprojectservice.adapter.in.messaging;

import co.edu.unicauca.academicprojectservice.application.services.ProyectoService;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.FormatoADTO;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FormatoAListener {

    private final ProyectoService proyectoService;

    public FormatoAListener(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    /**
     * Escucha mensajes relacionados con FormatoA o Proyectos provenientes del exchange principal.
     * Los eventos llegan desde otros microservicios (p. ej., coordinator-service o project-service)
     * a través de la cola del servicio académico.
     */
    @RabbitListener(queues = "${messaging.queues.projectFormatoA}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTO dto) {
        try {
            if (dto == null) {
                System.err.println("[RabbitMQ] FormatoA DTO nulo — se ignora");
                return;
            }
            System.out.println("📩 [RabbitMQ] Mensaje recibido (FormatoA): " + dto);

            if (dto.getProyectoId() == null) {
                throw new IllegalArgumentException("proyectoId es requerido");
            }
            if (dto.getEstado() == null) {
                throw new IllegalArgumentException("estado es requerido");
            }

            final String estadoName = (dto.getEstado() instanceof Enum<?>)
                    ? ((Enum<?>) dto.getEstado()).name()
                    : dto.getEstado().toString();
            final EstadoFormatoA estado = EstadoFormatoA.valueOf(estadoName);

            proyectoService.registrarResultadoRevisionFormatoADesdeEvento(dto.getProyectoId(), estado);

            System.out.println("[AcademicProjectService] Resultado de revisión de FormatoA aplicado al proyecto");
        } catch (IllegalArgumentException ex) {
            System.err.println("[RabbitMQ] Evento FormatoA inválido: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Evento FormatoA inválido", ex);
        } catch (Exception ex) {
            System.err.println("[RabbitMQ] Error procesando FormatoA: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Error procesando FormatoA", ex);
        }
    }
}
