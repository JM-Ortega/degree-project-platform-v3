package co.edu.unicauca.coordinatorservice.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static co.edu.unicauca.shared.contracts.messaging.RoutingKeys.*;

@Configuration
public class RabbitMQConfig {

    // ========= Nombres de exchanges =========

    @Value("${messaging.exchange.main}")
    private String mainExchangeName;

    @Value("${messaging.exchange.dlx}")
    private String dlxExchangeName;

    // ========= Nombres de colas del coordinator =========

    // Eventos de proyecto (ProyectoDTO)
    @Value("${messaging.queues.coordinator}")
    private String coordinatorProjectQueueName;

    // Eventos de Formato A (FormatoADTO)
    @Value("${messaging.queues.coordinatorFormatoA}")
    private String coordinatorFormatoAQueueName;

    // Eventos de usuario creado (auth.user.created)
    @Value("${messaging.queues.coordinatorAuth}")
    private String coordinatorAuthQueueName;

    // Dead Letter Queue del coordinator
    @Value("${messaging.queues.coordinatorDlq}")
    private String coordinatorDlqQueueName;

    // ========= Exchanges =========

    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(mainExchangeName, true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(dlxExchangeName, true, false);
    }

    // ========= Colas =========

    // Cola para eventos de proyecto
    @Bean
    public Queue coordinatorProjectQueue() {
        return QueueBuilder.durable(coordinatorProjectQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", coordinatorDlqQueueName)
                .build();
    }

    // Cola para eventos de Formato A
    @Bean
    public Queue coordinatorFormatoAQueue() {
        return QueueBuilder.durable(coordinatorFormatoAQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", coordinatorDlqQueueName)
                .build();
    }

    // Cola para eventos de usuario creado (auth.user.created)
    @Bean
    public Queue coordinatorAuthQueue() {
        return QueueBuilder.durable(coordinatorAuthQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", coordinatorDlqQueueName)
                .build();
    }

    // Dead Letter Queue del coordinator-service
    @Bean
    public Queue coordinatorDlqQueue() {
        return QueueBuilder.durable(coordinatorDlqQueueName).build();
    }

    // ========= Bindings =========

    // project.created -> cola de proyectos del coordinator
    @Bean
    public Binding bindCoordinatorProject() {
        return BindingBuilder
                .bind(coordinatorProjectQueue())
                .to(mainExchange())
                .with(PROJECT_CREATED);
    }

    // academic.formata.changed -> cola de Formato A del coordinator
    @Bean
    public Binding bindCoordinatorFormatoA() {
        return BindingBuilder
                .bind(coordinatorFormatoAQueue())
                .to(mainExchange())
                .with(ACADEMIC_FORMATO_A_CHANGED);
    }

    // auth.user.created -> cola de auth del coordinator
    @Bean
    public Binding bindCoordinatorAuthUserCreated() {
        return BindingBuilder
                .bind(coordinatorAuthQueue())
                .to(mainExchange())
                .with(AUTH_USER_CREATED);
    }

    // DLX -> DLQ del coordinator
    @Bean
    public Binding bindCoordinatorDlq() {
        return BindingBuilder
                .bind(coordinatorDlqQueue())
                .to(deadLetterExchange())
                .with(coordinatorDlqQueueName);
    }

    // ========= Infraestructura JSON / listeners =========

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
