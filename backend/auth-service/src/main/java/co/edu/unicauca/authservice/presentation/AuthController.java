package co.edu.unicauca.authservice.presentation;

import co.edu.unicauca.authservice.domain.entities.Persona;
import co.edu.unicauca.authservice.dto.LoginRequest;
import co.edu.unicauca.authservice.dto.LoginResponse;
import co.edu.unicauca.authservice.dto.RegistroPersonaDto;
import co.edu.unicauca.authservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el registro y autenticación de usuarios.
 *
 * El inicio de sesión (login) se delega a Keycloak usando
 * el flujo "Resource Owner Password Credentials".
 *
 * Este controlador expone:
 *  - POST /register : alta de nuevos usuarios (Estudiante / Docente).
 *  - POST /login    : autenticación mediante Keycloak.
 */
@Tag(
        name = "Auth",
        description = "Registro de usuarios y login mediante Keycloak"
)
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =====================================================================
    //  REGISTER
    // =====================================================================

    /**
     * Endpoint para registrar una nueva persona (solo Estudiante o Docente).
     *
     * @param dto datos de registro enviados por el cliente
     * @return la persona registrada
     */
    @Operation(summary = "Registrar una nueva persona (solo Estudiante o Docente)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Persona registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = Persona.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o rol no permitido",
                    content = @Content
            )
    })
    @PostMapping("/register")
    public ResponseEntity<Persona> register(@Valid @RequestBody RegistroPersonaDto dto) {
        Persona persona = authService.registrarPersona(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(persona);
    }


    // =====================================================================
    //  LOGIN
    // =====================================================================

    /**
     * Endpoint para iniciar sesión.
     * <p>
     * Este método delega la autenticación en Keycloak:
     * 1. Envía usuario/contraseña al endpoint de token del realm.
     * 2. Verifica que el usuario tenga el rol solicitado.
     * 3. Devuelve los tokens y la información básica de sesión.
     *
     * @param request credenciales + rol escogido para esta sesión
     * @return tokens emitidos por Keycloak y datos de sesión
     */
    @Operation(summary = "Iniciar sesión (autenticación delegada a Keycloak)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inicio de sesión exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Credenciales inválidas o rol no autorizado",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
