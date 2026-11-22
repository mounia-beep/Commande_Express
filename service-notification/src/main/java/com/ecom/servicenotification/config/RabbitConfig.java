package com.ecom.servicenotification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "stock.exchange";
    public static final String ROUTING_KEY = "stock.alerte";
    public static final String QUEUE = "notification.stock-alerte.queue";

    @Bean
    public DirectExchange stockExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue stockAlerteQueue() {
        return new Queue(QUEUE, true);   // true = la file survit à un redémarrage de RabbitMQ
    }

    @Bean
    public Binding stockAlerteBinding(Queue stockAlerteQueue, DirectExchange stockExchange) {
        return BindingBuilder.bind(stockAlerteQueue).to(stockExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();   // messages en JSON
    }
}