package com.ecom.stock.service;

import com.ecom.stock.client.CatalogueGateway;
import com.ecom.stock.dto.CreationResult;
import com.ecom.stock.dto.ProductClientDTO;
import com.ecom.stock.dto.StockDetailsDTO;
import com.ecom.stock.dto.StockRequestDTO;
import com.ecom.stock.dto.StockResponseDTO;
import com.ecom.stock.entity.IdempotencyRecord;
import com.ecom.stock.entity.Stock;
import com.ecom.stock.event.StockEventPublisher;
import com.ecom.stock.exception.DuplicateResourceException;
import com.ecom.stock.exception.ResourceNotFoundException;
import com.ecom.stock.mapper.StockMapper;
import com.ecom.stock.repository.IdempotencyRepository;
import com.ecom.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository repository;
    private final StockMapper mapper;
    private final CatalogueGateway catalogue;                     // V6 : remplace ProductClient
    private final StockEventPublisher eventPublisher;
    private final IdempotencyRepository idempotencyRepository;    // V6

    public StockService(StockRepository repository, StockMapper mapper,
                        CatalogueGateway catalogue,
                        StockEventPublisher eventPublisher,
                        IdempotencyRepository idempotencyRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.catalogue = catalogue;
        this.eventPublisher = eventPublisher;
        this.idempotencyRepository = idempotencyRepository;
    }

    // V6 : création idempotente
    @Transactional
    public CreationResult createIdempotent(StockRequestDTO dto, String idempotencyKey) {
        boolean hasKey = idempotencyKey != null && !idempotencyKey.isBlank();
        if (hasKey) {
            var existing = idempotencyRepository.findById(idempotencyKey);
            if (existing.isPresent()) {                           // clé déjà vue : on rejoue la réponse
                return new CreationResult(findById(existing.get().getStockId()), false);
            }
        }
        StockResponseDTO created = create(dto);
        if (hasKey) {
            idempotencyRepository.save(new IdempotencyRecord(idempotencyKey, created.id()));
        }
        return new CreationResult(created, true);
    }

    @Transactional
    public StockResponseDTO create(StockRequestDTO dto) {
        if (repository.existsByProductId(dto.productId())) {
            throw new DuplicateResourceException("Un stock existe déjà pour le produit : " + dto.productId());
        }
        catalogue.getProductForValidation(dto.productId());       // V4 + V6 : appel Feign protégé n°1
        Stock saved = repository.save(mapper.toEntity(dto));
        alerterSiNecessaire(saved);
        return mapper.toDto(saved);
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

    // V4 + V6 : consultation enrichie, avec mode dégradé (appel Feign protégé n°2)
    @Transactional(readOnly = true)
    public StockDetailsDTO findDetailsByProductId(Long productId) {
        Stock stock = repository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun stock pour le produit : " + productId));
        ProductClientDTO produit = catalogue.getProductForDetails(productId);   // peut valoir null (catalogue KO)
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
        if (!stock.getProductId().equals(dto.productId())) {
            catalogue.getProductForValidation(dto.productId());
        }
        mapper.updateEntity(stock, dto);
        Stock saved = repository.save(stock);
        alerterSiNecessaire(saved);
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Stock getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable : " + id));
    }

    // V5 : publie un événement si le stock est bas
    private void alerterSiNecessaire(Stock s) {
        if (s.getQuantite() <= s.getSeuilAlerte()) {
            eventPublisher.publishStockAlerte(s);
        }
    }
}