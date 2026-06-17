package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Операции с партнерами")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать партнера")
    public PartnerResponse createPartner(@Valid @RequestBody PartnerRequest request) {
        return partnerService.createPartner(request);
    }

    @GetMapping("/{partnerId}/orders")
    @Operation(summary = "Получить все заказы партнера")
    public List<OrderResponse> getOrdersByPartnerId(@PathVariable UUID partnerId) {
        return partnerService.getOrdersByPartnerId(partnerId);
    }

    @DeleteMapping("/{partnerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить партнера (каскадно удалятся его заказы)")
    public void deletePartner(@PathVariable UUID partnerId) {
        partnerService.deletePartner(partnerId);
    }
}