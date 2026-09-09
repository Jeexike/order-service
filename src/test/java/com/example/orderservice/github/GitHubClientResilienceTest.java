package com.example.orderservice.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.exception.GitHubUnavailableException;
import com.example.orderservice.exception.InvalidGitHubLinkException;
import com.sun.net.httpserver.HttpServer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ActiveProfiles("resilience")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GitHubClient resilience (CB / RL / Retry)")
class GitHubClientResilienceTest extends AbstractIntegrationTest {

    static HttpServer httpServer;
    static final AtomicInteger REQUEST_COUNT = new AtomicInteger();
    static volatile int responseCode = 200;
    static volatile String responseBody = "{}";

    @Autowired
    GitHubClient gitHubClient;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    RateLimiterRegistry rateLimiterRegistry;

    @DynamicPropertySource
    static void githubProps(DynamicPropertyRegistry registry) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        httpServer.createContext("/", exchange -> {
            REQUEST_COUNT.incrementAndGet();
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        httpServer.start();
        String base = "http://localhost:" + httpServer.getAddress().getPort();
        registry.add("github.api.base-url", () -> base);
        registry.add("github.api.token", () -> "");
    }

    @AfterAll
    static void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @BeforeEach
    void resetState() {
        circuitBreakerRegistry.circuitBreaker("gitHubApi").reset();
        REQUEST_COUNT.set(0);
        responseCode = 200;
        responseBody = "{}";
        ensurePermits();
    }

    private void ensurePermits() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1000)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ZERO)
                .build();
        rateLimiterRegistry.replace("gitHubApi", RateLimiter.of("gitHubApi", config));
    }

    @Test
    @Order(1)
    @DisplayName("невалидный link → InvalidGitHubLinkException, без HTTP")
    void invalidLink_noHttpCall() {
        REQUEST_COUNT.set(0);
        assertThatThrownBy(() -> gitHubClient.fetch("https://example.com/x"))
                .isInstanceOf(InvalidGitHubLinkException.class);
        assertThat(REQUEST_COUNT.get()).isZero();
    }

    @Test
    @Order(2)
    @DisplayName("5xx → failed calls CB растут")
    void serverErrors_recorded() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("gitHubApi");
        int failedBefore = cb.getMetrics().getNumberOfFailedCalls();
        responseCode = 500;
        responseBody = "err";

        for (int i = 0; i < 5; i++) {
            try {
                gitHubClient.fetch("https://github.com/owner/repo");
            } catch (Exception ignored) {
            }
        }

        assertThat(cb.getMetrics().getNumberOfFailedCalls()).isGreaterThan(failedBefore);
    }

    @Test
    @Order(3)
    @DisplayName("CB OPEN → GitHubUnavailableException")
    void whenOpen_fallback() {
        circuitBreakerRegistry.circuitBreaker("gitHubApi").transitionToOpenState();

        assertThatThrownBy(() -> gitHubClient.fetch("https://github.com/owner/repo"))
                .isInstanceOf(GitHubUnavailableException.class);
    }

    @Test
    @Order(4)
    @DisplayName("RateLimiter → RequestNotPermitted")
    void rateLimiter_blocksWhenExhausted() {
        RateLimiterConfig tight = RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofSeconds(60))
                .timeoutDuration(Duration.ZERO)
                .build();
        RateLimiter rl = RateLimiter.of("gitHubApi", tight);
        rateLimiterRegistry.replace("gitHubApi", rl);

        assertThat(rl.acquirePermission()).isTrue();
        assertThatThrownBy(() -> gitHubClient.fetch("https://github.com/owner/repo"))
                .isInstanceOf(RequestNotPermitted.class);
    }
}
