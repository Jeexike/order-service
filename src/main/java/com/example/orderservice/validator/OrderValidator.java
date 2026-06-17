package com.example.orderservice.validator;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    public void validateOrderRequest(OrderRequest request) {
        if (request.getSource().equalsIgnoreCase(request.getDestination())) {
            throw new ValidationException("Source and destination cannot be the same");
        }
    }
}