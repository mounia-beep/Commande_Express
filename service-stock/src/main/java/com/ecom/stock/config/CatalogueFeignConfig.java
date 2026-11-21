package com.ecom.stock.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;

// Pas de @Configuration : cette config ne doit s'appliquer qu'au client du catalogue
public class CatalogueFeignConfig {

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(200, 1000, 3);   // 3 tentatives au total, pause de 200 ms puis ~300 ms
    }
}