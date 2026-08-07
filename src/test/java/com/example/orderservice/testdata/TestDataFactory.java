package com.example.orderservice.testdata;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.PartnerEntity;
import java.sql.Timestamp;
import java.util.UUID;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static OrderRequest createOrderRequest() {
        return createOrderRequest(UUID.randomUUID());
    }

    public static OrderRequest createOrderRequest(UUID partnerId) {
        OrderRequest request = new OrderRequest();
        request.setName("Order 1");
        request.setSource("Moscow");
        request.setDestination("Saint Petersburg");
        request.setPartnerId(partnerId);
        request.setLink("https://github.com/Jeexike/order-service");
        return request;
    }

    public static PartnerRequest createPartnerRequest() {
        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");
        return request;
    }

    public static PartnerEntity createPartnerEntity() {
        return createPartnerEntity(UUID.randomUUID());
    }

    public static PartnerEntity createPartnerEntity(UUID id) {
        PartnerEntity partner = new PartnerEntity();
        partner.setId(id);
        partner.setName("Partner");
        partner.setEmail("partner@test.com");
        partner.setCreatedAt(Timestamp.valueOf("2025-01-01 12:00:00"));
        partner.setUpdatedAt(Timestamp.valueOf("2025-01-01 12:30:00"));
        return partner;
    }

    public static OrderEntity createOrderEntity() {
        return createOrderEntity(UUID.randomUUID(), createPartnerEntity());
    }

    public static OrderEntity createOrderEntity(UUID id, PartnerEntity partner) {
        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        entity.setName("Order");
        entity.setSource("A");
        entity.setDestination("B");
        entity.setLink("https://github.com/Jeexike/order-service");
        entity.setPartner(partner);
        entity.setCreatedAt(Timestamp.valueOf("2025-01-01 12:00:00"));
        entity.setUpdatedAt(Timestamp.valueOf("2025-01-01 12:30:00"));
        return entity;
    }
}
