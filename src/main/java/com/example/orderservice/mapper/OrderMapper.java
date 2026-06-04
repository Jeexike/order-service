package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;

import java.util.List;
import java.util.UUID;

public interface OrderMapper {

    OrderEntity mapOrderRequestToOrderEntity(OrderRequest orderRequest);
    OrderEntity mapOrderRequestToOrderEntity(UUID id, OrderRequest orderRequest);
    OrderResponse mapOrderEntityToOrderResponse(OrderEntity orderEntity);
    List<OrderResponse> mapOrderEntityToOrderResponse(List<OrderEntity> orderEntities);
}