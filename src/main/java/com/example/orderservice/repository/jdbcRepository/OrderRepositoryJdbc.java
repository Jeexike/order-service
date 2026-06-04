package com.example.orderservice.repository.jdbcRepository;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.repository.OrderQueries;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class OrderRepositoryJdbc implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepositoryJdbc(@Qualifier("jdbcRepository") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OrderEntity> rowMapper = ((rs, rowNum) -> new OrderEntity(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getObject("created_at", Timestamp.class),
            rs.getObject("updated_at", Timestamp.class)
    ));

    @Override
    public OrderEntity getOrderById(UUID id) {
        return jdbcTemplate.queryForObject(OrderQueries.GET_ORDER_BY_ID.getQuery(), rowMapper, id);
    }

    @Override
    public List<OrderEntity> getOrders() {
        return jdbcTemplate.query(OrderQueries.GET_ALL_ORDERS.getQuery(), rowMapper);
    }

    @Override
    public OrderEntity createOrder(OrderEntity newOrder) {
        jdbcTemplate.update(OrderQueries.CREATE_ORDER.getQuery(),
                newOrder.getName(),
                newOrder.getSource(),
                newOrder.getDestination(),
                newOrder.getCreatedAt(),
                newOrder.getUpdatedAt());
        return newOrder;
    }

    @Override
    public OrderEntity updateOrder(OrderEntity updatedOrder) {
        jdbcTemplate.update(OrderQueries.UPDATE_ORDER.getQuery(),
                updatedOrder.getName(),
                updatedOrder.getSource(),
                updatedOrder.getDestination(),
                updatedOrder.getUpdatedAt(),
                updatedOrder.getId());
        return updatedOrder;
    }

    @Override
    public void deleteOrder(UUID id) {
        jdbcTemplate.update(OrderQueries.DELETE_ORDER.getQuery(), id);
    }
}
