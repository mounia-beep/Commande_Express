package com.example.servicecatalogue.dto;

import com.example.servicecatalogue.entity.Product;
import com.example.servicecatalogue.entity.StatutProduit;
import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String nom,
        BigDecimal prix,
        String description,
        String categorie,
        StatutProduit statut
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getNom(), p.getPrix(),
                p.getDescription(), p.getCategorie(), p.getStatut());
    }
}
