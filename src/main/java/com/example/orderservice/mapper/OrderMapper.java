package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderMapper {

    public OrderEntity mapOrderRequestToOrderEntity(OrderRequest orderRequest) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setName(orderRequest.getName());
        orderEntity.setSource(orderRequest.getSource());
        orderEntity.setDestination(orderRequest.getDestination());
        return orderEntity;
    }

    public OrderEntity mapOrderRequestToOrderEntity(UUID id, OrderRequest orderRequest) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(id);
        orderEntity.setName(orderRequest.getName());
        orderEntity.setSource(orderRequest.getSource());
        orderEntity.setDestination(orderRequest.getDestination());
        return orderEntity;
    }

    public OrderResponse mapOrderEntityToOrderResponse(OrderEntity orderEntity) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(orderEntity.getId());
        orderResponse.setName(orderEntity.getName());
        orderResponse.setSource(orderEntity.getSource());
        orderResponse.setDestination(orderEntity.getDestination());
        orderResponse.setCreatedAt(orderEntity.getCreatedAt());
        orderResponse.setUpdatedAt(orderEntity.getUpdatedAt());

        if (orderEntity.getPartner() != null) {
            orderResponse.setPartnerId(orderEntity.getPartner().getId());
        }

        return orderResponse;
    }

    public List<OrderResponse> mapOrderEntityToOrderResponse(List<OrderEntity> orderEntities) {
        List<OrderResponse> orderResponseList = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            orderResponseList.add(mapOrderEntityToOrderResponse(orderEntity));
        }
        return orderResponseList;
    }
}