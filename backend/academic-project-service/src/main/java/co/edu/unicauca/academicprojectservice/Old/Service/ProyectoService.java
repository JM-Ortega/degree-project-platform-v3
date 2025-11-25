package co.edu.unicauca.academicprojectservice.Old.Service;

import co.edu.unicauca.academicprojectservice.application.dto.ProyectoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.*;

import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.DocenteDTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.EstudianteDTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.FormatoADTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.ProyectoDTOSend;
import co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.FormatoADTO;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.*;
import co.edu.unicauca.shared.contracts.events.academic.AnteproyectoSinEvaluadoresEvent;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProyectoService {
    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private FormatoARepository formatoARepository;
    @Autowired
    private AnteproyectoRepository anteproyectoRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.routing.projectCreated}")
    private String routingKeyProjectCreated;

    @Value("${messaging.routing.projectUpdated}")
    private String routingKeyProjectUpdated;

    //  =======================  Migrado ===================================
    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        Docente docente = docenteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con correo: " + correo));

        return proyectoRepository.listarInfoPorDocente(docente.getId(), filtro);
    }
    //  ==========================================================



    //En ello... (Laura)
    public int getMaxVersionFormatoA(UUID proyectoId) {
        Integer maxVersion = formatoARepository.findMaxVersionByProyectoId(proyectoId);
        return maxVersion != null ? maxVersion : 0;
    }

    //  =======================  Migrado  ===================================
    private FormatoA getUltimoFormatoA(UUID proyectoId) {
        List<FormatoA> resultados = formatoARepository.findUltimoFormatoA(proyectoId, PageRequest.of(0, 1));
        return resultados.isEmpty() ? null : resultados.get(0);
    }
    //  ==========================================================

    //  =======================  Juan  ===================================
    public EstadoProyecto enforceAutoCancelIfNeeded(UUID proyectoId) {
        int observados = formatoARepository.countByProyectoIdAndEstado(proyectoId, EstadoFormatoA.OBSERVADO);
        if (observados >= 3) {
            proyectoRepository.actualizarEstadoProyecto(proyectoId, EstadoProyecto.FORMATOA_RECHAZADO);
        }
        String est = proyectoRepository.getEstadoProyecto(proyectoId);
        return EstadoProyecto.valueOf(est);
    }
