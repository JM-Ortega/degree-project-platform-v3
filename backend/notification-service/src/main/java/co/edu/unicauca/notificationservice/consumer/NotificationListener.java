package co.edu.unicauca.notificationservice.consumer;

import co.edu.unicauca.notificationservice.service.NotificationService;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Consumer de eventos de notificación.
 * Decide el canal de envío (solo correo o correo+SMS) según la presencia de teléfonos.
 */
@Slf4j
@Component
public class NotificationListener {

    NotificationService  notificationService;

    /**
     * Inyección explícita de beans calificados.
     *
     * @param notificationService bean del servicio
     */
    public NotificationListener(@Qualifier("notificationService") NotificationService  notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Procesa eventos de notificación recibidos desde la cola AMQP.
     * Si existen teléfonos, utiliza el sender decorado (correo + SMS); de lo contrario, solo correo.
     *
     * @param event evento de notificación deserializado desde el mensaje AMQP
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
