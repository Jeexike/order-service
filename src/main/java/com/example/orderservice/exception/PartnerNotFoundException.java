package com.example.orderservice.exception;

import java.util.UUID;

public class PartnerNotFoundException extends RuntimeException {
    public PartnerNotFoundException(UUID id) {
        super("Partner with id=" + id + " not found");
    }
}
