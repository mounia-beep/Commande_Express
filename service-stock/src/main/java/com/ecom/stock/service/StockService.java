package com.ecom.stock.service;

import com.ecom.stock.dto.StockRequestDTO;
import com.ecom.stock.dto.StockResponseDTO;
import com.ecom.stock.entity.Stock;
import com.ecom.stock.exception.DuplicateResourceException;
import com.ecom.stock.exception.ResourceNotFoundException;
import com.ecom.stock.mapper.StockMapper;
import com.ecom.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository repository;
    private final StockMapper mapper;

    public StockService(StockRepository repository, StockMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public StockResponseDTO create(StockRequestDTO dto) {
        if (repository.existsByProductId(dto.productId())) {
            throw new DuplicateResourceException("Un stock existe déjà pour le produit : " + dto.productId());
        }
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public List<StockResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StockResponseDTO findById(Long id) {
        return mapper.toDto(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public StockResponseDTO findByProductId(Long productId) {
        return repository.findByProductId(productId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun stock pour le produit : " + productId));
    }

    @Transactional
    public StockResponseDTO update(Long id, StockRequestDTO dto) {
        Stock stock = getOrThrow(id);
        repository.findByProductId(dto.productId())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(o -> {
                    throw new DuplicateResourceException("Un stock existe déjà pour le produit : " + dto.productId());
                });
        mapper.updateEntity(stock, dto);
        return mapper.toDto(repository.save(stock));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Stock getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable : " + id));
    }
}