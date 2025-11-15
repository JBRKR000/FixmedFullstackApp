package org.fixmed.fixmed.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Binding; // ✔️ POPRAWNY import

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange notificationsExchange() {
        return ExchangeBuilder
                .directExchange("fixmed.exchange.notifications")
                .durable(true)
                .build();
    }

    @Bean
    public Queue dummyQueue() {
        return new Queue("dummyQueueForDeclarationOnly", true); // nie musi być używana
    }

    @Bean
    public Binding dummyBinding() {
        return BindingBuilder
                .bind(dummyQueue())
                .to(notificationsExchange())
                .with("fixmed.key.notifications");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());

        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.printf("❌ Message returned! replyCode=%s, replyText=%s, exchange=%s, routingKey=%s%n",
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey());
        });

        return rabbitTemplate;
    }

    @Bean
    MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}