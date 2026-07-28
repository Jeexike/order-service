package com.example.orderservice.tracking;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OutboxEntity;
import com.example.orderservice.entity.RepoSnapshotEntity;
import com.example.orderservice.github.GitHubClient;
import com.example.orderservice.github.dto.IssueResponse;
import com.example.orderservice.github.dto.PullRequestResponse;
import com.example.orderservice.github.dto.RepoSnapshotData;
import com.example.orderservice.repository.OutboxRepository;
import com.example.orderservice.repository.RepoSnapshotRepository;
import com.example.orderservice.tracking.dto.OrderLinkChangedEvent;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepoTrackingService {

    private final GitHubClient gitHubClient;
    private final RepoSnapshotRepository repoSnapshotRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void trackOrder(OrderEntity order) {
        RepoSnapshotData fresh;
        try {
            fresh = gitHubClient.fetch(order.getLink());
        } catch (Exception e) {
            log.warn(
                    "Failed to fresh Github data for order {} (link={}): {}",
                    order.getId(),
                    order.getLink(),
                    e.getMessage());
            return;
        }

        RepoSnapshotEntity existing =
                repoSnapshotRepository.findByOrderId(order.getId()).orElse(null);

        Timestamp now = Timestamp.from(Instant.now());

        if (existing == null) {
            repoSnapshotRepository.save(toEntity(order, fresh, now, now));
            log.info("Initial Github snapshot stored for order {} (link={})", order.getId(), order.getLink());

            return;
        }

        List<String> changedFields = diff(existing, fresh);

        if (changedFields.isEmpty()) {
            existing.setLastCheckedAt(now);
            repoSnapshotRepository.save(existing);
            return;
        }

        RepoSnapshotEntity updated = toEntity(order, fresh, existing.getCreatedAt(), now);
        updated.setId(existing.getId());
        repoSnapshotRepository.save(updated);

        OrderLinkChangedEvent event =
                new OrderLinkChangedEvent(order.getId(), order.getLink(), changedFields, OffsetDateTime.now());

        outboxRepository.save(OutboxEntity.builder()
                .orderId(order.getId())
                .payload(writeJson(event))
                .createdAt(now)
                .build());

        log.info("Detected changes for order {} (link={}): {}", order.getId(), order.getLink(), changedFields);
    }

    private List<String> diff(RepoSnapshotEntity existing, RepoSnapshotData fresh) {
        List<String> changed = new ArrayList<>();

        if (!Objects.equals(
                existing.getRepositoryUpdatedAt(), fresh.repository().updatedAt())) {
            changed.add("repository.updatedAt");
        }
        if (!Objects.equals(existing.getRepositoryName(), fresh.repository().repositoryName())) {
            changed.add("repository.name");
        }
        if (!Objects.equals(readIssues(existing.getIssues()), fresh.issues())) {
            changed.add("issues");
        }
        if (!Objects.equals(readPullRequests(existing.getPullRequests()), fresh.pullRequests())) {
            changed.add("pullRequests");
        }
        return changed;
    }

    private RepoSnapshotEntity toEntity(
            OrderEntity order, RepoSnapshotData data, Timestamp createdAt, Timestamp lastCheckedAt) {
        return RepoSnapshotEntity.builder()
                .orderId(order.getId())
                .link(order.getLink())
                .repositoryName(data.repository().repositoryName())
                .repositoryUpdatedAt(data.repository().updatedAt())
                .issues(writeJson(data.issues()))
                .pullRequests(writeJson(data.pullRequests()))
                .createdAt(createdAt)
                .lastCheckedAt(lastCheckedAt)
                .build();
    }

    private List<IssueResponse> readIssues(String json) {
        return readJson(json, IssueResponse.class);
    }

    private List<PullRequestResponse> readPullRequests(String json) {
        return readJson(json, PullRequestResponse.class);
    }

    private <T> List<T> readJson(String json, Class<T> elementType) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
            return objectMapper.readValue(json, listType);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to deserialize stored snapshot JSON", e);
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize snapshot data", e);
        }
    }
}
