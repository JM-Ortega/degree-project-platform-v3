package co.edu.unicauca.academicprojectservice.port.out.persistence;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Estudiante;
import co.edu.unicauca.academicprojectservice.application.dto.*;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DbPortProyecto {


    // ================== ESTUDIANTE ==================

    Optional<EstudianteId> buscarEstudianteIdPorCorreo(String correo);

    Estudiante obtenerEstudiantePorId(UUID id);


    // =================== DOCENTE ====================

    Optional<DocenteId> buscarDocenteIdPorCorreo(String correo);

    Docente obtenerDocenteInfoPorId(UUID id);

    Optional<DocenteDTO> obtenerDocentePorCorreo(String correo);


    // =================== PROYECTO ===================

//== nuevo
   // List<ProyectoInfoDTO> listarInfoPorDocente(UUID docenteId, String filtro);

    List<ProyectoEstudianteDTO> listarProyectosPorCorreoEstudiante(String correo);
    //No esta implementada
   // Optional<Proyecto> buscarPorId(UUID proyectoId);

    Proyecto buscarPorCorreo(String correo);


  //  void guardarAnteproyecto (String correo, AnteproyectoDTO dto);

// ==========

    // Se usa para crear el proyecto en el frontend
    void guardarProyecto(Proyecto proyecto);

    List<ProyectoInfoDTO> listarInfoProyectosPorCorreoDocente(String correoDocente, String filtro);

    int getMaxVersionFormatoA(UUID proyectoId);

    void actualizarEstadoProyecto(UUID proyectoId, EstadoProyecto estado);

    EstadoProyecto obtenerEstadoProyecto(UUID proyectoId);

    Proyecto findById(UUID proyectoId);

    // fomatoA
    co.edu.unicauca.academicprojectservice.domain.model.FormatoA obtenerUltimoFormatoA(UUID proyectoId);

    int contarFormatoAObservados(UUID proyectoId);

//    Optional<FormatoADTO> obtenerUltimoFormatoAObservadoDTO(UUID proyectoId);

    int countProyectosByEstadoYTipo(TipoProyecto tipo, EstadoProyecto estado, String correoDocente);

    //Anteproyecto
    List<AnteproyectoDTO> listarAnteproyectosPorCorreoDocente(String correo, String filtro);

    AnteproyectoDTO obtenerAnteproyecto (UUID proyectoId);
}
