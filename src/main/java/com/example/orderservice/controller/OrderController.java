package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.validator.OrderValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Операции с заказами")
public class OrderController {

    private final OrderService orderService;
    private final OrderValidator  orderValidator;

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public OrderResponse getOrder(
            @Parameter(description = "ID заказа") @PathVariable UUID id) {
        orderValidator.validateId(id);
        return orderService.getOrderById(id);
    }

    @GetMapping
    @Operation(summary = "Получить список всех заказов")
    public List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        orderValidator.validateOrderRequest(orderRequest);
        return orderService.createOrder(orderRequest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующий заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ обновлён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    public OrderResponse updateOrder(
            @Parameter(description = "ID заказа") @PathVariable UUID id,
            @Valid @RequestBody OrderRequest orderRequest) {
        orderValidator.validateId(id);
        orderValidator.validateOrderRequest(orderRequest);
        return orderService.updateOrder(id, orderRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ удалён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public void deleteOrder(
            @Parameter(description = "ID заказа") @PathVariable UUID id) {
        orderValidator.validateId(id);
        orderService.deleteOrder(id);
    }
}
