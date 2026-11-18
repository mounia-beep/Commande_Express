package com.ecom.servicecommande.controller;

import com.ecom.servicecommande.dto.OrderLineRequest;
import com.ecom.servicecommande.dto.OrderLineResponse;
import com.ecom.servicecommande.dto.OrderLineUpdateRequest;
import com.ecom.servicecommande.service.OrderLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders/{orderId}/lignes")
@RequiredArgsConstructor
public class OrderLineController {

    private final OrderLineService orderLineService;

    @GetMapping
    public List<OrderLineResponse> getAll(@PathVariable Long orderId) {
        return orderLineService.getAll(orderId);
    }

    @GetMapping("/{ligneId}")
    public OrderLineResponse getById(@PathVariable Long orderId, @PathVariable Long ligneId) {
        return orderLineService.getById(orderId, ligneId);
    }

    @PostMapping
    public ResponseEntity<OrderLineResponse> add(@PathVariable Long orderId,
                                                 @Valid @RequestBody OrderLineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderLineService.add(orderId, request));
    }

    @PutMapping("/{ligneId}")
    public OrderLineResponse update(@PathVariable Long orderId, @PathVariable Long ligneId,
                                    @Valid @RequestBody OrderLineUpdateRequest request) {
        return orderLineService.update(orderId, ligneId, request);
    }

    @DeleteMapping("/{ligneId}")
    public ResponseEntity<Void> delete(@PathVariable Long orderId, @PathVariable Long ligneId) {
        orderLineService.delete(orderId, ligneId);
        return ResponseEntity.noContent().build();
    }
}