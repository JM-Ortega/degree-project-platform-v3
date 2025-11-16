package co.edu.unicauca.shared.contracts.events.notification;


import co.edu.unicauca.shared.contracts.model.Programa;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Evento de notificación intercambiado entre servicios.
 * Representa la estructura de datos común para correo electrónico y SMS.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    /** Tipo de evento que originó la notificación (ej. project.created). */
    @NotBlank
    private String type;

    /** Asunto del mensaje de notificación. */
    @NotBlank
    private String subject;

    /** Contenido principal del mensaje. */
    @NotBlank
    private String message;

    /** Programa de los estudiantes. */
    @NotBlank
    private Programa programa;

    /** Marca de tiempo del evento (formato ISO-8601). */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime timestamp;
}
