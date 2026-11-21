package com.ecom.stock.service;

import com.ecom.stock.client.ProductClient;
import com.ecom.stock.dto.ProductClientDTO;
import com.ecom.stock.dto.StockDetailsDTO;
import com.ecom.stock.dto.StockRequestDTO;
import com.ecom.stock.dto.StockResponseDTO;
import com.ecom.stock.entity.Stock;
import com.ecom.stock.event.StockEventPublisher;                  // V5 : import qui manquait
import com.ecom.stock.exception.DuplicateResourceException;
import com.ecom.stock.exception.ResourceNotFoundException;
import com.ecom.stock.exception.ServiceUnavailableException;
import com.ecom.stock.mapper.StockMapper;
import com.ecom.stock.repository.StockRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository repository;
    private final StockMapper mapper;
    private final ProductClient productClient;
    private final StockEventPublisher eventPublisher;             // V5

    public StockService(StockRepository repository, StockMapper mapper,
                        ProductClient productClient,              // V5 : virgule qui manquait
                        StockEventPublisher eventPublisher) {     // V5
        this.repository = repository;
        this.mapper = mapper;
        this.productClient = productClient;
        this.eventPublisher = eventPublisher;                     // V5
    }

    @Transactional
    public StockResponseDTO create(StockRequestDTO dto) {
        if (repository.existsByProductId(dto.productId())) {
            throw new DuplicateResourceException("Un stock existe déjà pour le produit : " + dto.productId());
        }
        fetchProduct(dto.productId());                            // V4 : validation distante
        Stock saved = repository.save(mapper.toEntity(dto));      // V5
        alerterSiNecessaire(saved);                               // V5
        return mapper.toDto(saved);                               // V5
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

    // V4 : consultation enrichie (agrégation)
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
        if (!stock.getProductId().equals(dto.productId())) {      // V4 : on revérifie seulement si le produit change
            fetchProduct(dto.productId());
        }
        mapper.updateEntity(stock, dto);
        Stock saved = repository.save(stock);                     // V5
        alerterSiNecessaire(saved);                               // V5
        return mapper.toDto(saved);                               // V5
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Stock getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable : " + id));
    }

    // V4 : appel Feign + traduction des erreurs
    private ProductClientDTO fetchProduct(Long productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Produit introuvable dans le catalogue : " + productId);
        } catch (FeignException e) {
            throw new ServiceUnavailableException("Service catalogue indisponible, réessayez plus tard");
        }
    }

    // V5 : publie un événement si le stock est bas
    private void alerterSiNecessaire(Stock s) {
        if (s.getQuantite() <= s.getSeuilAlerte()) {
            eventPublisher.publishStockAlerte(s);
        }
    }
}