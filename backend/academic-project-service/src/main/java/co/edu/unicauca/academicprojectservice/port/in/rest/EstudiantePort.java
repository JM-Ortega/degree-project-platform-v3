package co.edu.unicauca.academicprojectservice.port.in.rest;

import co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO;
import org.springframework.http.ResponseEntity;

public interface EstudiantePort {

    ResponseEntity<?> estudianteLibre(String correo);

    ResponseEntity<Boolean> existeEstudiante(String correo);

    ResponseEntity<Boolean> estudianteTieneProyectoEnTramitePorCorreo(String correo);

    ResponseEntity<Boolean> estudianteTieneFormatoAAprobado(String correo);

    ResponseEntity<String> asociarAnteproyectoAProyecto(String correo, AnteproyectoDTO anteproyectoDTO);

    ResponseEntity<Boolean> estudianteTieneAnteproyecto(String correo);
}
