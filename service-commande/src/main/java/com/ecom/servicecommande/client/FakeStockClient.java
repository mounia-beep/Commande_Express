package com.ecom.servicecommande.client;

import com.ecom.servicecommande.dto.OrderLineRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FakeStockClient implements StockClient {

    @Override
    public boolean reserve(Long orderId, List<OrderLineRequest> lignes) {
        return lignes.stream().allMatch(l -> l.quantite() <= 100);
    }

    @Override
    public void restore(Long orderId) {
        // rien : le vrai service-stock le fera plus tard
    }
}