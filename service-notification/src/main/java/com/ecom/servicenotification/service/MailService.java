package com.ecom.servicenotification.service;

import com.ecom.servicenotification.dto.StockAlerteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.from}")
    private String from;

    @Value("${notification.admin-email}")
    private String adminEmail;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerAlerteStock(StockAlerteEvent event) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(adminEmail);
        mail.setSubject("Alerte stock bas : produit " + event.productId());
        mail.setText("Le stock du produit " + event.productId() + " est à " + event.quantite()
                + " unité(s). Seuil d'alerte : " + event.seuilAlerte() + ".");
        mailSender.send(mail);
        log.info("Mail envoyé à {} pour le produit {}", adminEmail, event.productId());
    }
}