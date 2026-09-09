package com.example.orderservice.service;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import java.util.List;
import java.util.UUID;

public interface PartnerService {

    PartnerResponse createPartner(PartnerRequest request);

    List<PartnerResponse> getAllPartners();

    PartnerResponse getPartnerById(UUID partnerId);

    List<OrderResponse> getOrdersByPartnerId(UUID partnerId);

    void deletePartner(UUID partnerId);
}
