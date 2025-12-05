package co.edu.unicauca.authservice.presentation;

import co.edu.unicauca.authservice.services.InformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de exponer información institucional
 * relacionada con programas académicos y datos de contacto.
 */
@Tag(name = "Información", description = "Endpoints para consultar datos institucionales")
@RestController
public class InformationController {
    @Autowired
    private InformationService informationService;

    /**
     * Obtiene el correo del coordinador asociado a un programa académico.
     *
     * @param programa nombre del programa del cual se quiere obtener el correo
     * @return correo electrónico del coordinador
     */
    @Operation(
            summary = "Obtiene el correo del coordinador de un programa",
            description = "Devuelve el correo electrónico del coordinador según el nombre del programa proporcionado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correo encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No existe un coordinador para el programa indicado")
    })
    @GetMapping("/coordinador/{programa}")
    public ResponseEntity<String> getEmailCoordinador(@PathVariable String programa)
    {
        String email =informationService.obtenerEmailCoordinadorPorPrograma(programa);
        return ResponseEntity.ok(email);
    }

    /**
     * Obtiene el correo del jefe de departamento.
     *
     * @param departamento nombre del departamento
     * @return correo electrónico del jefe del departamento
     */
    @Operation(
            summary = "Obtiene el correo del jefe de un departamento",
            description = "Devuelve el correo electrónico del jefe del departamento proporcionado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Correo encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No existe un jefe asociado al departamento indicado")
    })
    @GetMapping("/jefe/{departamento}")
    public ResponseEntity<String> getEmailJefe(@PathVariable String departamento)
    {
        String email =informationService.obtenerEmailJefePorDepartamento(departamento);
        return ResponseEntity.ok(email);
    }

    /**
     * Obtiene el número de teléfono asociado a un correo electrónico institucional.
     *
     * @param correo correo institucional de la persona
     * @return número telefónico correspondiente
     */
    @Operation(
            summary = "Obtiene número telefónico por correo",
            description = "Devuelve el número de teléfono asociado a un correo institucional."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Número encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No existe un teléfono asociado al correo indicado")
    })
    @GetMapping("/telefono")
    public ResponseEntity<String> getNumerosTelefono(
            @RequestParam String correo
    ){
        String telefono = informationService.obtenerTelefonoPorCorreo(correo);
        return ResponseEntity.ok(telefono);
    }
}
