package com.example.orderservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.testdata.TestDataFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void shouldMapOrderRequestToOrderEntity() {
        OrderRequest request = TestDataFactory.createOrderRequest();

        OrderEntity entity = mapper.toOrderEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getSource(), entity.getSource());
        assertEquals(request.getDestination(), entity.getDestination());
        assertEquals(request.getLink(), entity.getLink());
    }

    @Test
    void shouldMapOrderRequestToOrderEntityWithId() {
        UUID id = UUID.randomUUID();
        OrderRequest request = TestDataFactory.createOrderRequest();

        OrderEntity entity = mapper.toOrderEntity(id, request);

        assertEquals(id, entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getSource(), entity.getSource());
        assertEquals(request.getDestination(), entity.getDestination());
        assertEquals(request.getLink(), entity.getLink());
    }

    @Test
    void shouldMapOrderEntityToOrderResponse() {
        PartnerEntity partner = TestDataFactory.createPartnerEntity();
        OrderEntity entity = TestDataFactory.createOrderEntity(UUID.randomUUID(), partner);

        OrderResponse response = mapper.toOrderResponse(entity);

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getSource(), response.getSource());
        assertEquals(entity.getDestination(), response.getDestination());
        assertEquals(entity.getLink(), response.getLink());
        assertEquals(entity.getCreatedAt(), response.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), response.getUpdatedAt());
        assertEquals(partner.getId(), response.getPartnerId());
    }

    @Test
    void shouldMapOrderEntityListToOrderResponseList() {
        PartnerEntity partner = TestDataFactory.createPartnerEntity();

        OrderEntity entity1 = TestDataFactory.createOrderEntity(UUID.randomUUID(), partner);
        entity1.setName("First");

        OrderEntity entity2 = TestDataFactory.createOrderEntity(UUID.randomUUID(), partner);
        entity2.setName("Second");

        List<OrderResponse> responses = mapper.toOrderResponse(List.of(entity1, entity2));

        assertEquals(2, responses.size());
        assertEquals("First", responses.get(0).getName());
        assertEquals("Second", responses.get(1).getName());
    }
}
