package co.edu.unicauca.academicprojectservice.adapter.in.rest;

import co.edu.unicauca.academicprojectservice.port.in.rest.EstudiantePort;
import co.edu.unicauca.academicprojectservice.application.services.EstudianteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class EstudianteController implements EstudiantePort {
    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    public ResponseEntity<?> estudianteLibre(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no puede estar vacío");
        }
        boolean existe = estudianteService.existeEstudiantePorCorreo(correo);
        if (!existe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un estudiante con ese correo");
        }
        boolean libre = !estudianteService.estudianteTieneProyectoEnTramitePorCorreo(correo);
        return ResponseEntity.ok().body(
                java.util.Map.of("correo", correo, "libre", libre)
        );
    }

    public ResponseEntity<Boolean> existeEstudiante(String correo) {
        return ResponseEntity.ok(estudianteService.existeEstudiantePorCorreo(correo));
    }

    public ResponseEntity<Boolean> estudianteTieneProyectoEnTramitePorCorreo(String correo) {
        return ResponseEntity.ok(estudianteService.estudianteTieneProyectoEnTramitePorCorreo(correo));
    }

    public ResponseEntity<Boolean> estudianteTieneFormatoAAprobado(String correo) {
        return ResponseEntity.ok(estudianteService.estudianteTieneFormatoAAprobado(correo));
    }
}
