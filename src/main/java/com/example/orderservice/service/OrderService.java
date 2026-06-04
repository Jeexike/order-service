package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse getOrderById(UUID id);
    List<OrderResponse> getOrders();
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse updateOrder(UUID id, OrderRequest orderRequest);
    void deleteOrder(UUID id);
}
