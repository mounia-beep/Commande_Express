package com.ecom.stock.controller;

import com.ecom.stock.dto.StockRequestDTO;
import com.ecom.stock.dto.StockResponseDTO;
import com.ecom.stock.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import com.ecom.stock.dto.StockDetailsDTO;
import com.ecom.stock.dto.CreationResult;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService service;
    @Value("${server.port}")
    private String port;

    @GetMapping("/instance")
    public String instance() {
        return "Réponse de l'instance sur le port " + port;
    }

    public StockController(StockService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StockResponseDTO> create(
            @Valid @RequestBody StockRequestDTO dto,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        CreationResult result = service.createIdempotent(dto, idempotencyKey);
        if (!result.created()) {
            return ResponseEntity.ok()
                    .header("Idempotent-Replayed", "true")
                    .body(result.stock());
        }
        return ResponseEntity.created(URI.create("/stocks/" + result.stock().id())).body(result.stock());
    }

    @GetMapping
    public List<StockResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public StockResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/product/{productId}")
    public StockResponseDTO findByProductId(@PathVariable Long productId) {
        return service.findByProductId(productId);
    }

    @PutMapping("/{id}")
    public StockResponseDTO update(@PathVariable Long id, @Valid @RequestBody StockRequestDTO dto) {
        return service.update(id, dto);
    }
    @GetMapping("/product/{productId}/details")
    public StockDetailsDTO findDetails(@PathVariable Long productId) {
        return service.findDetailsByProductId(productId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}