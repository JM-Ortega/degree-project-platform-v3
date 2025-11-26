package co.edu.unicauca.academicprojectservice.infrastructure.adapters.input.rest;

import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.repository.FormatoARepository;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {
    private final EstudianteRepository estudianteRepository;
    private final FormatoARepository formatoARepository;

    public EstudianteController(EstudianteRepository estudianteRepository, FormatoARepository formatoARepository) {
        this.estudianteRepository = estudianteRepository;
        this.formatoARepository = formatoARepository;
    }

    @GetMapping("/libre/{correo}")
    public ResponseEntity<?> estudianteLibre(@PathVariable String correo) {
        if (correo == null || correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no puede estar vacío");
        }
        boolean existe = estudianteRepository.findByCorreoIgnoreCase(correo).isPresent();
        if (!existe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un estudiante con ese correo");
        }
        boolean libre = !estudianteRepository.tieneProyectoEnTramite(correo);
        return ResponseEntity.ok().body(
                java.util.Map.of("correo", correo, "libre", libre)
        );
    }

    @GetMapping("/existe/{correo}")
    public ResponseEntity<Boolean> existeEstudiante(@PathVariable String correo) {
        if (correo == null || correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no puede estar vacío");
        }
        return ResponseEntity.ok(estudianteRepository.findByCorreoIgnoreCase(correo).isPresent());
    }

    @GetMapping("/tieneProyectoEnTramite/{correo}")
    public ResponseEntity<Boolean> estudianteTieneProyectoEnTramitePorCorreo(@PathVariable String correo) {
        if (correo == null || correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no puede estar vacío");
        }
        return ResponseEntity.ok(estudianteRepository.tieneProyectoEnTramite(correo));
    }

    @GetMapping("/tieneFormatoAAprobado/{correo}")
    public ResponseEntity<Boolean> estudianteTieneFormatoAAprobado(@PathVariable String correo) {
        if (correo == null || correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no puede estar vacío");
        }
        return ResponseEntity.ok(formatoARepository.existeFormatoAAprobadoPorCorreo(correo, EstadoFormatoA.APROBADO));
    }
}
