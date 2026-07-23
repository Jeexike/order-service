package com.example.orderservice.repository;

import com.example.orderservice.entity.PartnerEntity;
import java.util.List;
import java.util.UUID;

public interface PartnerRepository {

    PartnerEntity getPartnerById(UUID id);

    List<PartnerEntity> getAllPartners();

    PartnerEntity createPartner(PartnerEntity partnerEntity);

    PartnerEntity updatePartner(PartnerEntity partnerEntity);

    void deletePartner(UUID id);

    boolean existsById(UUID id);
}
