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

    @RabbitListener(queues = "${messaging.queues.projectFormatoA}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTO dto) {
        try {
            if (dto == null) {
                System.err.println("❌ [RabbitMQ] FormatoA DTO nulo — ignorado");
                return;
            }

            System.out.printf(
                    "📩 [RabbitMQ] Evento FormatoA recibido: proyectoId=%s, estado=%s, version=%d%n",
                    dto.getProyectoId(), dto.getEstado(), dto.getNroVersion()
            );

            if (dto.getProyectoId() == null) {
                throw new IllegalArgumentException("proyectoId es requerido");
            }
            if (dto.getEstado() == null) {
                throw new IllegalArgumentException("estado es requerido");
            }

            EstadoFormatoA estado = dto.getEstado();

            proyectoService.registrarResultadoRevisionFormatoADesdeEvento(
                    dto.getProyectoId(),
                    estado,
                    dto.getBlob(),
                    dto.getNombreFormatoA()
            );


            System.out.println("✔️ [AcademicProjectService] Revisión de FormatoA aplicada al proyecto");

        } catch (IllegalArgumentException ex) {
            System.err.println("⚠️ [RabbitMQ] Evento FormatoA inválido: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Evento inválido FormatoA", ex);

        } catch (Exception ex) {
            System.err.println("❌ [RabbitMQ] Error procesando FormatoA: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Error procesando FormatoA", ex);
        }
    }
}
