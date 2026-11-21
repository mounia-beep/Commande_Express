package com.ecom.servicenotification.listener;

import com.ecom.servicenotification.config.RabbitConfig;
import com.ecom.servicenotification.dto.StockAlerteEvent;
import com.ecom.servicenotification.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockAlerteListener {

    private static final Logger log = LoggerFactory.getLogger(StockAlerteListener.class);

    private final MailService mailService;

    public StockAlerteListener(MailService mailService) {
        this.mailService = mailService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onStockAlerte(StockAlerteEvent event) {
        log.info("Événement reçu : {}", event);
        mailService.envoyerAlerteStock(event);
    }
}