package com.example.orderservice.mapper;

import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.PartnerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartnerMapper {

    public PartnerEntity mapPartnerRequestToPartnerEntity(PartnerRequest request) {
        PartnerEntity entity = new PartnerEntity();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        return entity;
    }

    public PartnerEntity mapPartnerRequestToPartnerEntity(UUID id, PartnerRequest request) {
        PartnerEntity entity = new PartnerEntity();
        entity.setId(id);
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        return entity;
    }

    public PartnerResponse mapPartnerEntityToPartnerResponse(PartnerEntity entity) {
        PartnerResponse response = new PartnerResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public List<PartnerResponse> mapPartnerEntityToPartnerResponse(List<PartnerEntity> entities) {
        List<PartnerResponse> responses = new ArrayList<>();
        for (PartnerEntity entity : entities) {
            responses.add(mapPartnerEntityToPartnerResponse(entity));
        }
        return responses;
    }
}