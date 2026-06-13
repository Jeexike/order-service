package com.example.orderservice.validator;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderValidator {

    public void validateId(UUID id) {
        if (id == null) {
            throw new ValidationException("Order id cannot be null");
        }
    }

    public void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            throw new ValidationException("Order request cannot be null");
        }
        if (request.getSource() != null
                && request.getDestination() != null
                && request.getSource().equalsIgnoreCase(request.getDestination())) {
            throw new ValidationException("Source and destination cannot be the same");
        }
    }
}