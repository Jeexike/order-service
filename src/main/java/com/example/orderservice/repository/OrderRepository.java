package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderEntity;
import java.util.List;
import java.util.UUID;

public interface OrderRepository {

    OrderEntity getOrderById(UUID id);

    List<OrderEntity> getOrders();

    List<OrderEntity> getOrdersByPartnerId(UUID partnerId);

    OrderEntity createOrder(OrderEntity newOrder);

    OrderEntity updateOrder(OrderEntity updatedOrder);

    void deleteOrder(UUID id);
}
