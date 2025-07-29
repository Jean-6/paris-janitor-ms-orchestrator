package org.example.parisjanitormsorchestrator.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class Rabbit {

    public static final String EXCHANGE = "orchestration.exchange";

    public static final String ROUTING_KEY_DATA = "data.create";
    public static final String ROUTING_KEY_IMAGE = "image.create";
    public static final String ROUTING_KEY_DOCUMENT = "document.create";
    public static final String ROUTING_KEY_RESPONSE = "orchestration.response";
    public static final String ROUTING_KEY_ROLLBACK = "rollback";

    public static final String QUEUE_DATA = "queue.data";
    public static final String QUEUE_IMAGE = "queue.image";
    public static final String QUEUE_DOCUMENT = "queue.document";
    public static final String ACK_QUEUE = "ack.queue";
    public static final String ROLLBACK_QUEUE = "rollback.queue";

    @Bean
    public DirectExchange orchestrationExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue dataQueue() {
        return new Queue(QUEUE_DATA, true);
    }

    @Bean
    public Queue imageQueue() {
        return new Queue(QUEUE_IMAGE, true);
    }

    @Bean
    public Queue documentQueue() {
        return new Queue(QUEUE_DOCUMENT, true);
    }

    @Bean
    public Queue ackQueue() {
        return new Queue(ACK_QUEUE, true);
    }

    @Bean
    public Queue rollbackQueue() {
        return new Queue(ROLLBACK_QUEUE, true);
    }

    @Bean
    public Binding bindingDataQueue(Queue dataQueue, DirectExchange exchange) {
        return BindingBuilder.bind(dataQueue).to(exchange).with(ROUTING_KEY_DATA);
    }

    @Bean
    public Binding bindingImageQueue(Queue imageQueue, DirectExchange exchange) {
        return BindingBuilder.bind(imageQueue).to(exchange).with(ROUTING_KEY_IMAGE);
    }

    @Bean
    public Binding bindingDocumentQueue(Queue documentQueue, DirectExchange exchange) {
        return BindingBuilder.bind(documentQueue).to(exchange).with(ROUTING_KEY_DOCUMENT);
    }

    @Bean
    public Binding bindingAckQueue() {
        return BindingBuilder
                .bind(ackQueue())
                .to(orchestrationExchange())
                .with(ROUTING_KEY_RESPONSE);
    }

    @Bean
    public Binding bindingRollbackQueue(Queue rollbackQueue, DirectExchange exchange) {
        return BindingBuilder.bind(rollbackQueue).to(exchange).with(ROUTING_KEY_ROLLBACK);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Pour les dates, etc.
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }




}
