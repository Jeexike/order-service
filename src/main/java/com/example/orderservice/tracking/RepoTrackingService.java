package com.example.orderservice.tracking;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.RepoSnapshotEntity;
import com.example.orderservice.entity.TrackingOutboxEntity;
import com.example.orderservice.github.GitHubClient;
import com.example.orderservice.github.dto.RepoSnapshotData;
import com.example.orderservice.repository.RepoSnapshotRepository;
import com.example.orderservice.repository.TrackingOutboxRepository;
import com.example.orderservice.tracking.dto.OrderLinkChangedEvent;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepoTrackingService {

    private final GitHubClient gitHubClient;
    private final RepoSnapshotRepository repoSnapshotRepository;
    private final TrackingOutboxRepository trackingOutboxRepository;
    private final RepoSnapshotMapper repoSnapshotMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public void trackOrder(OrderEntity order) {
        RepoSnapshotData fresh;
        try {
            fresh = gitHubClient.fetch(order.getLink());
        } catch (Exception e) {
            log.error("Failed to fetch GitHub data for order {} (link={})", order.getId(), order.getLink(), e);
            return;
        }

        RepoSnapshotEntity existing =
                repoSnapshotRepository.findByOrderId(order.getId()).orElse(null);

        Timestamp now = Timestamp.from(Instant.now());

        if (existing == null) {
            repoSnapshotRepository.save(repoSnapshotMapper.toEntity(order, fresh, now, now));
            log.info("Initial GitHub snapshot stored for order {} (link={})", order.getId(), order.getLink());

            return;
        }

        List<String> changedFields = diff(existing, fresh);

        if (changedFields.isEmpty()) {
            existing.setLastCheckedAt(now);
            repoSnapshotRepository.save(existing);
            return;
        }

        RepoSnapshotEntity updated = repoSnapshotMapper.toEntity(order, fresh, existing.getCreatedAt(), now);
        updated.setId(existing.getId());
        repoSnapshotRepository.save(updated);

        OrderLinkChangedEvent event =
                new OrderLinkChangedEvent(order.getId(), order.getLink(), changedFields, OffsetDateTime.now());

        trackingOutboxRepository.save(TrackingOutboxEntity.builder()
                .orderId(order.getId())
                .payload(writeJson(event))
                .createdAt(now)
                .build());

        log.info("Detected changes for order {} (link={}): {}", order.getId(), order.getLink(), changedFields);
    }

    private List<String> diff(RepoSnapshotEntity existing, RepoSnapshotData fresh) {
        List<String> changed = new ArrayList<>();

        if (!isSameInstant(existing.getRepositoryUpdatedAt(), fresh.repository().updatedAt())) {
            changed.add("repository.updatedAt");
        }
        if (!Objects.equals(existing.getRepositoryName(), fresh.repository().repositoryName())) {
            changed.add("repository.name");
        }
        if (!Objects.equals(existing.getIssuesHash(), repoSnapshotMapper.hash(fresh.issues()))) {
            changed.add("issues");
        }
        if (!Objects.equals(existing.getPullRequestsHash(), repoSnapshotMapper.hash(fresh.pullRequests()))) {
            changed.add("pullRequests");
        }
        return changed;
    }

    private boolean isSameInstant(OffsetDateTime a, OffsetDateTime b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.truncatedTo(ChronoUnit.SECONDS).isEqual(b.truncatedTo(ChronoUnit.SECONDS));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize snapshot data", e);
        }
    }
}
