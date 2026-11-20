package com.ecom.catalogue.dto;

import com.ecom.catalogue.entity.StatutProduit;

import java.math.BigDecimal;

public record ProductResponseDTO(Long id, String nom, BigDecimal prix,
                                 String description, String categorie, StatutProduit statut) {}