package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.PartnerRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
    private final OrderMapper orderMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public OrderResponse getOrderById(UUID id) {
        return orderMapper.toOrderResponse(orderRepository.getOrderById(id));
    }

    @Override
    @Transactional
    public List<OrderResponse> getOrders() {
        return orderMapper.toOrderResponse(orderRepository.getOrders());
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        OrderEntity orderEntity = orderMapper.toOrderEntity(orderRequest);
        orderEntity.setPartner(partnerRepository.getPartnerById(orderRequest.getPartnerId()));

        OrderEntity saved = orderRepository.createOrder(orderEntity);
        return orderMapper.toOrderResponse(withDbTimestamps(saved));
    }

    private OrderEntity withDbTimestamps(OrderEntity entity) {
        if (entity.getCreatedAt() != null && entity.getUpdatedAt() != null) {
            return entity;
        }
        if (entityManager.contains(entity)) {
            entityManager.flush();
            entityManager.refresh(entity);
            return entity;
        }
        return orderRepository.getOrderById(entity.getId());
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(UUID id, OrderRequest orderRequest) {
        OrderEntity orderEntity = orderMapper.toOrderEntity(id, orderRequest);
        orderEntity.setPartner(partnerRepository.getPartnerById(orderRequest.getPartnerId()));

        OrderEntity updated = orderRepository.updateOrder(orderEntity);
        return orderMapper.toOrderResponse(updated);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        orderRepository.deleteOrder(id);
    }
}
