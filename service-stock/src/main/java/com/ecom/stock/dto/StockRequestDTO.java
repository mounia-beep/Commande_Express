package com.ecom.stock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockRequestDTO(
        @NotNull(message = "Le productId est obligatoire") Long productId,
        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 0, message = "La quantité doit être supérieure ou égale à 0") Integer quantite,
        @NotNull(message = "Le seuil d'alerte est obligatoire")
        @Min(value = 0, message = "Le seuil d'alerte doit être supérieur ou égal à 0") Integer seuilAlerte
) {}