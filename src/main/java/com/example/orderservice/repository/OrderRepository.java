package com.example.orderservice.repository;

import com.example.orderservice.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository {

    Order getOrderById(UUID id);
    List<Order> getOrders();
    Order createOrder(Order newOrder);
    Order updateOrder(Order updatedOrder);
    void deleteOrder(UUID id);
}
