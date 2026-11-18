package com.ecom.catalogue.service;

import com.ecom.catalogue.dto.ProductRequestDTO;
import com.ecom.catalogue.dto.ProductResponseDTO;
import com.ecom.catalogue.entity.Product;
import com.ecom.catalogue.exception.DuplicateResourceException;
import com.ecom.catalogue.exception.ResourceNotFoundException;
import com.ecom.catalogue.mapper.ProductMapper;
import com.ecom.catalogue.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        if (repository.existsByNomIgnoreCase(dto.nom().trim())) {
            throw new DuplicateResourceException("Produit déjà existant : " + dto.nom());
        }
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll(String categorie) {
        List<Product> products = (categorie == null || categorie.isBlank())
                ? repository.findAll()
                : repository.findByCategorieIgnoreCase(categorie.trim());
        return products.stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        return mapper.toDto(getOrThrow(id));
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product product = getOrThrow(id);
        repository.findByNomIgnoreCase(dto.nom().trim())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(o -> {
                    throw new DuplicateResourceException("Produit déjà existant : " + dto.nom());
                });
        mapper.updateEntity(product, dto);
        return mapper.toDto(repository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Product getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + id));
    }
}