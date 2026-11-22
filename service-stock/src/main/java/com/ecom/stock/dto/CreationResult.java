package com.ecom.stock.dto;

// created = true : nouvelle création (201) ; false : rejeu d'une clé déjà vue (200)
public record CreationResult(StockResponseDTO stock, boolean created) {}