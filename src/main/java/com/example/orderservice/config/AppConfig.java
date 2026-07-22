package com.example.orderservice.config;

import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.mapper.PartnerMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.PartnerRepository;
import com.example.orderservice.repository.jdbcRepository.OrderRepositoryJdbc;
import com.example.orderservice.repository.jdbcRepository.PartnerRepositoryJdbc;
import com.example.orderservice.repository.jpaRepository.OrderJpaRepository;
import com.example.orderservice.repository.jpaRepository.OrderRepositoryJpa;
import com.example.orderservice.repository.jpaRepository.PartnerJpaRepository;
import com.example.orderservice.repository.jpaRepository.PartnerRepositoryJpa;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AppConfig {

    // MAPPERS

    @Bean
    public OrderMapper orderMapper() {
        return new OrderMapper();
    }

    @Bean
    public PartnerMapper partnerMapper() {
        return new PartnerMapper();
    }

    // ORDER REPOSITORIES

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
    public OrderRepository jpaOrderRepository(OrderJpaRepository orderJpaRepository) {
        return new OrderRepositoryJpa(orderJpaRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
    public OrderRepository jdbcOrderRepository(JdbcTemplate jdbcTemplate) {
        return new OrderRepositoryJdbc(jdbcTemplate);
    }

    // PARTNER REPOSITORIES

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
    public PartnerRepository jpaPartnerRepository(PartnerJpaRepository partnerJpaRepository) {
        return new PartnerRepositoryJpa(partnerJpaRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
    public PartnerRepository jdbcPartnerRepository(JdbcTemplate jdbcTemplate) {
        return new PartnerRepositoryJdbc(jdbcTemplate);
    }
}
