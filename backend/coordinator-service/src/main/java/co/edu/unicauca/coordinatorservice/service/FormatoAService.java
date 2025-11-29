package co.edu.unicauca.coordinatorservice.service;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOSInternos.FormatoAResumenDTO;
import co.edu.unicauca.coordinatorservice.repository.CoordinadorRepository;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.FormatoADTO;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static co.edu.unicauca.shared.contracts.messaging.RoutingKeys.*;

@Slf4j
@Service
public class FormatoAService {

    private final RabbitTemplate rabbitTemplate;
    private final FormatoARepository formatoARepository;
    private final CoordinadorRepository coordinadorRepository;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    public FormatoAService(FormatoARepository formatoARepository,
                           RabbitTemplate rabbitTemplate,
                           CoordinadorRepository coordinadorRepository) {
        this.formatoARepository = formatoARepository;
        this.rabbitTemplate = rabbitTemplate;
        this.coordinadorRepository = coordinadorRepository;
    }

    /**
     * Actualiza un Formato A existente, incrementa versión y publica
     * eventos de dominio + notificación.
     */
    public FormatoA actualizarFormato(UUID id,
                                      MultipartFile archivo,
                                      String nuevoEstado,
                                      String nombreArchivo) throws IOException {

        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formato A no encontrado con id: " + id));

        if (formato.getNroVersion() > 3) {
            throw new IllegalStateException("No se pueden subir más de 3 versiones del Formato A para este proyecto.");
        }

        formato.setNroVersion(formato.getNroVersion());
        formato.setBlob(archivo.getBytes());
        formato.setNombreFormatoA(nombreArchivo);
        formato.setFechaSubida(OffsetDateTime.now(ZoneOffset.UTC).toLocalDate());

        String estadoNormalizado = nuevoEstado == null
                ? ""
                : nuevoEstado.trim().toLowerCase();

        boolean rechazado = "rechazado".equals(estadoNormalizado);

        String routingKeyFuncional = rechazado
                ? COORDINATOR_FORMAT_A_REJECTED
                : COORDINATOR_FORMAT_A_APPROVED;

        String subject = rechazado ? "Formato A Rechazado" : "Formato A Aprobado";
        String message = String.format(
                "El Formato A del proyecto '%s' fue %s por el coordinador.",
                formato.getNombreProyecto(),
                rechazado ? "RECHAZADO" : "APROBADO"
        );

        formato.setEstadoFormatoA(rechazado ? EstadoFormatoA.OBSERVADO : EstadoFormatoA.APROBADO);
        formatoARepository.save(formato);

        FormatoADTO dto = new FormatoADTO();
        dto.setId(formato.getId());
        dto.setProyectoId(formato.getProyectoId());
        dto.setNroVersion(formato.getNroVersion());
        dto.setNombreFormatoA(formato.getNombreFormatoA());
        dto.setFechaSubida(formato.getFechaSubida());
        dto.setBlob(formato.getBlob());
        dto.setEstado(formato.getEstadoFormatoA());

        log.info("Converter activo: {}", rabbitTemplate.getMessageConverter().getClass().getName());
        rabbitTemplate.convertAndSend(mainExchange, routingKeyFuncional, dto);
        log.info("📤 Evento funcional publicado: {}", routingKeyFuncional);

        List<String> destinatarios = new ArrayList<>();

        if (formato.getDirector() != null && formato.getDirector().getEmail() != null) {
        destinatarios.add(formato.getDirector().getEmail());
        }
        if (formato.getCoodirector() != null && formato.getCoodirector().getEmail() != null) {
            destinatarios.add(formato.getCoodirector().getEmail());
        }
        if (formato.getEstudiantes() != null) {
        for (Estudiante e : formato.getEstudiantes()) {
            if (e.getEmail() != null) {
                destinatarios.add(e.getEmail());
            }
        }
        }

        NotificationEvent notificationEvent = new NotificationEvent(
                routingKeyFuncional,
                destinatarios,
                subject,
                message,
                null,
                OffsetDateTime.now(ZoneOffset.UTC),
                true
        );

        rabbitTemplate.convertAndSend(mainExchange, NOTIFICATION_SEND + "." + routingKeyFuncional, notificationEvent);
        log.info("📨 Evento de notificación publicado: {}", routingKeyFuncional);

        return formato;
    }

    public List<FormatoAResumenDTO> listarResumenPorCoordinador(String correoCoordinador) {

        // 1. Buscar coordinador por correo
        Coordinador coordinador = coordinadorRepository.findByCorreo(correoCoordinador)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró coordinador con correo: " + correoCoordinador
                ));

        var programaCoord = coordinador.getPrograma(); // enum o String, según tu entidad

        // 2. Traer todos los formatos y filtrar por programa de estudiante
        List<FormatoA> todos = formatoARepository.findAll();

        List<FormatoA> filtrados = todos.stream()
                .filter(f -> {
                    if (f.getEstudiantes() == null || f.getEstudiantes().isEmpty()) {
                        return false;
                    }
                    // asumiendo que el programa de los estudiantes es del mismo tipo que el del coordinador
                    return f.getEstudiantes().stream()
                            .anyMatch(est -> est.getPrograma() == programaCoord
                                    || (est.getPrograma() != null && est.getPrograma().equals(programaCoord)));
                })
                .toList();

        // 👀 DEBUG opcional
        System.out.println("==== FORMATOS A EN BD PARA PROGRAMA " + programaCoord + " ====");
        for (FormatoA f : filtrados) {
            String progEst = (f.getEstudiantes() == null || f.getEstudiantes().isEmpty())
                    ? "SIN_ESTUDIANTES"
                    : String.valueOf(f.getEstudiantes().get(0).getPrograma());

            String dir = (f.getDirector() == null)
                    ? "SIN_DIRECTOR"
                    : f.getDirector().getEmail();

            System.out.printf("FA id=%s, proyecto=%s, estado=%s, version=%d, programaEst=%s, director=%s%n",
                    f.getId(),
                    f.getNombreProyecto(),
                    f.getEstadoFormatoA(),
                    f.getNroVersion(),
                    progEst,
                    dir
            );
        }

        // 3. Mapear a DTO de resumen
        return filtrados.stream()
                .map(f -> new FormatoAResumenDTO(
                        f.getId(), // UUID
                        f.getNombreProyecto(),
                        f.getDirector() != null
                                ? f.getDirector().getNombres() + " " + f.getDirector().getApellidos()
                                : "(Sin director)",
                        f.getTipoProyecto() != null ? f.getTipoProyecto().toString() : null,
                        f.getFechaSubida(),
                        f.getEstadoFormatoA(),
                        f.getNroVersion(),
                        f.getNombreFormatoA()
                ))
                .toList();
    }
}
