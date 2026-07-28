package com.example.orderservice.tracking;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.repository.OrderRepository;
import java.util.List;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepoTrackingScheduler {

    private final ExecutorService repoTrackingExecutor;
    private final OrderRepository orderRepository;
    private final RepoTrackingService repoTrackingService;

    @Scheduled(fixedRateString = "${tracking.repo-check-rate-ms:3600000}")
    public void scheduleRepoTracking() {
        List<OrderEntity> orders = orderRepository.getOrders();
        orders.forEach((order) -> {
            repoTrackingExecutor.submit(() -> {
                try {
                    repoTrackingService.trackOrder(order);
                } catch (Exception e) {
                    log.warn("Failed to track order {}, reason: {}", order, e.getMessage());
                }
            });
        });
    }
}
