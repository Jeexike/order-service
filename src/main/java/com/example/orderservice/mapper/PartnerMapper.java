package com.example.orderservice.mapper;

import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.PartnerEntity;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PartnerMapper {

    public PartnerEntity toPartnerEntity(PartnerRequest request) {
        PartnerEntity entity = new PartnerEntity();
        entity.setName(request.getName());
        String email = request.getEmail();
        entity.setEmail(email == null || email.isBlank() ? null : email.trim());
        return entity;
    }

    public PartnerEntity toPartnerEntity(UUID id, PartnerRequest request) {
        PartnerEntity entity = new PartnerEntity();
        entity.setId(id);
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        return entity;
    }

    public PartnerResponse toPartnerResponse(PartnerEntity entity) {
        PartnerResponse response = new PartnerResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public List<PartnerResponse> toPartnerResponse(List<PartnerEntity> entities) {
        return entities.stream().map(this::toPartnerResponse).collect(Collectors.toList());
    }
}
