package com.example.orderservice.tracking;

import com.example.orderservice.entity.TrackingOutboxEntity;
import com.example.orderservice.repository.TrackingOutboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackingOutboxDispatcherScheduler {

    private final TrackingOutboxRepository trackingOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${tracking.outbox.batch-size:100}")
    private int batchSize;

    @Value("${tracking.outbox.topic}")
    private String outboxTopic;

    @Scheduled(fixedRateString = "${tracking.outbox.dispatch-rate-ms:10000}")
    public void scheduleOutboxDispatcher() {
        Pageable firstPage = PageRequest.of(0, batchSize);
        List<TrackingOutboxEntity> outboxes = trackingOutboxRepository.findAllByOrderByCreatedAtAsc(firstPage);

        outboxes.forEach(outbox -> {
            try {
                kafkaTemplate
                        .send(outboxTopic, outbox.getOrderId().toString(), outbox.getPayload())
                        .get();
                trackingOutboxRepository.deleteById(outbox.getId());
            } catch (Exception e) {
                log.error("Error while sending outbox message {}", outbox.getId(), e);
            }
        });
    }
}
