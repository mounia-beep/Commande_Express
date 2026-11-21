package com.ecom.stock.client;

import com.ecom.stock.dto.ProductClientDTO;
import com.ecom.stock.exception.ResourceNotFoundException;
import com.ecom.stock.exception.ServiceUnavailableException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CatalogueGateway {

    private static final Logger log = LoggerFactory.getLogger(CatalogueGateway.class);

    private final ProductClient productClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public CatalogueGateway(ProductClient productClient, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.productClient = productClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    // Appel Feign n°1 : validation. Pas de mode dégradé : si le catalogue est KO, on refuse (503).
    public ProductClientDTO getProductForValidation(Long productId) {
        return call("catalogue-validation", productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable dans le catalogue : " + productId));
    }

    // Appel Feign n°2 : consultation enrichie. Mode dégradé : si le catalogue est KO, on renvoie null.
    public ProductClientDTO getProductForDetails(Long productId) {
        try {
            return call("catalogue-details", productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable dans le catalogue : " + productId));
        } catch (ServiceUnavailableException e) {
            log.warn("Mode dégradé : détails du produit {} renvoyés sans les infos du catalogue", productId);
            return null;
        }
    }

    private Optional<ProductClientDTO> call(String breakerName, Long productId) {
        return circuitBreakerFactory.create(breakerName).run(
                () -> {
                    try {
                        return Optional.of(productClient.getProductById(productId));
                    } catch (FeignException.NotFound e) {
                        return Optional.<ProductClientDTO>empty();   // 404 = réponse valide, pas une panne
                    }
                },
                throwable -> {                                       // fallback : timeout, panne, circuit ouvert
                    log.warn("[{}] appel catalogue en échec : {}", breakerName, throwable.toString());
                    throw new ServiceUnavailableException("Service catalogue indisponible, réessayez plus tard");
                });
    }
}