package com.ecom.servicecommande.client;

import com.ecom.servicecommande.dto.OrderLineRequest;

import java.util.List;

public interface StockClient {
    boolean reserve(Long orderId, List<OrderLineRequest> lignes);   // true = tout est réservé
    void restore(Long orderId);                                     // rendre les quantités
}