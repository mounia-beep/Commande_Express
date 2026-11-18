package com.ecom.catalogue.repository;

import com.ecom.catalogue.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNomIgnoreCase(String nom);
    boolean existsByNomIgnoreCase(String nom);
    List<Product> findByCategorieIgnoreCase(String categorie);
}