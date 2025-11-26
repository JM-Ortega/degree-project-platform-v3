package co.edu.unicauca.academicprojectservice.port.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DocentePort {
    // se mantiene
    @GetMapping("/countProyectos/{correo}")
    public ResponseEntity<Integer> contarProyectosEnTramite(@PathVariable String correo);
    //
}
