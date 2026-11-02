package com.ecom.catalogue.dto;

import com.ecom.catalogue.entity.StatutProduit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotNull(message = "Le prix est obligatoire")
        @Positive(message = "Le prix doit être positif")
        BigDecimal prix,

        @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
        String description,

        @NotBlank(message = "La catégorie est obligatoire")
        String categorie,

        StatutProduit statut
) {
}