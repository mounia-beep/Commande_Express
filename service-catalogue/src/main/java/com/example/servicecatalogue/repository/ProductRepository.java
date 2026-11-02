package com.example.servicecatalogue.repository;

import com.example.servicecatalogue.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}