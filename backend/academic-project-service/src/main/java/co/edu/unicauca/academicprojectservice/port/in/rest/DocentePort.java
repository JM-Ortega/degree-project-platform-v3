package co.edu.unicauca.academicprojectservice.port.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public interface DocentePort {
    ResponseEntity<Integer> contarProyectosEnTramite(String correo);
}
