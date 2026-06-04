package com.example.orderservice.repository.jpaRepository;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderRepositoryJpa implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderEntity getOrderById(UUID id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order Not Found: " + id));
    }

    @Override
    public List<OrderEntity> getOrders() {
        return orderJpaRepository.findAll();
    }

    @Override
    public OrderEntity createOrder(OrderEntity newOrder) {
        return orderJpaRepository.save(newOrder);
    }

    @Override
    public OrderEntity updateOrder(OrderEntity updatedOrder) {
        OrderEntity existing = orderJpaRepository.findById(updatedOrder.getId())
                .orElseThrow(() -> new RuntimeException("Order Not Found: " + updatedOrder.getId()));

        existing.setName(updatedOrder.getName());
        existing.setSource(updatedOrder.getSource());
        existing.setDestination(updatedOrder.getDestination());

        return orderJpaRepository.save(existing);
    }

    @Override
    public void deleteOrder(UUID id) {
        orderJpaRepository.deleteById(id);
    }
}
