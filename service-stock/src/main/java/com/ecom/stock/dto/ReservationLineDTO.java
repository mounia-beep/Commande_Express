package com.ecom.stock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationLineDTO(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantite
) { }