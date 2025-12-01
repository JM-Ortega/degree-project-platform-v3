package co.edu.unicauca.notificationservice.sender;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Decorador que añade el envío de SMS después del envío base.
 * Envuelve un {@link NotificationSender} y ejecuta la lógica adicional.
 */
@Slf4j
public class SmsNotificationDecorator implements NotificationSender {
    /** Componente original encargado del envío base (correo). */
    private final NotificationSender wrapped;

    /**
     * Construye el decorador con el componente que será extendido.
     *
     * @param wrapped implementación base del envío
     */
    public SmsNotificationDecorator(NotificationSender wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Envía la notificación base y luego registra el envío por SMS.
     *
     * @param event evento de notificación con teléfonos y mensaje
     */
    @Override
    public void send(NotificationEvent event) {
        // Envío base (correo electrónico)
        wrapped.send(event);

        log.info("""
        
        📱 Enviando SMS
        ├── A: {}
        └── Mensaje: {}
        """, event.getTelefonos(), event.getMensaje());

    }
}
