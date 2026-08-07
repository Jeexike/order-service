package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {

    public OrderEntity toOrderEntity(OrderRequest orderRequest) {
        return OrderEntity.builder()
                .name(orderRequest.getName())
                .source(orderRequest.getSource())
                .destination(orderRequest.getDestination())
                .link(orderRequest.getLink())
                .build();
    }

    public OrderEntity toOrderEntity(UUID id, OrderRequest orderRequest) {
        return OrderEntity.builder()
                .id(id)
                .name(orderRequest.getName())
                .source(orderRequest.getSource())
                .destination(orderRequest.getDestination())
                .link(orderRequest.getLink())
                .build();
    }

    public OrderResponse toOrderResponse(OrderEntity orderEntity) {
        return OrderResponse.builder()
                .id(orderEntity.getId())
                .name(orderEntity.getName())
                .source(orderEntity.getSource())
                .destination(orderEntity.getDestination())
                .link(orderEntity.getLink())
                .partnerId(orderEntity.getPartner().getId())
                .createdAt(orderEntity.getCreatedAt())
                .updatedAt(orderEntity.getUpdatedAt())
                .build();
    }

    public List<OrderResponse> toOrderResponse(List<OrderEntity> orderEntities) {
        return orderEntities.stream().map(this::toOrderResponse).collect(Collectors.toList());
    }
}
