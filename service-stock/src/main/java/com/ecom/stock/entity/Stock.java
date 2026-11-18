package com.ecom.stock.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence distante vers le produit (service-catalogue) : simple identifiant, pas de @ManyToOne
    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private int seuilAlerte;

    public Stock() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public int getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(int seuilAlerte) { this.seuilAlerte = seuilAlerte; }
}