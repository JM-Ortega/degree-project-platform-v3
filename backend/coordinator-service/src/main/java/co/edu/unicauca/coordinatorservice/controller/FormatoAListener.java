package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.FormatoADTO;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FormatoAListener {

    private static final Logger log = LoggerFactory.getLogger(FormatoAListener.class);

    private final FormatoARepository formatoARepository;

    public FormatoAListener(FormatoARepository formatoARepository) {
        this.formatoARepository = formatoARepository;
    }

    /**
     * Maneja eventos de Formato A publicados por academic-project-service.
     * El coordinator mantiene una réplica local para sus consultas.
     */
    @RabbitListener(queues = "${messaging.queues.coordinatorFormatoA}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTO dto) {
        if (dto == null) {
            log.warn("[CoordinatorService] Mensaje FormatoADTO nulo recibido. Se ignora.");
            return;
        }

        log.info("[CoordinatorService] Evento FormatoA recibido: nombreFormatoA='{}', proyectoId={}, version={}",
                dto.getNombreFormatoA(), dto.getProyectoId(), dto.getNroVersion());

        // Buscar FormatoA existente por proyectoId, o crear uno nuevo
        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getProyectoId());
        FormatoA formato = existingFormato.orElseGet(FormatoA::new);

        // Actualizar campos básicos
        formato.setProyectoId(dto.getProyectoId());
        formato.setNroVersion(dto.getNroVersion());
        formato.setNombreFormatoA(dto.getNombreFormatoA());
        formato.setFechaSubida(dto.getFechaSubida());
        formato.setBlob(dto.getBlob());

        // Estado del Formato A (enum compartido)
        if (dto.getEstado() != null) {
            formato.setEstadoFormatoA(dto.getEstado());
        } else {
            log.warn("[CoordinatorService] FormatoA recibido sin estado (proyectoId={}). Se marca como PENDIENTE.",
                    dto.getProyectoId());
            formato.setEstadoFormatoA(EstadoFormatoA.PENDIENTE);
        }

        formatoARepository.save(formato);

        log.info("[CoordinatorService] FormatoA persistido: nombreFormatoA='{}', proyectoId={}, version={}",
                formato.getNombreFormatoA(), formato.getProyectoId(), formato.getNroVersion());
    }
}
