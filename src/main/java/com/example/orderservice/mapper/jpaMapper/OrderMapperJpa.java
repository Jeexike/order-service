package com.example.orderservice.mapper.jpaMapper;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderMapperJpa implements OrderMapper {
    private final ApplicationContext applicationContext;

    @Override
    public OrderEntity mapOrderRequestToOrderEntity(OrderRequest orderRequest) {
        OrderEntity orderEntity = applicationContext.getBean(OrderEntity.class);
        orderEntity.setName(orderRequest.getName());
        orderEntity.setSource(orderRequest.getSource());
        orderEntity.setDestination(orderRequest.getDestination());

        return orderEntity;
    }

    @Override
    public OrderEntity mapOrderRequestToOrderEntity(UUID id, OrderRequest orderRequest) {
        OrderEntity orderEntity = applicationContext.getBean(OrderEntity.class);
        orderEntity.setId(id);
        orderEntity.setName(orderRequest.getName());
        orderEntity.setSource(orderRequest.getSource());
        orderEntity.setDestination(orderRequest.getDestination());

        return orderEntity;
    }

    @Override
    public OrderResponse mapOrderEntityToOrderResponse(OrderEntity orderEntity) {
        OrderResponse orderResponse = applicationContext.getBean(OrderResponse.class);
        orderResponse.setId(orderEntity.getId());
        orderResponse.setName(orderEntity.getName());
        orderResponse.setSource(orderEntity.getSource());
        orderResponse.setDestination(orderEntity.getDestination());
        orderResponse.setCreatedAt(orderEntity.getCreatedAt());
        orderResponse.setUpdatedAt(orderEntity.getUpdatedAt());

        return orderResponse;
    }

    @Override
    public List<OrderResponse> mapOrderEntityToOrderResponse(List<OrderEntity> orderEntities) {
        List<OrderResponse> orderResponseList = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            OrderResponse orderResponse = mapOrderEntityToOrderResponse(orderEntity);
            orderResponseList.add(orderResponse);
        }

        return orderResponseList;
    }
}
