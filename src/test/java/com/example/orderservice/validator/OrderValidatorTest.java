package com.example.orderservice.validator;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderValidatorTest {

    private OrderValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OrderValidator();
    }

    @Test
    @DisplayName("Валидный запрос: source и destination различаются - исключения нет")
    void validateOrderRequest_ShouldNotThrow_WhenSourceAndDestinationDiffer() {

        OrderRequest request = new OrderRequest();
        request.setName("Order");
        request.setSource("Moscow");
        request.setDestination("SPB");

        assertDoesNotThrow(() -> validator.validateOrderRequest(request));
    }

    @Test
    @DisplayName("Невалидный запрос: source и destination совпадают - должно бросить ValidationException")
    void validateOrderRequest_ShouldThrow_WhenSourceEqualsDestination() {

        OrderRequest request = new OrderRequest();
        request.setName("Order");
        request.setSource("Moscow");
        request.setDestination("Moscow");

        assertThrows(ValidationException.class, () -> validator.validateOrderRequest(request));
    }

    @Test
    @DisplayName("Невалидный запрос: source и destination совпадают без учета регистра - должно бросить ValidationException")
    void validateOrderRequest_ShouldThrow_WhenSourceEqualsDestinationIgnoringCase() {

        OrderRequest request = new OrderRequest();
        request.setName("Order");
        request.setSource("moscow");
        request.setDestination("MOSCOW");

        assertThrows(ValidationException.class, () -> validator.validateOrderRequest(request));
    }
}