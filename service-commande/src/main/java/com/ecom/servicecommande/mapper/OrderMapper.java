package com.ecom.servicecommande.mapper;

import com.ecom.servicecommande.dto.OrderLineResponse;
import com.ecom.servicecommande.dto.OrderResponse;
import com.ecom.servicecommande.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderLineResponse> lignes = order.getLignes().stream()
                .map(l -> new OrderLineResponse(
                        l.getId(), l.getProductId(), l.getQuantite(), l.getPrixUnitaire()))
                .toList();

        return new OrderResponse(
                order.getId(), order.getUserId(), order.getDate(),
                order.getStatut(), order.getMontantTotal(), lignes);
    }
}