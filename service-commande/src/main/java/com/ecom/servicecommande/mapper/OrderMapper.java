package com.ecom.servicecommande.mapper;

import com.ecom.servicecommande.dto.OrderLineResponse;
import com.ecom.servicecommande.dto.OrderResponse;
import com.ecom.servicecommande.entity.Order;
import com.ecom.servicecommande.entity.OrderLine;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderLineResponse> lignes = order.getLignes().stream()
                .map(this::toLineResponse)
                .toList();

        return new OrderResponse(
                order.getId(), order.getUserId(), order.getDate(),
                order.getStatut(), order.getMontantTotal(), lignes);
    }

    public OrderLineResponse toLineResponse(OrderLine l) {
        return new OrderLineResponse(l.getId(), l.getProductId(), l.getQuantite(), l.getPrixUnitaire());
    }
}