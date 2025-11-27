package co.edu.unicauca.academicprojectservice.port.in.rest;

import co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface EstudiantePort {
    // se debe mantener pero actualizar
    @GetMapping("/libre/{correo}")
    public ResponseEntity<?> estudianteLibre(@PathVariable String correo);
    // =========================================

    @GetMapping("/existe/{correo}")
    public ResponseEntity<Boolean> existeEstudiante(@PathVariable String correo);

    @GetMapping("/tieneProyectoEnTramite/{correo}")
    public ResponseEntity<Boolean> estudianteTieneProyectoEnTramitePorCorreo(@PathVariable String correo);

    @GetMapping("/tieneFormatoAAprobado/{correo}")
    public ResponseEntity<Boolean> estudianteTieneFormatoAAprobado(@PathVariable String correo);

    @PostMapping("/asociarAnteproyecto/{correo}")
    public ResponseEntity<String> asociarAnteproyectoAProyecto(@PathVariable String correo,
            @RequestBody AnteproyectoDTO anteproyectoDTO);
}
