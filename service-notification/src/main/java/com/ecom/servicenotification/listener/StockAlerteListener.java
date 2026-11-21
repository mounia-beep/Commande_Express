package com.ecom.servicenotification.listener;

import com.ecom.servicenotification.config.RabbitConfig;
import com.ecom.servicenotification.dto.StockAlerteEvent;
import com.ecom.servicenotification.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StockAlerteListener {

    private static final Logger log = LoggerFactory.getLogger(StockAlerteListener.class);

    private final MailService mailService;
    private final Set<String> dejaTraites = ConcurrentHashMap.newKeySet();   // identifiants d'événements déjà vus

    public StockAlerteListener(MailService mailService) {
        this.mailService = mailService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onStockAlerte(StockAlerteEvent event) {
        if (dejaTraites.contains(event.eventId())) {
            log.warn("Événement déjà traité, ignoré : {}", event.eventId());
            return;
        }
        log.info("Événement reçu : {}", event);
        mailService.envoyerAlerteStock(event);
        dejaTraites.add(event.eventId());   // on mémorise APRÈS l'envoi du mail
    }
}