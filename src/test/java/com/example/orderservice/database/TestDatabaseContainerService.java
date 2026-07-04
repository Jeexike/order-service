package com.example.orderservice.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.DriverManager;

@Testcontainers
public class TestDatabaseContainerService {

    public static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("orders_test")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withReuse(true);

    static {
        POSTGRES.start();
        runLiquibase();
    }

    private static void runLiquibase() {
        try (var connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword())) {

            var database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(
                            new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    database);

            liquibase.update(new Contexts(), new LabelExpression());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    private static JdbcTemplate jdbcTemplate;

    private static void initJdbc() {

        if (jdbcTemplate != null)
            return;

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(POSTGRES.getJdbcUrl());
        config.setUsername(POSTGRES.getUsername());
        config.setPassword(POSTGRES.getPassword());

        DataSource ds = new HikariDataSource(config);

        jdbcTemplate = new JdbcTemplate(ds);
    }

    public static void cleanDatabase() {

        initJdbc();

        jdbcTemplate.execute("""
            TRUNCATE TABLE orders, partners
            RESTART IDENTITY
            CASCADE
            """);
    }
}