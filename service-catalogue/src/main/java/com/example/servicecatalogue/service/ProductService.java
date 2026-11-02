package com.example.servicecatalogue.service;

import com.example.servicecatalogue.dto.ProductRequest;
import com.example.servicecatalogue.dto.ProductResponse;
import com.example.servicecatalogue.entity.Product;
import com.example.servicecatalogue.entity.StatutProduit;
import com.example.servicecatalogue.exception.ResourceNotFoundException;
import com.example.servicecatalogue.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return ProductResponse.from(repository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(ProductResponse::from);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        apply(product, request);
        return ProductResponse.from(repository.save(product));
    }

    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Product findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id " + id));
    }

    private void apply(Product product, ProductRequest r) {
        product.setNom(r.nom());
        product.setPrix(r.prix());
        product.setDescription(r.description());
        product.setCategorie(r.categorie());
        product.setStatut(r.statut() != null ? r.statut() : StatutProduit.DISPONIBLE);
    }
}