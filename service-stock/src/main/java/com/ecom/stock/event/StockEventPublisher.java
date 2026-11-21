package com.ecom.stock.event;

import com.ecom.stock.config.RabbitConfig;
import com.ecom.stock.entity.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(StockEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public StockEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishStockAlerte(Stock stock) {
        StockAlerteEvent event = new StockAlerteEvent(UUID.randomUUID().toString(),
                stock.getProductId(), stock.getQuantite(), stock.getSeuilAlerte());
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);
            log.info("Événement publié : {}", event);
        } catch (AmqpException e) {
            log.error("Impossible de publier l'événement (RabbitMQ indisponible ?)", e);
        }
    }
}