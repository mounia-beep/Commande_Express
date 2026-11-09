package com.ecom.servicecommande.entity;

import com.ecom.servicecommande.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")          // "order" est un mot réservé en SQL
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private OrderStatus statut;

    private BigDecimal montantTotal;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lignes = new ArrayList<>();
}