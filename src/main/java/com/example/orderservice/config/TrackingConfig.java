package com.example.orderservice.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrackingConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService repoTrackingExecutor(@Value("${tracking.thread-pool-size:5}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
