package com.example.orderservice.tracking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderLinkChangedEvent(
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("link") String link,
        @JsonProperty("changedFields") List<String> changedFields,
        @JsonProperty("detectedAt") OffsetDateTime detectedAt) {}
