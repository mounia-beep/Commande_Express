package com.ecom.servicecommande.client;

import com.ecom.servicecommande.dto.ProductDto;

public interface ProductClient {
    ProductDto getProduct(Long id);
}