package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import co.edu.unicauca.notificationservice.service.InformationService;

/**
 * Implementación de {@link NotificationSender} que envía notificaciones por correo electrónico.
 * Registra en el log la información del envío.
 */
@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender {

    /**
     * Envía una notificación por correo electrónico.
     * Solo registra el envío en el log.
     *
     * @param event evento con los datos del correo (destinatarios, asunto y mensaje)
     */
    @Override
    public void send(NotificationEvent event) {
        log.info("""
                        
                        ✉️  Enviando correo electrónico
                        ├── Para: {}
                        ├── Asunto: {}
                        └── Mensaje: {}
                        """,
                String.join(", ", event.getCorreos()),
                event.getAsunto(),
                event.getMensaje());
    }
}
