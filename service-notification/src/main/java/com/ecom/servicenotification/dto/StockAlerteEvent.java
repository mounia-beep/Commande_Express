package com.ecom.servicenotification.dto;

public record StockAlerteEvent(String eventId, Long productId, int quantite, int seuilAlerte) {}