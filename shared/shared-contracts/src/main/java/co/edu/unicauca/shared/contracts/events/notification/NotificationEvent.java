package co.edu.unicauca.shared.contracts.events.notification;

import co.edu.unicauca.shared.contracts.model.Departamento;
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

    /** Lista de direcciones de correo electrónico de los estudiantes y docentes relacionados. */
    @NotEmpty
    private List<String> correos;

    /** Asunto del mensaje de notificación. */
    @NotBlank
    private String asunto;

    /** Contenido principal del mensaje. */
    @NotBlank
    private String mensaje;

    /** Programa de los estudiantes relacionados con la notificación. */
    // Debe poder ser vacio ya que por ejemplo el coordinador no necesita enviar el programa de el estudiante
    // Creo que el unico que necesittenviar el programa es el micro de academic
    private Programa programa;

    private Departamento departamento;

    /** Marca de tiempo del evento (formato ISO-8601). */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime timestamp;

    // Indica si se quiere enviar SMS o no
    private boolean sms;

    private List<String> telefonos;

    // General
    public NotificationEvent (String tipo, List<String> correos, String asunto, String mensaje, OffsetDateTime timestamp, boolean sms) {
        this.tipo = tipo;
        this.correos = correos;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.sms = sms;
    }

    // Notificar proyectos
    public NotificationEvent (String tipo, List<String> correos, String asunto, String mensaje, Programa programa, OffsetDateTime timestamp, boolean sms) {
        this.tipo = tipo;
        this.correos = correos;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.programa = programa;
        this.timestamp = timestamp;
        this.sms = sms;
    }

    // Notificar anteproyectos
    public NotificationEvent (String tipo, List<String> correos, String asunto, String mensaje, Departamento departamento, OffsetDateTime timestamp, boolean sms) {
        this.tipo = tipo;
        this.correos = correos;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.departamento = departamento;
        this.timestamp = timestamp;
        this.sms = sms;
    }
}
