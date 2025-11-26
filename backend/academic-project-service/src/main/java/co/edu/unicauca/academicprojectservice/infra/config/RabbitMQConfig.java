package co.edu.unicauca.academicprojectservice.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio Academic Project Service.
 * Define colas, exchange principal, bindings y convertidor de mensajes JSON.
 */
@Configuration
public class RabbitMQConfig {

    // =====================================================
    // 1. Nombres definidos en application.yml
    // =====================================================
    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.exchange.dlx}")
    private String dlxExchange;

    @Value("${messaging.queues.project}")
    private String projectQueue;

    @Value("${messaging.queues.projectDlq}")
    private String projectDlq;

    @Value("${messaging.queues.projectFormatoA}")
    private String projectFormatoAQueue;

    @Value("${messaging.routing.projectCreated}")
    private String projectCreatedRoutingKey;

    @Value("${messaging.routing.projectUpdated}")
    private String projectUpdatedRoutingKey;

    @Value("${messaging.routing.formatAApprovedByCoordinator}")
    private String formatAApprovedByCoordinatorRoutingKey;

    // =====================================================
    // 2. Exchange principal y DLX (Dead Letter Exchange)
    // =====================================================
    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(mainExchange);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(dlxExchange);
    }

    // =====================================================
    // 3. Declaración de colas (project, DLQ, FormatoA, y auth)
    // =====================================================
    @Bean
    public Queue projectQueue() {
        return QueueBuilder.durable(projectQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", projectDlq)
                .build();
    }

    @Bean
    public Queue projectDlq() {
        return QueueBuilder.durable(projectDlq).build();
    }

    @Bean
    public Queue projectFormatoAQueue() {
        return QueueBuilder.durable(projectFormatoAQueue)
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", projectDlq)
                .build();
    }

    // --- NUEVA COLA dedicada a los eventos de creación de usuarios ---
    @Bean
    public Queue academicAuthQueue() {
        return QueueBuilder.durable("academic.auth.q") // nombre único para este micro
                .withArgument("x-dead-letter-exchange", dlxExchange)
                .withArgument("x-dead-letter-routing-key", projectDlq)
                .build();
    }

    // =====================================================
    // 4. Bindings
    // =====================================================
    @Bean
    public Binding bindingProjectCreated(Queue projectQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(projectQueue).to(mainExchange).with(projectCreatedRoutingKey);
    }

    @Bean
    public Binding bindingProjectUpdated(Queue projectQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(projectQueue).to(mainExchange).with(projectUpdatedRoutingKey);
    }

    @Bean
    public Binding bindingFormatoAApproved(Queue projectFormatoAQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(projectFormatoAQueue).to(mainExchange).with(formatAApprovedByCoordinatorRoutingKey);
    }

    // --- NUEVO: binding para auth.user.created ---
    @Bean
    public Binding bindAuthUserCreated(Queue academicAuthQueue, TopicExchange mainExchange) {
        return BindingBuilder.bind(academicAuthQueue)
                .to(mainExchange)
                .with("auth.user.created");
    }

    // =====================================================
    // 5. Binding DLX -> DLQ
    // =====================================================
    @Bean
    public Binding bindProjectDlqToDlx(
            @Qualifier("projectDlq") Queue projectDlqQueue,
            TopicExchange deadLetterExchange,
            @Value("${messaging.queues.projectDlq}") String rkToDlq) {

        return BindingBuilder
                .bind(projectDlqQueue)
                .to(deadLetterExchange)
                .with(rkToDlq);
    }

    // =====================================================
    // 6. Convertidor JSON (Jackson)
    // =====================================================
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // =====================================================
    // 7. RabbitTemplate con convertidor JSON
    // =====================================================
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }
}
