package co.edu.unicauca.academicprojectservice.infrastructure.adapters.input.messaging;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.repository.FormatoARepository;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.FormatoADTOSend;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class FormatoAListener {

    private final FormatoARepository formatoARepository;
    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;

    public FormatoAListener(FormatoARepository formatoARepository,
                            DocenteRepository docenteRepository,
                            EstudianteRepository estudianteRepository) {
        this.formatoARepository = formatoARepository;
        this.docenteRepository = docenteRepository;
        this.estudianteRepository = estudianteRepository;
    }

    /**
     * Escucha mensajes relacionados con FormatoA o Proyectos provenientes del exchange principal.
     * Los eventos llegan desde otros microservicios (p. ej., coordinator-service o project-service)
     * a través de la cola del servicio académico.
     */
    @RabbitListener(queues = "${messaging.queues.coordinator}")
    @Transactional
    public void handleFormatoAEvent(FormatoADTOSend dto) {
        try {
            if (dto == null) {
                System.err.println("[RabbitMQ] FormatoA DTO nulo — se ignora");
                return;
            }
            System.out.println("📩 [RabbitMQ] Mensaje recibido (FormatoA): " + dto);

            if (dto.getProyectoId() == null) {
                throw new IllegalArgumentException("proyectoId es requerido");
            }
            if (dto.getNombreFormatoA() == null || dto.getNombreFormatoA().isBlank()) {
                throw new IllegalArgumentException("nombreFormatoA es requerido");
            }
            if (dto.getNroVersion() <= 0) {
                throw new IllegalArgumentException("nroVersion es requerido o inválido");
            }

            if (dto.getEstado() == null) {
                throw new IllegalArgumentException("estado es requerido");
            }

            final String estadoName = (dto.getEstado() instanceof Enum<?>)
                    ? ((Enum<?>) dto.getEstado()).name()
                    : dto.getEstado().toString();
            final EstadoFormatoA estado = EstadoFormatoA.valueOf(estadoName);

            Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getProyectoId());
            FormatoA formato = existingFormato.orElse(new FormatoA(dto.getNroVersion(), dto.getNombreFormatoA(), dto.getBlob()));
            formato.cambiarEstado(estado);

            formatoARepository.save(formato);

            System.out.println("[AcademicProjectService] FormatoA actualizado/creado ");
        } catch (IllegalArgumentException ex) {
            System.err.println("[RabbitMQ] Evento FormatoA inválido: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Evento FormatoA inválido", ex);
        } catch (Exception ex) {
            System.err.println("[RabbitMQ] Error procesando FormatoA: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Error procesando FormatoA", ex);
        }
    }
}
