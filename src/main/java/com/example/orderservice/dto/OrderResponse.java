package com.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
@Scope("prototype")
@Data
@JsonPropertyOrder({"id", "name", "source", "destination", "createdAt", "updatedAt"})
public class OrderResponse {
    private UUID id;
    private String name;
    private String source;
    private String destination;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
