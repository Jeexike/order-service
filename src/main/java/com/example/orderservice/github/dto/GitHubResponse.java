package com.example.orderservice.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubResponse(
        @JsonProperty("name") String repositoryName,
        @JsonProperty("updated_at") OffsetDateTime updatedAt) {}
