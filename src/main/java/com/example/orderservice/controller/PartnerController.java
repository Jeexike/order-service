package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.service.PartnerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Операции с партнерами")
public class PartnerController implements PartnerApi {

    private final PartnerService partnerService;

    @Override
    public PartnerResponse createPartner(@Valid @RequestBody PartnerRequest request) {
        return partnerService.createPartner(request);
    }

    @Override
    public List<PartnerResponse> getAllPartners() {
        return partnerService.getAllPartners();
    }

    @Override
    public PartnerResponse getPartnerById(@PathVariable UUID partnerId) {
        return partnerService.getPartnerById(partnerId);
    }

    @Override
    public List<OrderResponse> getOrdersByPartnerId(@PathVariable UUID partnerId) {
        return partnerService.getOrdersByPartnerId(partnerId);
    }

    @Override
    public void deletePartner(@PathVariable UUID partnerId) {
        partnerService.deletePartner(partnerId);
    }
}