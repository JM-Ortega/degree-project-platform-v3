package co.edu.unicauca.notificationservice.consumer;

import co.edu.unicauca.notificationservice.service.NotificationService;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Listener encargado de recibir eventos de notificación desde la cola de mensajería.
 * Valida el evento recibido y delega el envío al {@link NotificationService}.
 */
@Slf4j
@Component
public class NotificationListener {

    /** Servicio encargado de procesar y enviar la notificación. */
    NotificationService  notificationService;

    /**
     * Construye el listener con el servicio de notificaciones.
     *
     * @param notificationService servicio que maneja el envío de notificaciones
     */
    public NotificationListener(@Qualifier("notificationService") NotificationService  notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Maneja un evento de notificación recibido desde RabbitMQ.
     * Valida el evento, registra la información y delega el proceso de envío.
     * En caso de error, rechaza el mensaje sin reintento.
     *
     * @param event evento recibido desde la cola
     */
    @RabbitListener(queues = "${messaging.queues.notification}")
    public void handleNotification(NotificationEvent event) {
        if (event == null) {
            log.warn("Evento de notificación nulo recibido; se descarta.");
            return;
        }

        log.info("""
                        
                        📬 Nueva notificación a enviar:
                        ├─ Tipo: {}
                        ├─ Destinatarios: {}
                        └─ Mensaje: {}
                        """, event.getTipo(),
                String.join(", ", event.getCorreos()),
                event.getMensaje());

        try {
            notificationService.notificar(event);
        } catch (Exception e) {
            log.error("❌ Error al procesar notificación: {}", e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
