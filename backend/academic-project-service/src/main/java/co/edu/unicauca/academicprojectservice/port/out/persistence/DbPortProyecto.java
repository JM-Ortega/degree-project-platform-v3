package co.edu.unicauca.academicprojectservice.port.out.persistence;

import co.edu.unicauca.academicprojectservice.application.dto.*;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DbPortProyecto {


    // ================== ESTUDIANTE ==================

    Optional<EstudianteId> buscarEstudianteIdPorCorreo(String correo);

    Optional<EstudianteDTO> obtenerEstudiantePorId(UUID id);


    // =================== DOCENTE ====================

    Optional<DocenteId> buscarDocenteIdPorCorreo(String correo);

    Optional<DocenteInfoDTO> obtenerDocenteInfoPorId(UUID id);

    Optional<DocenteDTO> obtenerDocentePorCorreo(String correo);


    // =================== PROYECTO ===================

//== nuevo
    List<ProyectoInfoDTO> listarInfoPorDocente(UUID docenteId, String filtro);

    List<ProyectoEstudianteDTO> listarProyectosPorCorreoEstudiante(String correo);
    Optional<Proyecto> buscarPorId(UUID proyectoId);

// ==========

    Proyecto guardarProyecto(Proyecto proyecto);

    List<ProyectoInfoDTO> listarInfoProyectosPorCorreoDocente(String correoDocente, String filtro);

    int getMaxVersionFormatoA(UUID proyectoId);

    void actualizarEstadoProyecto(UUID proyectoId, EstadoProyecto estado);

    EstadoProyecto obtenerEstadoProyecto(UUID proyectoId);

    Proyecto findById(UUID proyectoId);


    // fomatoA
    FormatoA obtenerUltimoFormatoA(UUID proyectoId);

    int contarFormatoAObservados(UUID proyectoId);

    Optional<FormatoADTO> obtenerUltimoFormatoAObservadoDTO(UUID proyectoId);


}
