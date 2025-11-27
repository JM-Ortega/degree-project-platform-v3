package co.edu.unicauca.academicprojectservice.adapter.in.rest;

import co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO;
import co.edu.unicauca.academicprojectservice.application.services.ProyectoService;
import co.edu.unicauca.academicprojectservice.port.in.rest.EstudiantePort;
import co.edu.unicauca.academicprojectservice.application.services.EstudianteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class EstudianteController implements EstudiantePort {
    private final EstudianteService estudianteService;
    private final ProyectoService proyectoService;

    public EstudianteController(EstudianteService estudianteService, ProyectoService proyectoService) {
        this.estudianteService = estudianteService;
        this.proyectoService = proyectoService;
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

    public ResponseEntity<String> asociarAnteproyectoAProyecto(String correo, AnteproyectoDTO anteproyectoDTO) {
        try {
            proyectoService.asociarAnteproyectoAProyecto(correo, anteproyectoDTO);
            return ResponseEntity.ok("Anteproyecto asociado correctamente al proyecto del estudiante");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    public ResponseEntity<Boolean> estudianteTieneAnteproyecto(String correo) {
        try {
            return ResponseEntity.ok(estudianteService.estudianteTieneAnteproyectoAsociado(correo));
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
