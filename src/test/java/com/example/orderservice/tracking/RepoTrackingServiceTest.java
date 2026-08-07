package com.example.orderservice.tracking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.RepoSnapshotEntity;
import com.example.orderservice.entity.TrackingOutboxEntity;
import com.example.orderservice.github.GitHubClient;
import com.example.orderservice.github.dto.GitHubResponse;
import com.example.orderservice.github.dto.IssueResponse;
import com.example.orderservice.github.dto.PullRequestResponse;
import com.example.orderservice.github.dto.RepoSnapshotData;
import com.example.orderservice.repository.RepoSnapshotRepository;
import com.example.orderservice.repository.TrackingOutboxRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class RepoTrackingServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private RepoSnapshotRepository repoSnapshotRepository;

    @Mock
    private TrackingOutboxRepository trackingOutboxRepository;

    private RepoTrackingService repoTrackingService;
    private RepoSnapshotMapper repoSnapshotMapper;
    private JsonMapper objectMapper;

    private OrderEntity order;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().build();
        repoSnapshotMapper = new RepoSnapshotMapper(objectMapper);
        repoTrackingService = new RepoTrackingService(
                gitHubClient, repoSnapshotRepository, trackingOutboxRepository, repoSnapshotMapper, objectMapper);

        order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .link("https://github.com/example/repo")
                .build();
    }

    private RepoSnapshotData sampleData(OffsetDateTime repoUpdatedAt, String issueTitle, String prTitle) {
        GitHubResponse repository = new GitHubResponse("repo", repoUpdatedAt);
        List<IssueResponse> issues =
                List.of(new IssueResponse(issueTitle, new IssueResponse.User("alice"), repoUpdatedAt, "body"));
        List<PullRequestResponse> pullRequests =
                List.of(new PullRequestResponse(prTitle, new PullRequestResponse.User("bob"), repoUpdatedAt, "body"));
        return new RepoSnapshotData(repository, issues, pullRequests);
    }

    private RepoSnapshotEntity matchingSnapshotFor(RepoSnapshotData data) {
        RepoSnapshotEntity entity =
                repoSnapshotMapper.toEntity(order, data, Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));
        entity.setId(UUID.randomUUID());
        return entity;
    }

    @Test
    void trackOrder_noExistingSnapshot_savesInitialSnapshotWithHashes() {
        RepoSnapshotData fresh = sampleData(OffsetDateTime.now(), "Bug 1", "Fix 1");
        when(gitHubClient.fetch(order.getLink())).thenReturn(fresh);
        when(repoSnapshotRepository.findByOrderId(order.getId())).thenReturn(Optional.empty());

        repoTrackingService.trackOrder(order);

        ArgumentCaptor<RepoSnapshotEntity> captor = ArgumentCaptor.forClass(RepoSnapshotEntity.class);
        verify(repoSnapshotRepository).save(captor.capture());

        RepoSnapshotEntity saved = captor.getValue();
        assertThat(saved.getIssuesHash()).isNotBlank();
        assertThat(saved.getPullRequestsHash()).isNotBlank();
        assertThat(saved.getIssuesHash()).isEqualTo(repoSnapshotMapper.hash(fresh.issues()));
        assertThat(saved.getPullRequestsHash()).isEqualTo(repoSnapshotMapper.hash(fresh.pullRequests()));

        verify(trackingOutboxRepository, never()).save(any());
    }

    @Test
    void trackOrder_nothingChanged_onlyUpdatesLastCheckedAt() {
        OffsetDateTime repoUpdatedAt = OffsetDateTime.now();
        RepoSnapshotData fresh = sampleData(repoUpdatedAt, "Bug 1", "Fix 1");
        RepoSnapshotEntity existing = matchingSnapshotFor(fresh);

        when(gitHubClient.fetch(order.getLink())).thenReturn(fresh);
        when(repoSnapshotRepository.findByOrderId(order.getId())).thenReturn(Optional.of(existing));

        repoTrackingService.trackOrder(order);

        ArgumentCaptor<RepoSnapshotEntity> captor = ArgumentCaptor.forClass(RepoSnapshotEntity.class);
        verify(repoSnapshotRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
        assertThat(captor.getValue().getIssuesHash()).isEqualTo(existing.getIssuesHash());
        verify(trackingOutboxRepository, never()).save(any());
    }

    @Test
    void trackOrder_issuesChanged_savesUpdatedSnapshotAndPublishesEvent() {
        OffsetDateTime repoUpdatedAt = OffsetDateTime.now();
        RepoSnapshotData previous = sampleData(repoUpdatedAt, "Bug 1", "Fix 1");
        RepoSnapshotEntity existing = matchingSnapshotFor(previous);

        RepoSnapshotData fresh = sampleData(repoUpdatedAt, "Bug 1 updated", "Fix 1");
        when(gitHubClient.fetch(order.getLink())).thenReturn(fresh);
        when(repoSnapshotRepository.findByOrderId(order.getId())).thenReturn(Optional.of(existing));

        repoTrackingService.trackOrder(order);

        verify(repoSnapshotRepository).save(any(RepoSnapshotEntity.class));

        ArgumentCaptor<TrackingOutboxEntity> outboxCaptor = ArgumentCaptor.forClass(TrackingOutboxEntity.class);
        verify(trackingOutboxRepository).save(outboxCaptor.capture());

        assertThat(outboxCaptor.getValue().getOrderId()).isEqualTo(order.getId());
        assertThat(outboxCaptor.getValue().getPayload()).contains("\"issues\"");
    }

    @Test
    void trackOrder_repositoryUpdatedAtChanged_isReportedAsChangedField() {
        OffsetDateTime repoUpdatedAt = OffsetDateTime.now();
        RepoSnapshotData previous = sampleData(repoUpdatedAt, "Bug 1", "Fix 1");
        RepoSnapshotEntity existing = matchingSnapshotFor(previous);

        RepoSnapshotData fresh = sampleData(repoUpdatedAt.plusDays(1), "Bug 1", "Fix 1");
        when(gitHubClient.fetch(order.getLink())).thenReturn(fresh);
        when(repoSnapshotRepository.findByOrderId(order.getId())).thenReturn(Optional.of(existing));

        repoTrackingService.trackOrder(order);

        ArgumentCaptor<TrackingOutboxEntity> outboxCaptor = ArgumentCaptor.forClass(TrackingOutboxEntity.class);
        verify(trackingOutboxRepository).save(outboxCaptor.capture());

        assertThat(outboxCaptor.getValue().getPayload()).contains("repository.updatedAt");
    }

    @Test
    void trackOrder_gitHubFetchFails_doesNothing() {
        when(gitHubClient.fetch(order.getLink())).thenThrow(new RuntimeException("boom"));

        repoTrackingService.trackOrder(order);

        verify(repoSnapshotRepository, never()).save(any());
        verify(trackingOutboxRepository, never()).save(any());
    }
}
