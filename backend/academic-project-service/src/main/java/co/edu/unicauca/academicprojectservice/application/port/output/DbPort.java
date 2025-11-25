package co.edu.unicauca.academicprojectservice.application.port.output;

import co.edu.unicauca.academicprojectservice.application.dto.DocenteDTO;
import co.edu.unicauca.academicprojectservice.application.dto.DocenteInfoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.EstudianteDTO;
import co.edu.unicauca.academicprojectservice.application.dto.FormatoADTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DbPort {
    // ================== ESTUDIANTE ==================

    Optional<EstudianteId> buscarEstudianteIdPorCorreo(String correo);

    Optional<EstudianteDTO> obtenerEstudiantePorId(UUID id);

    
    
    
    // =================== DOCENTE ====================

    Optional<DocenteId> buscarDocenteIdPorCorreo(String correo);

    Optional<DocenteInfoDTO> obtenerDocenteInfoPorId(UUID id);

    Optional<DocenteDTO> obtenerDocentePorCorreo(String correo);

    
    
    
    
    
    
    // =================== PROYECTO ===================

    Proyecto guardarProyecto(Proyecto proyecto);

    List<ProyectoInfoDTO> listarInfoProyectosPorCorreoDocente(String correoDocente, String filtro);

    int getMaxVersionFormatoA(UUID proyectoId);

    void actualizarEstadoProyecto(UUID proyectoId, EstadoProyecto estado);

    EstadoProyecto obtenerEstadoProyecto(UUID proyectoId);

    
    
    
    
    
    

    // fomatoA
    FormatoA obtenerUltimoFormatoA(UUID proyectoId);

    int contarFormatoAObservados(UUID proyectoId);

    Optional<FormatoADTO> obtenerUltimoFormatoAObservadoDTO(UUID proyectoId);



}
