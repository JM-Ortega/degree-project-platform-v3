package co.edu.unicauca.academicprojectservice.port.in.rest;

import co.edu.unicauca.academicprojectservice.application.dto.*;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ProyectoPort {

    ResponseEntity<String> crearProyecto(ProyectoDTO dto);

    ResponseEntity<List<ProyectoInfoDTO>> listarPorDocente(String correo, String filtro);

    ResponseEntity<List<ProyectoEstudianteDTO>> listarPorEstudiante(String correo);

    ResponseEntity<EstadoProyecto> enforceAutoCancelIfNeeded(UUID proyectoId);

    ResponseEntity<Integer> getMaxVersionFormatoA(UUID proyectoId);

    ResponseEntity<Boolean> canResubmit(UUID proyectoId);

    ResponseEntity<Boolean> tieneObservacionesFA(UUID proyectoId);

    ResponseEntity<String> insertarFormatoAProyecto(Long proyectoId, FormatoADTO formatoA);

    ResponseEntity<FormatoADTO> obtenerUltimoFormatoAConObservaciones(UUID proyectoId);

    ResponseEntity<Integer> countProyectosByEstadoYTipo(
            TipoProyecto tipoProyecto,
            EstadoProyecto estadoProyecto,
            String correoDocente
    );

    ResponseEntity<List<AnteproyectoDTO>> listarAnteproyectosDocente(String correo, String filtro);

    ResponseEntity<AnteproyectoDTO> obtenerAnteproyecto(UUID proyectoId);
}
