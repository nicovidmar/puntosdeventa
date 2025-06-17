package com.notifierservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue registrationQueue() {
        return new Queue("registration.queue", true);
    }

    @Bean
    public Queue billQueue() {
        return new Queue("bill.queue", true);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAfterReceivePostProcessors(message -> {
            String traceId = (String) message.getMessageProperties().getHeaders().get("traceId");
            String spanId = (String) message.getMessageProperties().getHeaders().get("spanId");

            System.out.printf("Mensaje recibido con traceId=%s, spanId=%s%n", traceId, spanId);

            return message;
        });
        return factory;
    }
}