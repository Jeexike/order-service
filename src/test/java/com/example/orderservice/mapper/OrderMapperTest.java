package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.PartnerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderMapper();
    }

    @Test
    void shouldMapOrderRequestToOrderEntity() {
        OrderRequest request = new OrderRequest();
        request.setName("Order 1");
        request.setSource("Moscow");
        request.setDestination("Saint Petersburg");

        OrderEntity entity = mapper.mapOrderRequestToOrderEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getSource(), entity.getSource());
        assertEquals(request.getDestination(), entity.getDestination());
    }

    @Test
    void shouldMapOrderRequestToOrderEntityWithId() {
        UUID id = UUID.randomUUID();

        OrderRequest request = new OrderRequest();
        request.setName("Order 1");
        request.setSource("Moscow");
        request.setDestination("Saint Petersburg");

        OrderEntity entity = mapper.mapOrderRequestToOrderEntity(id, request);

        assertEquals(id, entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getSource(), entity.getSource());
        assertEquals(request.getDestination(), entity.getDestination());
    }

    @Test
    void shouldMapOrderEntityToOrderResponse() {
        UUID id = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();

        PartnerEntity partner = new PartnerEntity();
        partner.setId(partnerId);

        Timestamp createTime = Timestamp.valueOf("2025-01-01 12:00:00");
        Timestamp updateTime = Timestamp.valueOf("2025-01-01 12:30:00");

        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        entity.setName("Order");
        entity.setSource("A");
        entity.setDestination("B");
        entity.setCreatedAt(createTime);
        entity.setUpdatedAt(updateTime);
        entity.setPartner(partner);

        OrderResponse response = mapper.mapOrderEntityToOrderResponse(entity);

        assertEquals(id, response.getId());
        assertEquals("Order", response.getName());
        assertEquals("A", response.getSource());
        assertEquals("B", response.getDestination());
        assertEquals(createTime, response.getCreatedAt());
        assertEquals(updateTime, response.getUpdatedAt());
        assertEquals(partnerId, response.getPartnerId());
    }

    @Test
    void shouldMapOrderEntityWithoutPartner() {
        OrderEntity entity = new OrderEntity();
        entity.setName("Order");

        OrderResponse response = mapper.mapOrderEntityToOrderResponse(entity);

        assertNull(response.getPartnerId());
    }

    @Test
    void shouldMapOrderEntityListToOrderResponseList() {
        OrderEntity entity1 = new OrderEntity();
        entity1.setName("First");

        OrderEntity entity2 = new OrderEntity();
        entity2.setName("Second");

        List<OrderResponse> responses =
                mapper.mapOrderEntityToOrderResponse(List.of(entity1, entity2));

        assertEquals(2, responses.size());
        assertEquals("First", responses.get(0).getName());
        assertEquals("Second", responses.get(1).getName());
    }
}