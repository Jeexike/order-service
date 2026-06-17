package com.example.orderservice.service.jpaService;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.PartnerRepository;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceJpa implements OrderService {

    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse getOrderById(UUID id) {
        return orderMapper.mapOrderEntityToOrderResponse(
                orderRepository.getOrderById(id)
        );
    }

    @Override
    public List<OrderResponse> getOrders() {
        return orderMapper.mapOrderEntityToOrderResponse(
                orderRepository.getOrders()
        );
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        OrderEntity orderEntity = orderMapper.mapOrderRequestToOrderEntity(orderRequest);

        if (orderRequest.getPartnerId() != null) {
            PartnerEntity partner = partnerRepository.getPartnerById(orderRequest.getPartnerId());
            orderEntity.setPartner(partner);
        }

        OrderEntity saved = orderRepository.createOrder(orderEntity);
        return orderMapper.mapOrderEntityToOrderResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(UUID id, OrderRequest orderRequest) {
        orderRepository.getOrderById(id);

        OrderEntity orderEntity = orderMapper.mapOrderRequestToOrderEntity(id, orderRequest);

        if (orderRequest.getPartnerId() != null) {
            PartnerEntity partner = partnerRepository.getPartnerById(orderRequest.getPartnerId());
            orderEntity.setPartner(partner);
        }

        OrderEntity updated = orderRepository.updateOrder(orderEntity);
        return orderMapper.mapOrderEntityToOrderResponse(updated);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        orderRepository.deleteOrder(id);
    }
}