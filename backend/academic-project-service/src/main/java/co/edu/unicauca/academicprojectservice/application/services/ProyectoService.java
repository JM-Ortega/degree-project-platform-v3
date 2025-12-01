package co.edu.unicauca.academicprojectservice.application.services;


import co.edu.unicauca.academicprojectservice.application.dto.*;
import co.edu.unicauca.academicprojectservice.application.exceptions.ProyectoNoEncontradoException;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.port.out.messaging.MessagingPort;
import co.edu.unicauca.academicprojectservice.port.out.notification.NotificationPort;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortDocente;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortEstudiante;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortProyecto;
import co.edu.unicauca.shared.contracts.events.departmenthead.AnteproyectoConEvaluadoresEvent;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final DbPortDocente dbPortDocente;
    private final DbPortProyecto dbPortProyecto;
    private final DbPortEstudiante dbPortEstudiante;
    private final MessagingPort messagingPort;
    private final NotificationPort notificationPort;

    // Este es el que se llama por el frontend para crear un proyecto
    public void crearProyectoConArchivos(ProyectoDTO dto) {

        // ==========================
        //  VALIDACIÓN DE ESTUDIANTES
        // ==========================
        if (dto.getEstudiantes() == null || dto.getEstudiantes().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un estudiante");
        }

        // Limpia correos: quita null, vacíos, espacios
        List<String> correosLimpios = dto.getEstudiantes().stream()
                .map(c -> c == null ? "" : c.trim())
                .filter(c -> !c.isBlank())
                .toList();

        if (correosLimpios.isEmpty()) {
            throw new IllegalArgumentException("La lista de correos de estudiantes no contiene valores válidos");
        }

        // ========================================
        //  VALIDACIÓN DEL DIRECTOR (ya existe)
        // ========================================
        DocenteId directorId = dbPortDocente.findIdByCorreo(dto.getDirector())
                .orElseThrow(() -> new IllegalArgumentException("Director no encontrado: " + dto.getDirector()));

        // ========================================
        //  VALIDAR QUE CADA ESTUDIANTE EXISTA
        // ========================================
        List<EstudianteId> estudiantesId = correosLimpios.stream()
                .map(correo ->
                        dbPortEstudiante.findIdByCorreo(correo)
                                .orElseThrow(() ->
                                        new IllegalArgumentException("Este correo no pertenece a un estudiante: " + correo)
                                )
                )
                .toList();

        // ========================================
        //  VALIDACIÓN DEL DIRECTOR (> 7 proyectos)
        // ========================================
        int numeroProyectos = countProyectosByEstadoYTipo(
                dto.getTipoProyecto(),
                dto.getEstadoProyecto(),
                dto.getDirector()
        );

        if (numeroProyectos > 7) {
            throw new IllegalStateException("El docente alcanzó el límite de 7 proyectos en curso");
        }

        // ========================================
        //  VALIDACIÓN DEL ESTUDIANTE (no duplicado)
        // ========================================
        for (String correo : correosLimpios) {
            if (dbPortEstudiante.proyectoActivo(correo)) {
                throw new IllegalStateException("El estudiante ya tiene un proyecto en curso");
        }
        }

        // ========================================
        //  VALIDACIÓN DEL FORMATO A (OBLIGATORIO)
        // ========================================
        if (dto.getFormatoA() == null
                || dto.getFormatoA().blob() == null
                || dto.getFormatoA().blob().length == 0
                || dto.getFormatoA().nombreFormato() == null
                || dto.getFormatoA().nombreFormato().isBlank()) {

            throw new IllegalArgumentException("Debe adjuntar el Formato A inicial del proyecto.");
        }

        // ========================================
        //  CREACIÓN DEL PROYECTO (DOMINIO)
        // ========================================
        Proyecto proyecto = Proyecto.crear(
                dto.getTitulo(),
                estudiantesId,
                directorId,
                dto.getTipoProyecto()
        );

        // Adjuntar carta laboral si aplica
        if (dto.getTipoProyecto().equals(TipoProyecto.PRACTICA_PROFESIONAL)
                && dto.getCartaLaboral() != null
                && dto.getCartaLaboral().getBlob() != null) {

            proyecto.adjuntarCartaLaboral(dto.getCartaLaboral().getBlob());
        }

        // Adjuntar Formato A inicial (ya validado como obligatorio)
        proyecto.agregarFormatoAInicial(
                dto.getFormatoA().nombreFormato(),
                dto.getFormatoA().blob()
        );

        // Guardar en BD
        dbPortProyecto.guardarProyecto(proyecto);

        // Publicar eventos
        messagingPort.publicarProyectoCreado(proyecto);
        notificationPort.notificarProyectoCreado(proyecto);
    }


    // ======================  nuevo
    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        try {
            log.info("Listando proyectos para docente {} con filtro '{}'", correo, filtro);
        return dbPortProyecto.listarInfoProyectosPorCorreoDocente(correo, filtro);
        } catch (EntityNotFoundException e) {
            throw e; // la atrapará el controller y devuelve 404
        } catch (Exception ex) {
            log.error("Error al listar proyectos del docente {}", correo, ex);
            throw new IllegalStateException("Error al listar proyectos del docente " + correo, ex);
    }
    }


    public List<ProyectoEstudianteDTO> listarPorEstudiante(String correo) {
        return dbPortProyecto.listarProyectosPorCorreoEstudiante(correo);
    }

    public EstadoProyecto enforceAutoCancelIfNeeded(UUID proyectoId) {
        Proyecto proyecto = dbPortProyecto.findById(proyectoId);
        return proyecto.getEstadoProyecto();
    }


    public FormatoADTO obtenerUltimoFormatoAConObservaciones(UUID proyectoId) {
        Proyecto proyecto = dbPortProyecto.findById(proyectoId);
        if (proyecto == null) {
            throw new ProyectoNoEncontradoException(proyectoId);
        }

        FormatoA ultimo = proyecto.getFormatosA().stream()
                .filter(f -> f.getEstado() == EstadoFormatoA.OBSERVADO
                        || f.getEstado() == EstadoFormatoA.APROBADO)
                .max(Comparator.comparingInt(FormatoA::getNroVersion))
                .orElse(null);

        if (ultimo == null) {
            return null;
        }

        return new FormatoADTO(
                ultimo.getNombreFormato(),
                ultimo.getBlob(),
                ultimo.getNroVersion(),
                ultimo.getFechaCreacion(),
                ultimo.getEstado()
        );
    }


    public void insertarFormatoAEnProyecto(UUID proyectoId, FormatoADTO formatoA) {
        Proyecto proyecto = dbPortProyecto.findById(proyectoId);
        if (proyecto == null) {
            throw new ProyectoNoEncontradoException(proyectoId);
        }

        // Validacion mapeada del front para que no se exceda la version del formato a
        int maxVersion = getMaxVersionFormatoA(proyectoId);
        if (maxVersion >= 3)
            throw new IllegalStateException("Se alcanzó el máximo de 3 versiones del Formato A");

        proyecto.agregarNuevaVersionFormatoA(
                formatoA.nombreFormato(),
                formatoA.blob()
        );

        dbPortProyecto.guardarProyecto(proyecto);
        notificationPort.notificarFormatoActualizado(proyecto);
        messagingPort.publicarFormatoActualizado(proyecto);
    }

    public void asociarAnteproyectoAProyecto(String correo, AnteproyectoDTO dto) {
        // Validaciones mapeadas del front
        dbPortEstudiante.findIdByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante con el correo ingresado no existe"));

        if (!dbPortEstudiante.proyectoActivo(correo)) {
            throw new IllegalArgumentException("El estudiante no tiene un proyecto activo");
        }

        if (!dbPortEstudiante.formatoAAprobadoPorCorreo(correo, EstadoFormatoA.APROBADO)) {
            throw new IllegalArgumentException("El Formato A del estudiante no está en estado APROBADO");
        }

        Proyecto proyecto = dbPortProyecto.buscarPorCorreo(correo);

        if (proyecto.getAnteproyecto() != null) {
            throw new IllegalArgumentException("El estudiante ya tiene un anteproyecto asociado");
        }

        proyecto.crearAnteproyecto(dto.getNombreArchivo(), dto.getDescripcion(), dto.getTitulo(), dto.getBlob());
        dbPortProyecto.guardarProyecto(proyecto);

        // Crear unos especificos para el anteproyecto
        //messagingPort.publicarProyectoCreado(proyecto);
        messagingPort.publicarAnteproyectoSinEvaluadores(proyecto);

        notificationPort.notificarAJefes(proyecto);
    }

    public int getMaxVersionFormatoA(UUID proyectoId) {
        return dbPortProyecto.getMaxVersionFormatoA(proyectoId);
    }

    public boolean canResubmit(UUID proyectoId) {
        EstadoProyecto estado = dbPortProyecto.obtenerEstadoProyecto(proyectoId);
        if (estado == null) {
            return false;
        }

        // 1. Nunca permitir nueva versión si ya está en estados finales o de anteproyecto
        if (estado == EstadoProyecto.FORMATOA_RECHAZADO
                || estado == EstadoProyecto.FORMATOA_ACEPTADO
                || estado == EstadoProyecto.ANTEPROYECTO_ENVIADO
                || estado == EstadoProyecto.EN_REVISION_ANTEPROYECTO) {
            return false;
        }

        // 2. Solo permitir mientras el proyecto esté en segunda o tercera revisión de Formato A
        if (estado != EstadoProyecto.SEGUNDA_REVISION_FORMATOA
                && estado != EstadoProyecto.TERCERA_REVISION_FORMATOA) {
            return false;
        }

        // 3. Validar versiones
        int maxVersion = getMaxVersionFormatoA(proyectoId);

        // Si por alguna razón no hay Formato A, no tiene sentido permitir resubir
        if (maxVersion == 0) {
            return false;
        }

        // Máximo 3 versiones
        if (maxVersion >= 3) {
            return false;
        }

        // 4. El último Formato A debe estar en estado OBSERVADO
        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        return ultimo != null && ultimo.getEstado() == EstadoFormatoA.OBSERVADO;
    }


    private FormatoA getUltimoFormatoA(UUID proyectoId) {
        return dbPortProyecto.obtenerUltimoFormatoA(proyectoId);
    }


    public boolean tieneObservaciones(UUID proyectoId) {
        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        if (ultimo == null) {
            return false;
        }

        return ultimo.getEstado() == EstadoFormatoA.OBSERVADO
                || ultimo.getEstado() == EstadoFormatoA.APROBADO;
    }

    public int countProyectosByEstadoYTipo(TipoProyecto tipo, EstadoProyecto estado, String correoDocente) {
        return dbPortProyecto.countProyectosByEstadoYTipo(tipo, estado, correoDocente);
    }

    public List<AnteproyectoDTO> listarAnteproyectosDocente(String correo, String filtro) {
        return dbPortProyecto.listarAnteproyectosPorCorreoDocente(correo, filtro);
    }

    public AnteproyectoDTO obtenerAnteproyecto (UUID proyectoId) {
        return dbPortProyecto.obtenerAnteproyecto (proyectoId);
    }


    public void registrarResultadoRevisionFormatoADesdeEvento(UUID proyectoId, EstadoFormatoA nuevoEstado, byte[] archivoRevisado, String nombreArchivoRevisado) {
        Proyecto proyecto = dbPortProyecto.findById(proyectoId);
        if (proyecto == null) {
            throw new ProyectoNoEncontradoException(proyectoId);
        }

        proyecto.registrarResultadoRevisionFormatoA(nuevoEstado, archivoRevisado, nombreArchivoRevisado);

        dbPortProyecto.guardarProyecto(proyecto);
    }


    @Transactional
    public void asignarEvaluadoresAnteproyectoDesdeDeptHead(AnteproyectoConEvaluadoresEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }

        Proyecto proyecto = dbPortProyecto.findById(event.proyectoId());
        if (proyecto == null) {
            throw new ProyectoNoEncontradoException(event.proyectoId());
        }

        if (proyecto.getAnteproyecto() == null) {
            throw new IllegalStateException("El proyecto no tiene anteproyecto asociado");
        }

        if (!proyecto.getAnteproyecto().getId().equals(event.anteproyectoId())) {
            throw new IllegalStateException("El anteproyecto del evento no coincide con el anteproyecto del proyecto");
        }

        var evaluadores = event.evaluadores().stream()
                .map(correo -> dbPortDocente.findIdByCorreo(correo)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe docente con correo: " + correo)))
                .toList();

        proyecto.getAnteproyecto().asignarEvaluadores(evaluadores);
        proyecto.marcarAnteproyectoEnRevision();

        dbPortProyecto.guardarProyecto(proyecto);
    }
}

