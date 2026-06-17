package com.example.orderservice.repository.jpaRepository;

import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.exception.PartnerNotFoundException;
import com.example.orderservice.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class PartnerRepositoryJpa implements PartnerRepository {

    private final PartnerJpaRepository partnerJpaRepository;

    @Override
    public PartnerEntity getPartnerById(UUID id) {
        return partnerJpaRepository.findById(id)
                .orElseThrow(() -> new PartnerNotFoundException(id));
    }

    @Override
    public List<PartnerEntity> getAllPartners() {
        return partnerJpaRepository.findAll();
    }

    @Override
    public PartnerEntity createPartner(PartnerEntity partnerEntity) {
        return partnerJpaRepository.save(partnerEntity);
    }

    @Override
    public PartnerEntity updatePartner(PartnerEntity partnerEntity) {
        if (!partnerJpaRepository.existsById(partnerEntity.getId())) {
            throw new PartnerNotFoundException(partnerEntity.getId());
        }
        return partnerJpaRepository.save(partnerEntity);
    }

    @Override
    public void deletePartner(UUID id) {
        if (!partnerJpaRepository.existsById(id)) {
            throw new PartnerNotFoundException(id);
        }
        partnerJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return partnerJpaRepository.existsById(id);
    }

    public PartnerEntity getPartnerByIdWithOrders(UUID id) {
        PartnerEntity partner = partnerJpaRepository.findByIdWithOrders(id);
        if (partner == null) {
            throw new PartnerNotFoundException(id);
        }
        return partner;
    }
}