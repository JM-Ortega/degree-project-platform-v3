package co.edu.unicauca.authservice.dto;

import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO usado para registrar una nueva persona junto con su usuario asociado.
 *
 * <p>
 * Es genérico: a partir de los roles enviados, el backend decidirá qué tipo
 * concreto de {@link co.edu.unicauca.authservice.domain.entities.Persona}
 * se debe crear (Estudiante, Docente, Coordinador o JefeDeDepartamento).
 * </p>
 *
 * <p>
 * Para el registro público solo deben enviarse los roles:
 * <ul>
 *   <li>{@link Rol#ESTUDIANTE}</li>
 *   <li>{@link Rol#DOCENTE}</li>
 * </ul>
 * Los roles con mayor privilegio (COORDINADOR, JEFE_DE_DEPARTAMENTO)
 * deben gestionarse desde endpoints administrativos.
 * </p>
 */
@Schema(description = "Datos necesarios para registrar una nueva persona en el sistema.")
public record RegistroPersonaDto(

        @NotBlank
        @Schema(description = "Nombres de la persona.", example = "Juan Sebastián")
        String nombres,

        @NotBlank
        @Schema(description = "Apellidos de la persona.", example = "Ortega Narváez")
        String apellidos,

        @NotBlank
        @Email
        @Schema(description = "Correo que usará para autenticarse en la plataforma.",
                example = "juan.ortega@unicauca.edu.co")
        String email,

        @NotBlank
        @Schema(description = "Contraseña en texto plano. Se enviará a Keycloak para ser hasheada.",
                example = "Clave123*")
        String password,

        @Schema(description = "Número de celular de contacto.", example = "3145678901")
        String celular,

        @Schema(description = "Programa académico al que pertenece la persona.",
                example = "INGENIERIA_DE_SISTEMAS")
        Programa programa,

        @NotEmpty
        @ArraySchema(schema = @Schema(description = "Rol asignado.", example = "ESTUDIANTE"))
        @Schema(description = "Lista de roles que tendrá el usuario (mínimo uno).")
        List<Rol> roles,

        @Schema(description = "Departamento al que pertenece (solo Docente / JefeDeDepartamento).",
                example = "SISTEMAS")
        Departamento departamento

) { }
