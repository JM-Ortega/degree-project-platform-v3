package co.edu.unicauca.departmentheadservice.infra.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de Departamento.
 * Configura el Exchange, Queues y Bindings necesarios para publicar y escuchar eventos.
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
        return ExchangeBuilder
                .topicExchange(mainExchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange dlxExchange() {
        return ExchangeBuilder
                .topicExchange(dlxExchangeName)
                .durable(true)
                .build();
    }

    // ===== Queues =====
    @Bean
    public Queue departmentQueue() {
        return QueueBuilder
                .durable(departmentQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", departmentDlqName)
                .build();
    }

    @Bean
    public Queue departmentDlq() {
        return QueueBuilder
                .durable(departmentDlqName)
                .build();
    }

    // ===== Bindings (una cola, dos bindings específicos) =====

    /** Escucha eventos de creación de usuarios (auth.user.created). */
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder
                .bind(departmentQueue())
                .to(mainExchange())
                .with("auth.user.created");
    }

    /** Escucha eventos de creación de anteproyectos (academic.anteproyecto.created). */
    @Bean
    public Binding anteproyectoSinEvaluadoresBinding() {
        return BindingBuilder
                .bind(departmentQueue())
                .to(mainExchange())
                .with("academic.anteproyecto.created");
    }

    /** Binding para la Dead Letter Queue. */
    @Bean
    public Binding departmentDlqBinding() {
        return BindingBuilder
                .bind(departmentDlq())
                .to(dlxExchange())
                .with(departmentDlqName);
    }

    /** Binding para subir anteproyectos con evaluadores*/
    @Bean
    public Binding proposalApprovedBinding() {
        return BindingBuilder
                .bind(departmentQueue())
                .to(mainExchange())
                .with("department.proposal.approved");
    }


    // ===== Conversión JSON (el mismo converter sirve para template y listeners vía auto-config) =====
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    // ===== RabbitTemplate (por si publicas algo desde este micro) =====
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
