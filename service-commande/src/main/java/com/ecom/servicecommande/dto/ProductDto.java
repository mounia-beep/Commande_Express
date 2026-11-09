package com.ecom.servicecommande.dto;

import java.math.BigDecimal;

public record ProductDto(Long id, String nom, BigDecimal prix, String statut) { }