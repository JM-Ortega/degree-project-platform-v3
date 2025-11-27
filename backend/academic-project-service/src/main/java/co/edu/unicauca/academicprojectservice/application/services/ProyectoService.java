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

    // Este es el que se llama por el frontenda para crear un proyecto
    public void crearProyectoConArchivos(ProyectoDTO dto) {

        DocenteId directorId = dbPortDocente.findIdByCorreo(dto.getDirector())
                .orElseThrow(() -> new IllegalArgumentException("Director no encontrado: " + dto.getDirector()));

        List<EstudianteId> estudiantesId = dto.getEstudiantes().stream()
                .map(correo ->
                        dbPortEstudiante.findIdByCorreo(correo)
                                .orElseThrow(() -> new IllegalArgumentException("Este correo no pertenece a un estudiante: " + correo))
                )
                .toList();

        // Validación mapeada del front validar que el director no tenga mas de 7 proyectos activos
        int numeroProyectos = countProyectosByEstadoYTipo(dto.getTipoProyecto(), dto.getEstadoProyecto(), dto.getDirector());
        if (numeroProyectos > 7)
            throw new IllegalStateException("El docente alcanzó el límite de 7 proyectos en curso");

        // Validacion mapeada del front: Que el estudiante no tenga otro proyecto en curso
        for (String correo : dto.getEstudiantes()) {
            if (dbPortEstudiante.proyectoActivo(correo))
                throw new IllegalStateException("El estudiante ya tiene un proyecto en curso");
        }

        Proyecto proyecto = Proyecto.crear(
                dto.getTitulo(),
                estudiantesId,
                directorId,
                dto.getTipoProyecto()
        );

        if (dto.getTipoProyecto().equals(TipoProyecto.PRACTICA_PROFESIONAL) &&
                dto.getCartaLaboral() != null) {
            proyecto.adjuntarCartaLaboral(dto.getCartaLaboral());
        }

        if (dto.getFormatoA() != null) {
            String nombreFormato = dto.getFormatoA().nombreFormato();
            byte[] archivoFormato = dto.getFormatoA().blob();
            proyecto.agregarFormatoAInicial(nombreFormato, archivoFormato);
        }

        dbPortProyecto.guardarProyecto(proyecto);
        messagingPort.publicarProyectoCreado(proyecto);
        notificationPort.notificarProyectoCreado(proyecto);
    }

    // ======================  nuevo
    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        DocenteId docenteId = dbPortDocente.findIdByCorreo(correo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Docente no encontrado con correo: " + correo
                ));

        return dbPortProyecto.listarInfoProyectosPorCorreoDocente(correo, filtro);
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

        List<FormatoA> observados = proyecto.getFormatosA().stream()
                .filter(f -> f.getEstado() == EstadoFormatoA.OBSERVADO)
                .sorted((f1, f2) -> f2.getFechaCreacion().compareTo(f1.getFechaCreacion()))
                .toList();

        if (observados.isEmpty()) {
            throw new EntityNotFoundException("No hay formatos A con observaciones para este proyecto");
        }

        FormatoA ultimo = observados.getFirst();


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
        //TODO Falta enviar mensaje al coordinador para que rechace o apruebe
        // el formato cuando se actualice el formato aqui
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
        if (estado == null ||
                (estado != EstadoProyecto.FORMATOA_RECHAZADO
                        && estado != EstadoProyecto.ANTEPROYECTO_ENVIADO)) {
            return false;
        }

        int maxVersion = getMaxVersionFormatoA(proyectoId);
        if (maxVersion == 0) return true;
        if (maxVersion >= 3) return false;


        FormatoA ultimo = getUltimoFormatoA(proyectoId);

        return ultimo != null && ultimo.getEstado() == EstadoFormatoA.OBSERVADO;
    }


    private FormatoA getUltimoFormatoA(UUID proyectoId) {
        return dbPortProyecto.obtenerUltimoFormatoA(proyectoId);
    }


    public boolean tieneObservaciones(UUID proyectoId) {

        FormatoA ultimo = getUltimoFormatoA(proyectoId);
        return ultimo != null && ultimo.getEstado() == EstadoFormatoA.OBSERVADO;
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

    public void registrarResultadoRevisionFormatoADesdeEvento(UUID proyectoId, EstadoFormatoA nuevoEstado) {
        Proyecto proyecto = dbPortProyecto.findById(proyectoId);
        if (proyecto == null) {
            throw new ProyectoNoEncontradoException(proyectoId);
        }

        proyecto.registrarResultadoRevisionFormatoA(nuevoEstado);

        dbPortProyecto.guardarProyecto(proyecto);
    }



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

        // Mapear correos -> DocenteId usando el puerto de persistencia
        var evaluadores = event.evaluadores().stream()
                .map(correo -> dbPortDocente.findIdByCorreo(correo)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe docente con correo: " + correo)))
                .toList();

        // Usar lógica del dominio
        proyecto.getAnteproyecto().asignarEvaluadores(evaluadores);
        proyecto.marcarAnteproyectoEnRevision();

        dbPortProyecto.guardarProyecto(proyecto);
    }
}

