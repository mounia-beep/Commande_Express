package com.ecom.stock.dto;

import java.math.BigDecimal;

public record ProductClientDTO(Long id, String nom, BigDecimal prix,
                               String description, String categorie, String statut) {}