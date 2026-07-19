package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;
    private final OrderValidator orderValidator;

    @Override
    public OrderResponse getOrder(UUID id) {
        return orderService.getOrderById(id);
    }

    @Override
    public List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        orderValidator.validateOrderRequest(orderRequest);
        return orderService.createOrder(orderRequest);
    }

    @Override
    public OrderResponse updateOrder(UUID id, OrderRequest orderRequest) {
        orderValidator.validateOrderRequest(orderRequest);
        return orderService.updateOrder(id, orderRequest);
    }

    @Override
    public void deleteOrder(UUID id) {
        orderService.deleteOrder(id);
    }
}