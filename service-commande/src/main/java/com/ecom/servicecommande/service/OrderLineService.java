package com.ecom.servicecommande.service;

import com.ecom.servicecommande.client.ProductClient;
import com.ecom.servicecommande.dto.OrderLineRequest;
import com.ecom.servicecommande.dto.OrderLineResponse;
import com.ecom.servicecommande.dto.OrderLineUpdateRequest;
import com.ecom.servicecommande.dto.ProductDto;
import com.ecom.servicecommande.entity.Order;
import com.ecom.servicecommande.entity.OrderLine;
import com.ecom.servicecommande.enums.OrderStatus;
import com.ecom.servicecommande.exception.BusinessException;
import com.ecom.servicecommande.exception.ResourceNotFoundException;
import com.ecom.servicecommande.mapper.OrderMapper;
import com.ecom.servicecommande.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderLineResponse> getAll(Long orderId) {
        return findOrder(orderId).getLignes().stream()
                .map(orderMapper::toLineResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderLineResponse getById(Long orderId, Long ligneId) {
        return orderMapper.toLineResponse(findLine(findOrder(orderId), ligneId));
    }

    @Transactional
    public OrderLineResponse add(Long orderId, OrderLineRequest request) {
        Order order = findModifiableOrder(orderId);

        ProductDto product = productClient.getProduct(request.productId());
        if (product == null) {
            throw new ResourceNotFoundException("Produit introuvable : " + request.productId());
        }

        OrderLine line = new OrderLine();
        line.setOrder(order);
        line.setProductId(request.productId());
        line.setQuantite(request.quantite());
        line.setPrixUnitaire(product.prix());
        order.getLignes().add(line);

        recalculateTotal(order);
        orderRepository.saveAndFlush(order);
        return orderMapper.toLineResponse(line);
    }

    @Transactional
    public OrderLineResponse update(Long orderId, Long ligneId, OrderLineUpdateRequest request) {
        Order order = findModifiableOrder(orderId);
        OrderLine line = findLine(order, ligneId);

        line.setQuantite(request.quantite());

        recalculateTotal(order);
        orderRepository.saveAndFlush(order);
        return orderMapper.toLineResponse(line);
    }

    @Transactional
    public void delete(Long orderId, Long ligneId) {
        Order order = findModifiableOrder(orderId);
        OrderLine line = findLine(order, ligneId);

        if (order.getLignes().size() == 1) {
            throw new BusinessException("Une commande doit garder au moins une ligne");
        }

        order.getLignes().remove(line);   // supprimée en base grâce à orphanRemoval

        recalculateTotal(order);
        orderRepository.saveAndFlush(order);
    }

    // ----- méthodes privées -----

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : " + orderId));
    }

    private Order findModifiableOrder(Long orderId) {
        Order order = findOrder(orderId);
        if (order.getStatut() != OrderStatus.EN_ATTENTE_STOCK) {
            throw new BusinessException("La commande " + orderId + " n'est plus modifiable");
        }
        return order;
    }

    private OrderLine findLine(Order order, Long ligneId) {
        return order.getLignes().stream()
                .filter(l -> l.getId().equals(ligneId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ligne introuvable : " + ligneId));
    }

    private void recalculateTotal(Order order) {
        BigDecimal total = order.getLignes().stream()
                .map(l -> l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setMontantTotal(total);
    }
}