package com.ecom.servicecommande.client;

import com.ecom.servicecommande.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FakeProductClient implements ProductClient {
    @Override
    public ProductDto getProduct(Long id) {
        return new ProductDto(id, "Produit " + id, new BigDecimal("100.00"), "DISPONIBLE");
    }
}