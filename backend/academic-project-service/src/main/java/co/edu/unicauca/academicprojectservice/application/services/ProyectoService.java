package co.edu.unicauca.academicprojectservice.application.services;


import co.edu.unicauca.academicprojectservice.application.dto.FormatoADTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoEstudianteDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;

import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;

import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
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
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final DbPortDocente dbPortDocente;
    private final DbPortProyecto dbPortProyecto;
    private final DbPortEstudiante dbPortEstudiante;


    // ======================  nuevo

    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        DocenteId docenteId = dbPortDocente.findIdByCorreo(correo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Docente no encontrado con correo: " + correo
                ));

        return dbPortProyecto.listarInfoPorDocente(docenteId.value(), filtro);
    }

    public List<ProyectoEstudianteDTO> listarPorEstudiante(String correo) {
        return dbPortProyecto.listarProyectosPorCorreoEstudiante(correo);
    }

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
            String nombreFormato = dto.getFormatoA().getNombreFormato();
            byte[] archivoFormato = dto.getFormatoA().getBlob();
            proyecto.agregarFormatoAInicial(nombreFormato, archivoFormato);
        }

        dbPortProyecto.guardarProyecto(proyecto);
    }

    public EstadoProyecto enforceAutoCancelIfNeeded(UUID proyectoId) {
        Proyecto proyecto = dbPortProyecto.buscarPorId(proyectoId)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));

        return proyecto.getEstadoProyecto();
    }


    public FormatoADTO obtenerUltimoFormatoAConObservaciones(UUID proyectoId) {
        Proyecto proyecto = dbPortProyecto.findById(proyectoId)
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


//========================


/**

 // ===============================

 // ==================================== inicio migrado ======================================

 private FormatoA getUltimoFormatoA(UUID proyectoId) {
 return dbPort.obtenerUltimoFormatoA(proyectoId);
 }


 public EstadoProyecto enforceAutoCancelIfNeeded(UUID proyectoId) {

 int observados = dbPort.contarFormatoAObservados(proyectoId);

 if (observados >= 3) {
 dbPort.actualizarEstadoProyecto(proyectoId, EstadoProyecto.FORMATOA_RECHAZADO);
 }

 return dbPort.obtenerEstadoProyecto(proyectoId);
 }


 // ==================================== fin migrado ======================================


 public int getMaxVersionFormatoA(UUID proyectoId) {
 return dbPort.getMaxVersionFormatoA(proyectoId);
 }


 public void crearProyectoConArchivos(ProyectoDTO dto) {
 List<EstudianteId> estudiantes = dto.getEstudiantes().stream()
 .map(correo -> dbPort.buscarEstudianteIdPorCorreo(correo)
 .orElseThrow(() -> new IllegalArgumentException(
 "No existe un estudiante con el correo: " + correo)))
 .toList();


 DocenteId docenteId = dbPort.buscarDocenteIdPorCorreo(dto.getDirector())
 .orElseThrow(() -> new IllegalArgumentException("No existe un docente con ese correo"));

 Proyecto proyecto = new Proyecto(dto.getTitulo(), estudiantes, docenteId, dto.getTipoProyecto());

 proyecto.agregarFormatoAInicial(dto.getFormatoA().getNombreFormato(), dto.getFormatoA().getBlob());

 if (dto.getCartaLaboral() != null) {
 proyecto.adjuntarCartaLaboral(dto.getCartaLaboral());
 }

 Proyecto proyectoGuardado = dbPort.guardarProyecto(proyecto);

 messagingPort.publicarMensajeRMQ(proyectoGuardado);
 notificationPort.notificarACoordinadores(proyectoGuardado);
 }



 */
}
