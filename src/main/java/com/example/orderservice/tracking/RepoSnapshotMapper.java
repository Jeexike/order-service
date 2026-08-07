package com.example.orderservice.tracking;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.RepoSnapshotEntity;
import com.example.orderservice.github.dto.RepoSnapshotData;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RepoSnapshotMapper {

    private final ObjectMapper objectMapper;

    public RepoSnapshotEntity toEntity(
            OrderEntity order, RepoSnapshotData data, Timestamp createdAt, Timestamp lastCheckedAt) {
        String issuesJson = writeJson(data.issues());
        String pullRequestsJson = writeJson(data.pullRequests());

        return RepoSnapshotEntity.builder()
                .orderId(order.getId())
                .link(order.getLink())
                .repositoryName(data.repository().repositoryName())
                .repositoryUpdatedAt(data.repository().updatedAt())
                .issues(issuesJson)
                .issuesHash(sha256(issuesJson))
                .pullRequests(pullRequestsJson)
                .pullRequestsHash(sha256(pullRequestsJson))
                .createdAt(createdAt)
                .lastCheckedAt(lastCheckedAt)
                .build();
    }

    public String hash(Object value) {
        return sha256(writeJson(value));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize snapshot data", e);
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
