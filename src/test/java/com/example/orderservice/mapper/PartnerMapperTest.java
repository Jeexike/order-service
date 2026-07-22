package com.example.orderservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.testdata.TestDataFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartnerMapperTest {

    private PartnerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PartnerMapper();
    }

    @Test
    void shouldMapPartnerRequestToPartnerEntity() {
        PartnerRequest request = TestDataFactory.createPartnerRequest();

        PartnerEntity entity = mapper.toPartnerEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getEmail(), entity.getEmail());
    }

    @Test
    void shouldMapPartnerRequestToPartnerEntityWithId() {
        UUID id = UUID.randomUUID();
        PartnerRequest request = TestDataFactory.createPartnerRequest();

        PartnerEntity entity = mapper.toPartnerEntity(id, request);

        assertEquals(id, entity.getId());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getEmail(), entity.getEmail());
    }

    @Test
    void shouldMapPartnerEntityToPartnerResponse() {
        PartnerEntity entity = TestDataFactory.createPartnerEntity();

        PartnerResponse response = mapper.toPartnerResponse(entity);

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getEmail(), response.getEmail());
        assertEquals(entity.getCreatedAt(), response.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), response.getUpdatedAt());
    }

    @Test
    void shouldMapPartnerEntityListToPartnerResponseList() {
        PartnerEntity first = TestDataFactory.createPartnerEntity();
        first.setName("First");

        PartnerEntity second = TestDataFactory.createPartnerEntity();
        second.setName("Second");

        List<PartnerResponse> responses = mapper.toPartnerResponse(List.of(first, second));

        assertEquals(2, responses.size());
        assertEquals("First", responses.get(0).getName());
        assertEquals("Second", responses.get(1).getName());
    }
}
