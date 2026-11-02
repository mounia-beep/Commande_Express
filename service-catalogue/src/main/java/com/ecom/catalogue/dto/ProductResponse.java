package com.ecom.catalogue.dto;

import com.ecom.catalogue.entity.Product;
import com.ecom.catalogue.entity.StatutProduit;
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
