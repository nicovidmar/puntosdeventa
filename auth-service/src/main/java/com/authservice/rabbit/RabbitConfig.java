package com.authservice.rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;

@Configuration
public class RabbitConfig {
    public static final String REGISTRATION_QUEUE = "registration.queue";

    @Bean
    public Queue registrationQueue() {
        return new Queue(REGISTRATION_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Tracer tracer) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setBeforePublishPostProcessors(message -> {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                message.getMessageProperties().getHeaders().put("traceId", currentSpan.context().traceId());
                message.getMessageProperties().getHeaders().put("spanId", currentSpan.context().spanId());
            }
            return message;
        });
        return rabbitTemplate;
    }
}
