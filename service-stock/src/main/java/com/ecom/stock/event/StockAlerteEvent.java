package com.ecom.stock.event;

public record StockAlerteEvent(String eventId, Long productId, int quantite, int seuilAlerte) {}