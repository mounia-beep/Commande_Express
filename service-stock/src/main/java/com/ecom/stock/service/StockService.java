package com.ecom.stock.service;

import com.ecom.stock.client.ProductClient;                       // NOUVEAU
import com.ecom.stock.dto.ProductClientDTO;                       // NOUVEAU
import com.ecom.stock.dto.StockDetailsDTO;                        // NOUVEAU
import com.ecom.stock.dto.StockRequestDTO;
import com.ecom.stock.dto.StockResponseDTO;
import com.ecom.stock.entity.Stock;
import com.ecom.stock.exception.DuplicateResourceException;
import com.ecom.stock.exception.ResourceNotFoundException;
import com.ecom.stock.exception.ServiceUnavailableException;      // NOUVEAU
import com.ecom.stock.mapper.StockMapper;
import com.ecom.stock.repository.StockRepository;
import feign.FeignException;                                      // NOUVEAU
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository repository;
    private final StockMapper mapper;
    private final ProductClient productClient;                    // NOUVEAU

    public StockService(StockRepository repository, StockMapper mapper,
                        ProductClient productClient) {            // NOUVEAU
        this.repository = repository;
        this.mapper = mapper;
        this.productClient = productClient;
    }

    @Transactional
    public StockResponseDTO create(StockRequestDTO dto) {
        if (repository.existsByProductId(dto.productId())) {
            throw new DuplicateResourceException("Un stock existe déjà pour le produit : " + dto.productId());
        }
        fetchProduct(dto.productId());                            // NOUVEAU : validation distante
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

    // NOUVEAU : consultation enrichie (agrégation)
    @Transactional(readOnly = true)
    public StockDetailsDTO findDetailsByProductId(Long productId) {
        Stock stock = repository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun stock pour le produit : " + productId));
        ProductClientDTO produit = fetchProduct(productId);
        return mapper.toDetailsDto(stock, produit);
    }

    @Transactional
    public StockResponseDTO update(Long id, StockRequestDTO dto) {
        Stock stock = getOrThrow(id);
        repository.findByProductId(dto.productId())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(o -> {
                    throw new DuplicateResourceException("Un stock existe déjà pour le produit : " + dto.productId());
                });
        if (!stock.getProductId().equals(dto.productId())) {      // NOUVEAU : on revérifie seulement si le produit change
            fetchProduct(dto.productId());
        }
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

    // NOUVEAU : appel Feign + traduction des erreurs
    private ProductClientDTO fetchProduct(Long productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Produit introuvable dans le catalogue : " + productId);
        } catch (FeignException e) {
            throw new ServiceUnavailableException("Service catalogue indisponible, réessayez plus tard");
        }
    }
}