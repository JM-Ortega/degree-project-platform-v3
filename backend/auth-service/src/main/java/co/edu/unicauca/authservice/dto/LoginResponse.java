package co.edu.unicauca.authservice.dto;

import co.edu.unicauca.shared.contracts.dto.SessionInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Respuesta devuelta tras un inicio de sesión exitoso.
 *
 * <p>Incluye los tokens emitidos por Keycloak, la información
 * básica de sesión del usuario y la lista completa de roles
 * que posee dentro del cliente configurado.</p>
 */
@Schema(description = "Respuesta generada después de un inicio de sesión exitoso.")
public record LoginResponse(

        @Schema(description = "Token JWT de acceso emitido por Keycloak.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Token usado para renovar la sesión sin ingresar credenciales nuevamente.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9-refresh...")
        String refreshToken,

        @Schema(description = "Tiempo de expiración del accessToken (en segundos).",
                example = "300")
        Long expiresIn,

        @Schema(description = "Tipo de token retornado por Keycloak.",
                example = "Bearer")
        String tokenType,

        @Schema(description = "Información mínima del usuario para mantener la sesión en el front.")
        SessionInfo session,

        @Schema(description = "Lista de roles asignados al usuario dentro del cliente Keycloak.",
                example = "[\"ESTUDIANTE\", \"DOCENTE\"]")
        List<String> roles

) { }
