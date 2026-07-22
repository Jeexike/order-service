package com.example.orderservice.database;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerService.configureProperties(registry);
    }

    @BeforeEach
    void cleanDatabase() {
        TestDatabaseContainerService.cleanDatabase();
    }
}
