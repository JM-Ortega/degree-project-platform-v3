package co.edu.unicauca.authservice.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${messaging.exchange.main}")
    private String mainExchangeName;

    @Value("${messaging.exchange.dlx}")
    private String dlxExchangeName;

    @Value("${messaging.queues.auth}")
    private String authQueueName;

    @Value("${messaging.queues.authDlq}")
    private String authDlqName;

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
    public Queue authQueue() {
        return QueueBuilder.durable(authQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", authDlqName)
                .build();
    }

    @Bean
    public Queue authDlq() {
        return QueueBuilder.durable(authDlqName).build();
    }

    // ===== Bindings =====
    @Bean
    public Binding authBinding() {
        return BindingBuilder.bind(authQueue()).to(mainExchange()).with("auth.#");
    }

    @Bean
    public Binding authDlqBinding() {
        return BindingBuilder.bind(authDlq()).to(dlxExchange()).with(authDlqName);
    }

    // ===== JSON Converter con soporte JavaTime =====
    @Bean
    public ObjectMapper rabbitObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper rabbitObjectMapper) {
        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
    }

    // ===== RabbitTemplate (opcional: confirms/returns para robustez) =====
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);


        template.setMandatory(true);
        template.setConfirmCallback((corr, ack, cause) -> {
            if (!ack) {
                System.err.println("❌ NACK publish: " + cause);
            }
        });
        template.setReturnsCallback(ret -> {
            System.err.println("❌ Returned msg rk=" + ret.getRoutingKey() +
                    " body=" + new String(ret.getMessage().getBody()));
        });

        return template;
    }
}
