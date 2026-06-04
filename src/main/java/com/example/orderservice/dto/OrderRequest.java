package com.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String name;
    private String source;
    private String destination;
}
