package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Source cannot be blank")
    @Size(min = 2, max = 255, message = "Source must be between 2 and 255 characters")
    private String source;

    @NotBlank(message = "Destination cannot be blank")
    @Size(min = 2, max = 255, message = "Destination must be between 2 and 255 characters")
    private String destination;
}
