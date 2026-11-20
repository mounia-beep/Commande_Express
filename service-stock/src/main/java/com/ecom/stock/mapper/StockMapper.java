package com.ecom.stock.mapper;

import com.ecom.stock.dto.StockRequestDTO;
import com.ecom.stock.dto.StockResponseDTO;
import com.ecom.stock.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public Stock toEntity(StockRequestDTO dto) {
        Stock s = new Stock();
        updateEntity(s, dto);
        return s;
    }

    public void updateEntity(Stock s, StockRequestDTO dto) {
        s.setProductId(dto.productId());
        s.setQuantite(dto.quantite());
        s.setSeuilAlerte(dto.seuilAlerte());
    }

    public StockResponseDTO toDto(Stock s) {
        return new StockResponseDTO(s.getId(), s.getProductId(), s.getQuantite(), s.getSeuilAlerte());
    }
}