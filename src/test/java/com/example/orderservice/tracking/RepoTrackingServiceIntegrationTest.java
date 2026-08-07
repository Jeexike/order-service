package com.example.orderservice.tracking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.RepoSnapshotEntity;
import com.example.orderservice.entity.TrackingOutboxEntity;
import com.example.orderservice.github.GitHubClient;
import com.example.orderservice.github.dto.GitHubResponse;
import com.example.orderservice.github.dto.IssueResponse;
import com.example.orderservice.github.dto.PullRequestResponse;
import com.example.orderservice.github.dto.RepoSnapshotData;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.RepoSnapshotRepository;
import com.example.orderservice.repository.TrackingOutboxRepository;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PartnerService;
import com.example.orderservice.testdata.TestDataFactory;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(
        properties = {
            "repository.type=jpa",
            "spring.jpa.hibernate.ddl-auto=validate",
            "spring.test.database.replace=none",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
        })
@ActiveProfiles("test")
class RepoTrackingServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RepoTrackingService repoTrackingService;

    @Autowired
    private RepoSnapshotRepository repoSnapshotRepository;

    @Autowired
    private TrackingOutboxRepository trackingOutboxRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PartnerService partnerService;

    @MockitoBean
    private GitHubClient gitHubClient;

    private OrderEntity createTestOrder() {
        PartnerResponse partner = partnerService.createPartner(TestDataFactory.createPartnerRequest());

        OrderRequest orderRequest = TestDataFactory.createOrderRequest(partner.getId());
        OrderResponse order = orderService.createOrder(orderRequest);

        return orderRepository.getOrderById(order.getId());
    }

    private RepoSnapshotData sampleData(OffsetDateTime repoUpdatedAt, String issueTitle) {
        GitHubResponse repository = new GitHubResponse("order-service", repoUpdatedAt);
        List<IssueResponse> issues =
                List.of(new IssueResponse(issueTitle, new IssueResponse.User("alice"), repoUpdatedAt, "body"));
        List<PullRequestResponse> pullRequests =
                List.of(new PullRequestResponse("Fix 1", new PullRequestResponse.User("bob"), repoUpdatedAt, "body"));
        return new RepoSnapshotData(repository, issues, pullRequests);
    }

    @Test
    @DisplayName("Первый трекинг заказа создаёт снапшот с посчитанными хэшами")
    void trackOrder_firstRun_persistsSnapshotWithHashes() {
        OrderEntity order = createTestOrder();
        when(gitHubClient.fetch(order.getLink())).thenReturn(sampleData(OffsetDateTime.now(), "Bug 1"));

        repoTrackingService.trackOrder(order);

        RepoSnapshotEntity saved =
                repoSnapshotRepository.findByOrderId(order.getId()).orElseThrow();

        assertThat(saved.getIssuesHash()).isNotBlank();
        assertThat(saved.getPullRequestsHash()).isNotBlank();
        assertThat(trackingOutboxRepository.findAllByOrderByCreatedAtAsc(Pageable.unpaged()))
                .isEmpty();
    }

    @Test
    @DisplayName("Повторный трекинг без изменений не создаёт событие в outbox")
    void trackOrder_secondRunNoChanges_doesNotPublishEvent() {
        OrderEntity order = createTestOrder();
        RepoSnapshotData data = sampleData(OffsetDateTime.now(), "Bug 1");
        when(gitHubClient.fetch(order.getLink())).thenReturn(data);

        repoTrackingService.trackOrder(order);
        repoTrackingService.trackOrder(order);

        List<TrackingOutboxEntity> outbox = trackingOutboxRepository.findAllByOrderByCreatedAtAsc(Pageable.unpaged());
        assertThat(outbox).isEmpty();
    }

    @Test
    @DisplayName("Изменение issues публикует событие изменения в outbox")
    void trackOrder_issuesChanged_publishesEvent() {
        OrderEntity order = createTestOrder();
        OffsetDateTime repoUpdatedAt = OffsetDateTime.now();

        when(gitHubClient.fetch(order.getLink())).thenReturn(sampleData(repoUpdatedAt, "Bug 1"));
        repoTrackingService.trackOrder(order);

        when(gitHubClient.fetch(order.getLink())).thenReturn(sampleData(repoUpdatedAt, "Bug 1 (renamed)"));
        repoTrackingService.trackOrder(order);

        List<TrackingOutboxEntity> outbox = trackingOutboxRepository.findAllByOrderByCreatedAtAsc(Pageable.unpaged());

        assertThat(outbox).hasSize(1);
        assertThat(outbox.get(0).getPayload()).contains("\"issues\"");
    }
}
