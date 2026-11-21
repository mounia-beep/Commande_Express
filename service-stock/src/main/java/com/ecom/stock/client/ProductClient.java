package com.ecom.stock.client;

import com.ecom.stock.dto.ProductClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-catalogue")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductClientDTO getProductById(@PathVariable("id") Long id);
}