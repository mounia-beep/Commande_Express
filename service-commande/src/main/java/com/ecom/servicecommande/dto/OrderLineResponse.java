package com.ecom.servicecommande.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        Long productId,
        Integer quantite,
        BigDecimal prixUnitaire
) { }