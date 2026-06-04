package com.example.orderservice.config;

import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.mapper.jdbcMapper.OrderMapperJdbc;
import com.example.orderservice.mapper.jpaMapper.OrderMapperJpa;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.jdbcRepository.OrderRepositoryJdbc;
import com.example.orderservice.repository.jpaRepository.OrderJpaRepository;
import com.example.orderservice.repository.jpaRepository.OrderRepositoryJpa;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.jdbcService.OrderServiceJdbc;
import com.example.orderservice.service.jpaService.OrderServiceJpa;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AppConfig {

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
    @Primary
    public OrderRepository jpaRepository(OrderJpaRepository orderJpaRepository) {
        return new OrderRepositoryJpa(orderJpaRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
    public OrderRepository jdbcRepository(JdbcTemplate jdbcTemplate) {
        return new OrderRepositoryJdbc(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
    public OrderService jpaService(OrderRepository orderRepository, OrderMapper orderMapper) {
        return new OrderServiceJpa(orderRepository, orderMapper);
    }


    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
    public OrderService jdbcService(OrderRepository orderRepository,  OrderMapper orderMapper) {
        return new OrderServiceJdbc(orderRepository,  orderMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
    public OrderMapper jpaOrderMapper(ApplicationContext applicationContext) {
        return new OrderMapperJpa(applicationContext);
    }

    @Bean
    @ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
    public OrderMapper jdbcOrderMapper(ApplicationContext applicationContext) {
        return new OrderMapperJdbc(applicationContext);
    }
}
