package com.ecom.stock.dto;

public record StockResponseDTO(Long id, Long productId, int quantite, int seuilAlerte) {}