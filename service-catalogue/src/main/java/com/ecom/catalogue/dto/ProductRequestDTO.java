package com.ecom.catalogue.dto;

import com.ecom.catalogue.entity.StatutProduit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0") BigDecimal prix,
        String description,
        String categorie,
        StatutProduit statut
) {}