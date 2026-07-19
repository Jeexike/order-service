package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {

    public OrderEntity toOrderEntity(OrderRequest orderRequest) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setName(orderRequest.getName());
        orderEntity.setSource(orderRequest.getSource());
        orderEntity.setDestination(orderRequest.getDestination());
        return orderEntity;
    }

    public OrderEntity toOrderEntity(UUID id, OrderRequest orderRequest) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(id);
        orderEntity.setName(orderRequest.getName());
        orderEntity.setSource(orderRequest.getSource());
        orderEntity.setDestination(orderRequest.getDestination());
        return orderEntity;
    }

    public OrderResponse toOrderResponse(OrderEntity orderEntity) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(orderEntity.getId());
        orderResponse.setName(orderEntity.getName());
        orderResponse.setSource(orderEntity.getSource());
        orderResponse.setDestination(orderEntity.getDestination());
        orderResponse.setCreatedAt(orderEntity.getCreatedAt());
        orderResponse.setUpdatedAt(orderEntity.getUpdatedAt());

        orderResponse.setPartnerId(orderEntity.getPartner().getId());

        return orderResponse;
    }

    public List<OrderResponse> toOrderResponse(List<OrderEntity> orderEntities) {
        return orderEntities.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }
}