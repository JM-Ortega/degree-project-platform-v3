package co.edu.unicauca.academicprojectservice.Old.Controller;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.FormatoARepository;
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
    @RabbitListener(queues = "${messaging.queues.coordinator}") // <-- cola dedicada
    @Transactional
    public void handleFormatoAEvent(FormatoADTOSend dto) {
        try {
            // ===== Validaciones defensivas =====
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

            // ===== Mapeo de estado (Enum externo o String) =====
            // Si dto.getEstado() es Enum de otro paquete, usa .name(); si es String, úsalo directo.
            final String estadoName = (dto.getEstado() instanceof Enum<?>)
                    ? ((Enum<?>) dto.getEstado()).name()
                    : dto.getEstado().toString();
            final EstadoFormatoA estado = EstadoFormatoA.valueOf(estadoName);

            // ===== Upsert por proyecto =====
            Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getProyectoId());
            FormatoA formato = existingFormato.orElse(new FormatoA());

            formato.setNroVersion(dto.getNroVersion());
            formato.setNombreFormato(dto.getNombreFormatoA());
            formato.setFechaCreacion(dto.getFechaSubida());
            formato.setBlob(dto.getBlob());
            formato.setEstado(estado);

            // TODO(si aplica): asociar el Proyecto si el nuevo FormatoA no lo tiene aún
            // formato.setProyecto(proyectoRepository.getReferenceById(dto.getProyectoId()));

            formatoARepository.save(formato);

            System.out.println("[AcademicProjectService] FormatoA actualizado/creado: "
                    + formato.getNombreFormato() + " (versión " + formato.getNroVersion() + ")");
        } catch (IllegalArgumentException ex) {
            // Datos inválidos -> no reencolar, que vaya a DLQ
            System.err.println("[RabbitMQ] Evento FormatoA inválido: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Evento FormatoA inválido", ex);
        } catch (Exception ex) {
            // Error inesperado -> no reencolar para evitar bucles
            System.err.println("[RabbitMQ] Error procesando FormatoA: " + ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Error procesando FormatoA", ex);
        }
    }
}
