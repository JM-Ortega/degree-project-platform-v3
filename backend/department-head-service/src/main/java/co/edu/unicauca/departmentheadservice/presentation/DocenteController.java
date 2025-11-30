package co.edu.unicauca.departmentheadservice.presentation;

import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.departmentheadservice.services.DocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class DocenteController {

    private final DocenteService docenteService;

    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    /**
     * Endpoint para obtener los docentes que pertenecen al mismo departamento
     * que el docente identificado por el correo proporcionado.
     *
     * @param email correo del docente cuyo departamento se usará para filtrar a los demás docentes.
     * @return ResponseEntity con la lista de docentes del mismo departamento o un código de error.
     */
    @GetMapping("/departamento")
    @Operation(
            summary = "Obtener docentes del mismo departamento",
            description = "Devuelve todos los docentes que pertenecen al mismo departamento del docente cuyo correo se envía como parámetro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de docentes del departamento"),
            @ApiResponse(responseCode = "404", description = "No se encontró un docente con el correo proporcionado"),
            @ApiResponse(responseCode = "400", description = "El correo enviado no es válido")
    })
    public ResponseEntity<List<Docente>> obtenerDocentesPorDepartamento(
            @RequestParam String email
    ) {
        List<Docente> docentes = docenteService.obtenerDocentes(email);

        if (docentes == null || docentes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(docentes);
    }
}
