package com.ecom.servicecommande.repository;

import com.ecom.servicecommande.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}