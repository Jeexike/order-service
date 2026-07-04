package com.example.orderservice.mapper;

import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.PartnerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PartnerMapperTest {

    private PartnerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PartnerMapper();
    }

    @Test
    void shouldMapPartnerRequestToPartnerEntity() {
        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        PartnerEntity entity = mapper.mapPartnerRequestToPartnerEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getEmail(), entity.getEmail());
    }

    @Test
    void shouldMapPartnerRequestToPartnerEntityWithId() {
        UUID id = UUID.randomUUID();

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        PartnerEntity entity = mapper.mapPartnerRequestToPartnerEntity(id, request);

        assertEquals(id, entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getEmail(), entity.getEmail());
    }

    @Test
    void shouldMapPartnerEntityToPartnerResponse() {
        UUID id = UUID.randomUUID();
        Timestamp createTime = Timestamp.valueOf("2025-01-01 12:00:00");
        Timestamp updateTime = Timestamp.valueOf("2025-01-01 12:30:00");

        PartnerEntity entity = new PartnerEntity();
        entity.setId(id);
        entity.setName("Partner");
        entity.setEmail("partner@test.com");
        entity.setCreatedAt(createTime);
        entity.setUpdatedAt(updateTime);

        PartnerResponse response = mapper.mapPartnerEntityToPartnerResponse(entity);

        assertEquals(id, response.getId());
        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getEmail(), response.getEmail());
        assertEquals(createTime, response.getCreatedAt());
        assertEquals(updateTime, response.getUpdatedAt());
    }

    @Test
    void shouldMapPartnerEntityListToPartnerResponseList() {
        PartnerEntity first = new PartnerEntity();
        first.setName("First");

        PartnerEntity second = new PartnerEntity();
        second.setName("Second");

        List<PartnerResponse> responses =
                mapper.mapPartnerEntityToPartnerResponse(List.of(first, second));

        assertEquals(2, responses.size());
        assertEquals("First", responses.get(0).getName());
        assertEquals("Second", responses.get(1).getName());
    }
}