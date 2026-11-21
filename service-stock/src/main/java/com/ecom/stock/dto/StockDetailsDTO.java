package com.ecom.stock.dto;

public record StockDetailsDTO(Long stockId, int quantite, int seuilAlerte,
                              boolean enAlerte, ProductClientDTO produit) {}