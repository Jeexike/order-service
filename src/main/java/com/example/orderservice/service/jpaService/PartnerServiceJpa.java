package com.example.orderservice.service.jpaService;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.mapper.PartnerMapper;
import com.example.orderservice.repository.PartnerRepository;
import com.example.orderservice.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class PartnerServiceJpa implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public PartnerResponse createPartner(PartnerRequest request) {
        PartnerEntity entity = partnerMapper.toPartnerEntity(request);
        PartnerEntity saved = partnerRepository.createPartner(entity);
        return partnerMapper.toPartnerResponse(saved);
    }

    @Override
    @Transactional
    public PartnerResponse getPartnerById(UUID partnerId) {
        PartnerEntity partner = partnerRepository.getPartnerById(partnerId);
        return partnerMapper.toPartnerResponse(partner);
    }

    @Override
    @Transactional
    public List<OrderResponse> getOrdersByPartnerId(UUID partnerId) {
        PartnerEntity partner = partnerRepository.getPartnerById(partnerId);
        return partner.getOrders().stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePartner(UUID partnerId) {
        partnerRepository.getPartnerById(partnerId);
        partnerRepository.deletePartner(partnerId);
    }
}