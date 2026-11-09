package com.ecom.servicecommande.service;

import com.ecom.servicecommande.client.ProductClient;
import com.ecom.servicecommande.client.StockClient;
import com.ecom.servicecommande.client.UserClient;
import com.ecom.servicecommande.dto.OrderLineRequest;
import com.ecom.servicecommande.dto.OrderRequest;
import com.ecom.servicecommande.dto.OrderResponse;
import com.ecom.servicecommande.dto.ProductDto;
import com.ecom.servicecommande.dto.UserDto;
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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final StockClient stockClient;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse create(OrderRequest request) {

        // ③ Vérifier l'utilisateur
        UserDto user = userClient.getUser(request.userId());
        if (user == null) {
            throw new ResourceNotFoundException("Utilisateur introuvable : " + request.userId());
        }
        if (!user.actif()) {
            throw new BusinessException("Utilisateur inactif : " + request.userId());
        }

        // Préparer la commande
        Order order = new Order();
        order.setUserId(request.userId());
        order.setDate(LocalDateTime.now());
        order.setStatut(OrderStatus.EN_ATTENTE_STOCK);

        // ④ + ⑤ Vérifier les produits, récupérer les prix, calculer le total
        BigDecimal total = BigDecimal.ZERO;
        for (OrderLineRequest ligne : request.lignes()) {
            ProductDto product = productClient.getProduct(ligne.productId());
            if (product == null) {
                throw new ResourceNotFoundException("Produit introuvable : " + ligne.productId());
            }

            OrderLine orderLine = new OrderLine();
            orderLine.setOrder(order);
            orderLine.setProductId(ligne.productId());
            orderLine.setQuantite(ligne.quantite());
            orderLine.setPrixUnitaire(product.prix());
            order.getLignes().add(orderLine);

            total = total.add(product.prix().multiply(BigDecimal.valueOf(ligne.quantite())));
        }
        order.setMontantTotal(total);

        // ⑥ Enregistrer en EN_ATTENTE_STOCK (on a besoin de l'id pour le stock)
        order = orderRepository.save(order);

        // ⑦ Réserver tout ou rien
        boolean reserve = stockClient.reserve(order.getId(), request.lignes());
        order.setStatut(reserve ? OrderStatus.CONFIRMEE : OrderStatus.ANNULEE);
        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return orderMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = findOrThrow(id);

        if (order.getStatut() == OrderStatus.ANNULEE) {
            throw new BusinessException("La commande " + id + " est déjà annulée");
        }

        stockClient.restore(order.getId());   // le stock ne rend qu'une seule fois
        order.setStatut(OrderStatus.ANNULEE);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    private Order findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable : " + id));
    }
}