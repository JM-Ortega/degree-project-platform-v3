package co.edu.unicauca.authservice.infra.messaging;

import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.messaging.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de dominio relacionados con usuarios en RabbitMQ.
 *
 * Envía mensajes al exchange principal cuando se crea un usuario.
 */
@Component
public class UserEventsPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserEventsPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public UserEventsPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${messaging.exchange.main}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    /**
     * Publica un evento de creación de usuario en el exchange principal.
     *
     * @param event evento {@link UserCreatedEvent} con los datos del usuario creado.
     */
    public void publishUserCreatedEvent(UserCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    RoutingKeys.AUTH_USER_CREATED,
                    event,
                    message -> {
                        var props = message.getMessageProperties();
                        props.setContentType("application/json");
                        props.setHeader("__TypeId__",
                                "co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent");
                        props.setHeader("x-schema-version", "1");

                        // usamos personaId como identificador del mensaje
                        if (event.id() != null) {
                            props.setMessageId(event.id().toString());
                        }

                        return message;
                    }
            );

            log.info("[Auth] ✅ Evento publicado ex={} rk={} personaId={}",
                    exchangeName, RoutingKeys.AUTH_USER_CREATED, event.id());
        } catch (Exception ex) {
            log.error("[Auth] ❌ Error al publicar {}: {}", RoutingKeys.AUTH_USER_CREATED, ex.getMessage(), ex);
        }
    }
}
