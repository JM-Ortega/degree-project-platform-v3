package co.edu.unicauca.academicprojectservice.port.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