//  ==========================================================
    public boolean canResubmit(UUID proyectoId) {
        String estado = proyectoRepository.getEstadoProyecto(proyectoId);
        if (estado.equalsIgnoreCase(EstadoProyecto.FORMATOA_RECHAZADO.name())) {
            return false;
        }
        int maxVersion = getMaxVersionFormatoA(proyectoId);
        if (maxVersion == 0) return true;
        if (maxVersion >= 3) return false;

        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        if (ultimo == null) return true;

        return ultimo.getEstado() == EstadoFormatoA.OBSERVADO;
    }

    public boolean tieneObservaciones(UUID proyectoId) {
        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        return ultimo != null && ultimo.getEstado() == EstadoFormatoA.OBSERVADO;
    }

    public boolean existeProyecto(UUID proyectoId) {
        return proyectoRepository.existsById(proyectoId);
    }

    public String estadoProyecto(UUID proyectoId) {
        return proyectoRepository.getEstadoProyecto(proyectoId);
    }

    public boolean insertarFormatoAEnProyecto(Long proyectoId, FormatoA formatoA) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un proyecto con id " + proyectoId));

        int ultimaVersion = 0;
        if (proyecto.getFormatosA() != null && !proyecto.getFormatosA().isEmpty()) {
            ultimaVersion = proyecto.getFormatosA().stream()
                    .mapToInt(FormatoA::getNroVersion)
                    .max()
                    .orElse(0);
        }

        formatoA.setNroVersion(ultimaVersion + 1);
        formatoA.setFechaCreacion(LocalDate.now());
        formatoA.setProyecto(proyecto);

        proyecto.addFormato(formatoA);

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        FormatoA formatoGuardado = proyectoGuardado.getFormatosA().stream()
                .filter(f -> f.getNroVersion() == formatoA.getNroVersion())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró el formato recién guardado"));

        // =====================================================
        // Construcción del DTO que se enviará por RabbitMQ
        // =====================================================
        ProyectoDTOSend pDtoSend = new ProyectoDTOSend();
        pDtoSend.setId(proyectoGuardado.getId());
        pDtoSend.setTitulo(proyectoGuardado.getTitulo());
        pDtoSend.setTipoProyecto(proyectoGuardado.getTipoProyecto());
        pDtoSend.setEstado(proyectoGuardado.getEstadoProyecto());

        // ======= Estudiantes DTO =======
        List<EstudianteDTOSend> estudiantes = new ArrayList<>();
        for (Estudiante est : proyectoGuardado.getEstudiantes()) {
            EstudianteDTOSend estDto = new EstudianteDTOSend();
            estDto.setId(est.getId());
            estDto.setPrograma(est.getPrograma());
            estDto.setEmail(est.getCorreo());
            estDto.setNombres(est.getNombres());
            estDto.setApellidos(est.getApellidos());
            estDto.setCelular(est.getCelular());
            estudiantes.add(estDto);
        }
        pDtoSend.setEstudiantes(estudiantes);

        // ======= Director DTO =======
        Docente director = proyectoGuardado.getDirector();
        if (director != null) {
            DocenteDTOSend docDto = new DocenteDTOSend();
            docDto.setId(director.getId());
            docDto.setDepartamento(director.getDepartamento());
            docDto.setEmail(director.getCorreo());
            docDto.setNombres(director.getNombres());
            docDto.setApellidos(director.getApellidos());
            docDto.setCelular(director.getCelular());
            pDtoSend.setDirector(docDto);
        } else {
            pDtoSend.setDirector(null);
        }

        // ======= Formato A DTO (nuevo formato subido) =======
        FormatoADTOSend formatoSend = new FormatoADTOSend();
        formatoSend.setId(formatoGuardado.getId());
        formatoSend.setProyectoId(proyectoId);
        formatoSend.setNroVersion(formatoA.getNroVersion());
        formatoSend.setNombreFormatoA(formatoA.getNombreFormato());
        formatoSend.setFechaSubida(formatoA.getFechaCreacion());
        formatoSend.setBlob(formatoA.getBlob());
        formatoSend.setEstado(formatoA.getEstado());
        pDtoSend.setFormatoA(formatoSend);

        // ======= Envío del mensaje =======
        rabbitTemplate.convertAndSend(mainExchange, routingKeyProjectUpdated, pDtoSend);

        log.info("[RabbitMQ] Nueva versión de Formato A enviada a la cola: {} (Proyecto ID: {}, Versión: {})",
                routingKeyProjectUpdated, proyectoId, formatoA.getNroVersion());

        // ======= Notificación a coordinadores =======
        try {
            Estudiante estudiante = proyectoGuardado.getEstudiantes().isEmpty()
                    ? null : proyectoGuardado.getEstudiantes().get(0);

            Docente docente = proyectoGuardado.getDirector();

            List<String> destinatarios = new ArrayList<>();
            List<String> celulares = new ArrayList<>();

            List<Coordinador> coordinadores = coordinadorRepository.findAll();
            if (coordinadores != null && !coordinadores.isEmpty()) {
                for (Coordinador coordinador : coordinadores) {
                    if (coordinador.getCorreo() != null && !coordinador.getCorreo().isEmpty()) {
                        destinatarios.add(coordinador.getCorreo());
                    }
                    if (coordinador.getCelular() != null && !coordinador.getCelular().isEmpty()) {
                        celulares.add(coordinador.getCelular());
                    }
                }
            }

            String subject = "Formato A actualizado (nueva versión)";
            String message = String.format("""
                            Se ha subido una nueva versión del Formato A en el proyecto:
                            📘 %s
                            👩‍🎓 Estudiante: %s %s
                            👨‍🏫 Director: %s %s
                            🔢 Versión: %d
                            📅 Fecha de subida: %s
                            """,
                    proyectoGuardado.getTitulo(),
                    estudiante != null ? estudiante.getNombres() : "Desconocido",
                    estudiante != null ? estudiante.getApellidos() : "",
                    docente != null ? docente.getNombres() : "Desconocido",
                    docente != null ? docente.getApellidos() : "",
                    formatoGuardado.getNroVersion(),
                    formatoGuardado.getFechaCreacion()
            );

            NotificationEvent notificationEvent = new NotificationEvent(
                    "project.created",
                    destinatarios,
                    subject,
                    message,
                    celulares,
                    OffsetDateTime.now()
            );

            rabbitTemplate.convertAndSend(
                    mainExchange,
                    "notification.send.project.created",
                    notificationEvent
            );

            co.edu.unicauca.academicprojectservice.Old.Repository.ProyectoService.log.info("📨 Notificación enviada: {}", notificationEvent.getSubject());

        } catch (Exception e) {
            co.edu.unicauca.academicprojectservice.Old.Repository.ProyectoService.log.error("Error al enviar notificación: {}", e.getMessage(), e);
        }

        return true;
    }

    public FormatoA obtenerUltimoFormatoAConObservaciones(UUID proyectoId) {
        List<FormatoA> resultados = formatoARepository.findUltimoFormatoAObservado(
                proyectoId, EstadoFormatoA.OBSERVADO, PageRequest.of(0, 1)
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public void actualizarFormatoA(UUID proyectoId, EstadoFormatoA estado) {
        formatoARepository.actualizarFormatoA(proyectoId, estado);
    }

    public int countProyectosByEstadoYTipo(TipoProyecto tipo, EstadoProyecto estado, String correoDocente) {
        return proyectoRepository.countProyectosByEstadoYTipo(tipo, estado, correoDocente);
    }

    public List<AnteproyectoDTO> listarAnteproyectosDocente(String correo, String filtro) {
        return anteproyectoRepository.listarAnteproyectosPorCorreoDocente(correo, filtro);
    }

    public void asociarAnteproyectoAProyecto(String correo, AnteproyectoDTO dto) {

        var proyecto = proyectoRepository.findByEstudiantesCorreoIgnoreCaseAndEstadoProyecto(correo, EstadoProyecto.EN_REVISION_ANTEPROYECTO)
                .orElseThrow(() -> new EntityNotFoundException("El estudiante no tiene un proyecto asociado."));

        Anteproyecto anteproyecto = new Anteproyecto();
        anteproyecto.setDescripcion(dto.getDescripcion());
        anteproyecto.setTitulo(dto.getTitulo());
        anteproyecto.setNombreArchivo(dto.getNombreArchivo());
        anteproyecto.setBlob(dto.getBlob());
        anteproyecto.setFechaCreacion(LocalDate.now());
        //anteproyecto.setProyecto(proyecto);

        anteproyectoRepository.save(anteproyecto);

        proyecto.setAnteproyecto(anteproyecto);
        proyectoRepository.save(proyecto);

        // --- Enviar evento de actualización del proyecto ---
        ProyectoDTOSend pDtoSend = new ProyectoDTOSend();
        pDtoSend.setId(proyecto.getId());
        pDtoSend.setTitulo(proyecto.getTitulo());
        pDtoSend.setTipoProyecto(proyecto.getTipoProyecto());
        pDtoSend.setEstado(proyecto.getEstadoProyecto());

        Docente director = proyecto.getDirector();
        if (director != null) {
            DocenteDTOSend docDto = new DocenteDTOSend();
            docDto.setId(director.getId());
            docDto.setCelular(director.getCelular());
            docDto.setDepartamento(director.getDepartamento());
            docDto.setNombres(director.getNombres());
            docDto.setApellidos(director.getApellidos());
            docDto.setEmail(director.getCorreo());
            pDtoSend.setDirector(docDto);
        }

        // Estudiantes
        List<EstudianteDTOSend> estudiantes = new ArrayList<>();
        for (Estudiante e : proyecto.getEstudiantes()) {
            EstudianteDTOSend estDto = new EstudianteDTOSend();
            estDto.setId(e.getId());
            estDto.setNombres(e.getNombres());
            estDto.setApellidos(e.getApellidos());
            estDto.setCelular(e.getCelular());
            estDto.setPrograma(e.getPrograma());
            estDto.setEmail(e.getCorreo());
            estudiantes.add(estDto);
        }
        pDtoSend.setEstudiantes(estudiantes);

        // Anteproyecto asociado -> evento interno (DeptHead)
        AnteproyectoSinEvaluadoresEvent anteEvent = new AnteproyectoSinEvaluadoresEvent(
                proyecto.getId(),
                anteproyecto.getId(),
                anteproyecto.getTitulo(),
                anteproyecto.getDescripcion(),
                anteproyecto.getFechaCreacion(),
                proyecto.getEstudiantes().get(0).getCorreo(),
                proyecto.getDirector() != null ? proyecto.getDirector().getCorreo() : null,
                proyecto.getDirector() != null ? proyecto.getDirector().getDepartamento().name() : "DESCONOCIDO"
        );

        rabbitTemplate.convertAndSend(mainExchange, "academic.anteproyecto.created", anteEvent);
        log.info("[RabbitMQ] AnteproyectoSinEvaluadoresEvent publicado -> exchange={}, rk={}, payload={}",
                mainExchange, "academic.anteproyecto.created", anteEvent);

        // Además: publicar actualización de proyecto y notificar a jefes (merge de main)
        rabbitTemplate.convertAndSend(mainExchange, routingKeyProjectUpdated, pDtoSend);
        log.info("[RabbitMQ] Proyecto actualizado enviado a la cola: {}", routingKeyProjectUpdated);

        try {
            Estudiante estudiante = proyecto.getEstudiantes().isEmpty()
                    ? null : proyecto.getEstudiantes().get(0);
            Docente docente = proyecto.getDirector();

            List<String> destinatarios = new ArrayList<>();
            List<String> celulares = new ArrayList<>();

            List<JefeDeDepartamento> jefes = jefeDeDepartamentoRepository.findAll();
            if (jefes != null && !jefes.isEmpty()) {
                for (JefeDeDepartamento jefe : jefes) {
                    if (jefe.getCorreo() != null && !jefe.getCorreo().isEmpty()) {
                        destinatarios.add(jefe.getCorreo());
                    }
                    if (jefe.getCelular() != null && !jefe.getCelular().isEmpty()) {
                        celulares.add(jefe.getCelular());
                    }
                }
            }

            String subject = "Nuevo anteproyecto asociado a un proyecto en trámite";
            String message = String.format("""
                            Se ha asociado un nuevo anteproyecto al proyecto:
                            📘 %s
                            👩‍🎓 Estudiante: %s %s
                            👨‍🏫 Director: %s %s
                            🗓️ Fecha de creación del anteproyecto: %s
                            📝 Descripción: %s
                            """,
                    proyecto.getTitulo(),
                    estudiante != null ? estudiante.getNombres() : "Desconocido",
                    estudiante != null ? estudiante.getApellidos() : "",
                    docente != null ? docente.getNombres() : "Desconocido",
                    docente != null ? docente.getApellidos() : "",
                    anteproyecto.getFechaCreacion(),
                    anteproyecto.getDescripcion()
            );

            NotificationEvent notificationEvent = new NotificationEvent(
                    "project.anteproyecto.associated",
                    destinatarios,
                    subject,
                    message,
                    celulares,
                    OffsetDateTime.now()
            );

            rabbitTemplate.convertAndSend(
                    mainExchange,
                    "notification.send.project.anteproyecto.associated",
                    notificationEvent
            );

            log.info("📨 Notificación de asociación de anteproyecto enviada correctamente a jefes de departamento.");

        } catch (Exception e) {
            log.error("Error al enviar notificación a jefes de departamento: {}", e.getMessage(), e);
        }
    }

    public AnteproyectoDTO obtenerAnteproyecto(long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el proyecto con ID: " + proyectoId));

        Anteproyecto anteproyecto = proyecto.getAnteproyecto();
        if (anteproyecto == null) {
            throw new EntityNotFoundException("El proyecto no tiene un anteproyecto asociado");
        }

        AnteproyectoDTO dto = new AnteproyectoDTO();
        dto.setId(anteproyecto.getId());
        dto.setNombreArchivo(anteproyecto.getNombreArchivo());
        dto.setDescripcion(anteproyecto.getDescripcion());
        dto.setTitulo(anteproyecto.getTitulo());
        dto.setBlob(anteproyecto.getBlob());
        dto.setFechaCreacion(anteproyecto.getFechaCreacion());

        if (proyecto.getEstudiantes() != null && !proyecto.getEstudiantes().isEmpty()) {
            dto.setEstudianteNombre(proyecto.getEstudiantes().get(0).getNombres());
            dto.setEstudianteCorreo(proyecto.getEstudiantes().get(0).getCorreo());
        }

        return dto;
    }

    public FormatoADTO obtenerUltimoFormatoAConObservaciones(long proyectoId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el proyecto con ID: " + proyectoId));

        List<FormatoA> observados = proyecto.getFormatosA().stream()
                .filter(f -> f.getEstado() == EstadoFormatoA.OBSERVADO)
                .sorted((f1, f2) -> f2.getFechaCreacion().compareTo(f1.getFechaCreacion()))
                .collect(Collectors.toList());

        if (observados.isEmpty()) {
            throw new EntityNotFoundException("No hay formatos A con observaciones para este proyecto");
        }

        FormatoA ultimo = observados.get(0);

        FormatoADTO dto = new FormatoADTO();
        dto.setNombreFormato(ultimo.getNombreFormato());
        dto.setBlob(ultimo.getBlob());
        dto.setFechaCreacion(ultimo.getFechaCreacion());
        dto.setEstado(ultimo.getEstado());

        return dto;
    }
}
