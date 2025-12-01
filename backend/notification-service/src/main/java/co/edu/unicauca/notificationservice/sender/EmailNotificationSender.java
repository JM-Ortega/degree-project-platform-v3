package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import co.edu.unicauca.notificationservice.service.InformationService;

/**
 * Implementación de {@link NotificationSender} responsable del envío de notificaciones por correo electrónico.
 * Registra la información relevante del mensaje en el log para efectos de simulación o auditoría.
 */
@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender {
    /**
     * Envía una notificación por correo electrónico.
     * Este metodo simula el envío registrando los datos del mensaje en el log.
     *
     * @param event evento de notificación con los datos del mensaje y destinatarios.
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
