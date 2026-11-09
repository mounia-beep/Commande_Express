package com.ecom.servicecommande.repository;

import com.ecom.servicecommande.entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
}