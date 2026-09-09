package com.example.orderservice.service;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.mapper.PartnerMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.PartnerRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final OrderRepository orderRepository;
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
    @Transactional(readOnly = true)
    public List<PartnerResponse> getAllPartners() {
        return partnerMapper.toPartnerResponse(partnerRepository.getAllPartners());
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerResponse getPartnerById(UUID partnerId) {
        PartnerEntity partner = partnerRepository.getPartnerById(partnerId);
        return partnerMapper.toPartnerResponse(partner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByPartnerId(UUID partnerId) {
        partnerRepository.getPartnerById(partnerId);
        return orderMapper.toOrderResponse(orderRepository.getOrdersByPartnerId(partnerId));
    }

    @Override
    @Transactional
    public void deletePartner(UUID partnerId) {
        partnerRepository.deletePartner(partnerId);
    }
}
