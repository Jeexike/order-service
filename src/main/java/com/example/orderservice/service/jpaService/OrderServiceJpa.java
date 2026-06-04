package com.example.orderservice.service.jpaService;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class OrderServiceJpa implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceJpa(@Qualifier("jpaService") OrderRepository orderRepository, @Qualifier("jpaMapper") OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        return orderMapper.mapOrderEntityToOrderResponse(orderRepository.getOrderById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders() {
        return orderMapper.mapOrderEntityToOrderResponse(orderRepository.getOrders());
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        return orderMapper.mapOrderEntityToOrderResponse(orderRepository.createOrder(orderMapper.mapOrderRequestToOrderEntity(orderRequest)));
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(UUID id, OrderRequest orderRequest) {
        return orderMapper.mapOrderEntityToOrderResponse(orderRepository.updateOrder(orderMapper.mapOrderRequestToOrderEntity(id, orderRequest)));
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        orderRepository.deleteOrder(id);
    }
}
