package co.edu.unicauca.academicprojectservice.application.services;


import co.edu.unicauca.academicprojectservice.application.dto.*;

import co.edu.unicauca.academicprojectservice.application.exceptions.ProyectoNoEncontradoException;
import co.edu.unicauca.academicprojectservice.domain.model.*;

import co.edu.unicauca.academicprojectservice.port.out.messaging.MessagingPort;
import co.edu.unicauca.academicprojectservice.port.out.notification.NotificationPort;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortDocente;

import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortEstudiante;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortProyecto;
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
                                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + correo))
                )
                .toList();

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
        messagingPort.publicarMensajeRMQ(proyecto);
        notificationPort.notificarACoordinadores(proyecto);
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

        proyecto.agregarNuevaVersionFormatoA(
                formatoA.nombreFormato(),
                formatoA.blob()
        );

        dbPortProyecto.guardarProyecto(proyecto);
    }

    public void asociarAnteproyectoAProyecto(String correo, AnteproyectoDTO dto) {
        Proyecto proyecto = dbPortProyecto.buscarPorCorreo(correo);
        proyecto.crearAnteproyecto(dto.getNombreArchivo(), dto.getDescripcion(), dto.getTitulo(), dto.getBlob());
        dbPortProyecto.guardarProyecto(proyecto);
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
}

