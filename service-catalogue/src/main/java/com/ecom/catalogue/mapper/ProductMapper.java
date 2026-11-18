package com.ecom.catalogue.mapper;

import com.ecom.catalogue.dto.ProductRequestDTO;
import com.ecom.catalogue.dto.ProductResponseDTO;
import com.ecom.catalogue.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto) {
        Product p = new Product();
        updateEntity(p, dto);
        return p;
    }

    public void updateEntity(Product p, ProductRequestDTO dto) {
        p.setNom(dto.nom().trim());
        p.setPrix(dto.prix());
        p.setDescription(dto.description());
        p.setCategorie(dto.categorie() == null ? null : dto.categorie().trim());
        if (dto.statut() != null) {
            p.setStatut(dto.statut());
        }
    }

    public ProductResponseDTO toDto(Product p) {
        return new ProductResponseDTO(p.getId(), p.getNom(), p.getPrix(),
                p.getDescription(), p.getCategorie(), p.getStatut());
    }
}