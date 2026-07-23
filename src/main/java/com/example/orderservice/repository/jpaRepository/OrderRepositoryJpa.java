package com.example.orderservice.repository.jpaRepository;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryJpa implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderEntity getOrderById(UUID id) {
        return orderJpaRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public List<OrderEntity> getOrders() {
        return orderJpaRepository.findAll();
    }

    @Override
    public List<OrderEntity> getOrdersByPartnerId(UUID partnerId) {
        return orderJpaRepository.findByPartnerId(partnerId);
    }

    @Override
    public OrderEntity createOrder(OrderEntity newOrder) {
        return orderJpaRepository.save(newOrder);
    }

    @Override
    public OrderEntity updateOrder(OrderEntity updatedOrder) {
        OrderEntity existing = orderJpaRepository
                .findById(updatedOrder.getId())
                .orElseThrow(() -> new OrderNotFoundException(updatedOrder.getId()));

        existing.setName(updatedOrder.getName());
        existing.setSource(updatedOrder.getSource());
        existing.setDestination(updatedOrder.getDestination());
        existing.setPartner(updatedOrder.getPartner());

        return orderJpaRepository.save(existing);
    }

    @Override
    public void deleteOrder(UUID id) {
        OrderEntity existing = orderJpaRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        orderJpaRepository.delete(existing);
    }
}
