package co.edu.unicauca.academicprojectservice.adapter.in.rest;

import co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO;
import co.edu.unicauca.academicprojectservice.application.services.EstudianteService;
import co.edu.unicauca.academicprojectservice.application.services.ProyectoService;
import co.edu.unicauca.academicprojectservice.port.in.rest.EstudiantePort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController implements EstudiantePort {

    private final EstudianteService estudianteService;
    private final ProyectoService proyectoService;

    public EstudianteController(EstudianteService estudianteService, ProyectoService proyectoService) {
        this.estudianteService = estudianteService;
        this.proyectoService = proyectoService;
    }

    @GetMapping("/libre/{correo}")
    @Override
    public ResponseEntity<?> estudianteLibre(@PathVariable String correo) {
        if (correo == null || correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no puede estar vacío");
        }

        boolean existe = estudianteService.existeEstudiantePorCorreo(correo);
        if (!existe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un estudiante con ese correo");
        }

        boolean libre = !estudianteService.estudianteTieneProyectoEnTramitePorCorreo(correo);

        return ResponseEntity.ok(
                java.util.Map.of("correo", correo, "libre", libre)
        );
    }

    @GetMapping("/existe/{correo}")
    @Override
    public ResponseEntity<Boolean> existeEstudiante(@PathVariable String correo) {
        return ResponseEntity.ok(estudianteService.existeEstudiantePorCorreo(correo));
    }

    @GetMapping("/tieneProyectoEnTramite/{correo}")
    @Override
    public ResponseEntity<Boolean> estudianteTieneProyectoEnTramitePorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(estudianteService.estudianteTieneProyectoEnTramitePorCorreo(correo));
    }

    @GetMapping("/tieneFormatoAAprobado/{correo}")
    @Override
    public ResponseEntity<Boolean> estudianteTieneFormatoAAprobado(@PathVariable String correo) {
        return ResponseEntity.ok(estudianteService.estudianteTieneFormatoAAprobado(correo));
    }

    @PostMapping("/asociarAnteproyecto/{correo}")
    @Override
    public ResponseEntity<String> asociarAnteproyectoAProyecto(
            @PathVariable String correo,
            @RequestBody AnteproyectoDTO anteproyectoDTO
    ) {
        try {
            proyectoService.asociarAnteproyectoAProyecto(correo, anteproyectoDTO);
            return ResponseEntity.ok("Anteproyecto asociado correctamente");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

    @GetMapping("/{correo}/tieneAnteproyecto")
    @Override
    public ResponseEntity<Boolean> estudianteTieneAnteproyecto(@PathVariable String correo) {
        try {
            return ResponseEntity.ok(estudianteService.estudianteTieneAnteproyectoAsociado(correo));
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
