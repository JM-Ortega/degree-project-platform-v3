package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.*;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.FormatoADTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class FormatoAListener {
    private final FormatoARepository formatoARepository;

    public FormatoAListener(FormatoARepository formatoARepository) {
        this.formatoARepository = formatoARepository;
    }

    /**
     * Escucha mensajes provenientes de la cola del coordinador.
     * Estos mensajes llegan desde el microservicio de la fuente de la verdad
     * cuando se crea o actualiza un FormatoA o un Proyecto.
     */
    @RabbitListener(queues = "${messaging.queues.project}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTO dto) {
        if (dto == null) {
            System.err.println("⚠️ [RabbitMQ] Se recibió un mensaje nulo en CoordinatorService.");
            return;
        }

        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.getNombreFormatoA());

        // Buscar si ya existe el FormatoA
        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getProyectoId());
        FormatoA formato = existingFormato.orElse(new FormatoA());

        // Actualizar campos con la información nueva
        formato.setProyectoId(dto.getProyectoId());
        formato.setNroVersion(dto.getNroVersion());
        formato.setNombreFormatoA(dto.getNombreFormatoA());
        formato.setFechaSubida(dto.getFechaSubida());
        formato.setBlob(dto.getBlob());

        if (dto.getEstado() != null) {
            try {
                formato.setEstadoFormatoA(EstadoFormatoA.valueOf(dto.getEstado().toString()));
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ Estado inválido recibido: " + dto.getEstado());
            }
        } else {
            System.err.println("⚠️ FormatoA recibido sin estado (proyectoId=" + dto.getProyectoId() + ")");
            // Opcional: asignar un valor por defecto
            formato.setEstadoFormatoA(EstadoFormatoA.PENDIENTE);
        }

        formatoARepository.save(formato);

        System.out.println("[CoordinatorService] FormatoA guardado/actualizado correctamente: "
                + formato.getNombreProyecto() + " | Versión: " + formato.getNroVersion());
    }
}
