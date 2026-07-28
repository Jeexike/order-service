package com.example.orderservice.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueResponse(
        @JsonProperty("title") String title,
        @JsonProperty("user") User user,
        @JsonProperty("updated_at") OffsetDateTime updatedAt,
        @JsonProperty("body") String text) {

    public IssueResponse {
        if (text != null && text.length() > 200) {
            text = text.substring(0, 200);
        }
    }

    public record User(@JsonProperty("login") String login) {}
}
