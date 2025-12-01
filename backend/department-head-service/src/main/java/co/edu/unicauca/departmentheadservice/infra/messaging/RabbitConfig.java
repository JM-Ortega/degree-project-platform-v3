package co.edu.unicauca.departmentheadservice.infra.messaging;

import co.edu.unicauca.shared.contracts.messaging.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de Departamento.
 */
@Configuration
public class RabbitConfig {

    @Value("${messaging.exchange.main}")
    private String mainExchangeName;

    @Value("${messaging.exchange.dlx}")
    private String dlxExchangeName;

    @Value("${messaging.queues.department}")
    private String departmentQueueName;

    @Value("${messaging.queues.departmentDlq}")
    private String departmentDlqName;

    // ===== Exchanges =====
    @Bean
    public TopicExchange mainExchange() {
        return ExchangeBuilder.topicExchange(mainExchangeName).durable(true).build();
    }

    @Bean
    public TopicExchange dlxExchange() {
        return ExchangeBuilder.topicExchange(dlxExchangeName).durable(true).build();
    }

    // ===== Queues =====
    @Bean
    public Queue departmentQueue() {
        return QueueBuilder.durable(departmentQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", departmentDlqName)
                .build();
    }

    @Bean
    public Queue departmentDlq() {
        return QueueBuilder.durable(departmentDlqName).build();
    }

    // ===== Bindings =====

    /** Escucha eventos de creación de usuarios. */
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(departmentQueue())
                .to(mainExchange())
                .with(RoutingKeys.AUTH_USER_CREATED);
    }

    /** Escucha anteproyectos sin evaluadores. */
    @Bean
    public Binding anteproyectoSinEvaluadoresBinding() {
        return BindingBuilder.bind(departmentQueue())
                .to(mainExchange())
                .with(RoutingKeys.ACADEMIC_ANTEPROYECTO_CREATED);
    }

    /** Binding para la Dead Letter Queue. */
    @Bean
    public Binding departmentDlqBinding() {
        return BindingBuilder.bind(departmentDlq())
                .to(dlxExchange())
                .with(departmentDlqName);
    }

    // ===== Conversión JSON =====
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(
            com.fasterxml.jackson.databind.ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    // ===== RabbitTemplate =====
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
