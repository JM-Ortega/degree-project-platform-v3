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
    private String tipo;

    /** Lista de direcciones de correo electrónico de los estudiantes y docentes realcionados. */
    @NotEmpty
    private List<String> correos;

    /** Asunto del mensaje de notificación. */
    @NotBlank
    private String asunto;

    /** Contenido principal del mensaje. */
    @NotBlank
    private String mensaje;

    /** Programa al que pertenece el estudainte. */
    private Programa programa;

    /** Marca de tiempo del evento (formato ISO-8601). */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime timestamp;
}
