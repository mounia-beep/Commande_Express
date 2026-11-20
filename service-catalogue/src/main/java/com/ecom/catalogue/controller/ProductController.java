package com.ecom.catalogue.controller;

import com.ecom.catalogue.dto.ProductRequestDTO;
import com.ecom.catalogue.dto.ProductResponseDTO;
import com.ecom.catalogue.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/products/" + created.id())).body(created);
    }

    @GetMapping
    public List<ProductResponseDTO> findAll(@RequestParam(required = false) String categorie) {
        return service.findAll(categorie);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}