package com.example.orderservice.service.jpaService;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.PartnerRepository;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
public class OrderServiceJpa implements OrderService {

    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse getOrderById(UUID id) {
        return orderMapper.toOrderResponse(
                orderRepository.getOrderById(id)
        );
    }

    @Override
    @Transactional
    public List<OrderResponse> getOrders() {
        return orderMapper.toOrderResponse(
                orderRepository.getOrders()
        );
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        OrderEntity orderEntity = orderMapper.toOrderEntity(orderRequest);
        orderEntity.setPartner(partnerRepository.getPartnerById(orderRequest.getPartnerId()));

        OrderEntity saved = orderRepository.createOrder(orderEntity);
        return orderMapper.toOrderResponse(saved);
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