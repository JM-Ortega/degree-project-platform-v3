package co.edu.unicauca.authservice.dto;

import co.edu.unicauca.shared.contracts.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para iniciar sesión en el sistema.
 *
 * <p>Contiene las credenciales mínimas necesarias que se enviarán
 * al endpoint de Keycloak (username/password) y el rol con el que
 * el usuario desea autenticarse en esta sesión.</p>
 */
@Schema(description = "Credenciales utilizadas para iniciar sesión.")
public record LoginRequest(

        @NotBlank
        @Email
        @Schema(description = "Correo institucional del usuario.", example = "juan.ortega@unicauca.edu.co")
        String email,

        @NotBlank
        @Schema(description = "Contraseña del usuario.", example = "Clave123*")
        String password,

        @NotNull
        @Schema(description = "Rol con el que desea iniciar sesión.", example = "DOCENTE")
        Rol rol
) { }
