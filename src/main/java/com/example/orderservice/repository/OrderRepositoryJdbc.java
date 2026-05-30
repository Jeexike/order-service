package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@Data
@AllArgsConstructor
@ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
public class OrderRepositoryJdbc implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Order> rowMapper = ((rs, rowNum) ->  new Order(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getObject("created_at", Timestamp.class),
            rs.getObject("updated_at", Timestamp.class)
    ));


    @Override
    public Order getOrderById(UUID id) {
        return jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?", rowMapper, id);

    }

    @Override
    public List<Order> getOrders() {
        return jdbcTemplate.query("SELECT * FROM orders", rowMapper);
    }

    @Override
    public Order createOrder(Order newOrder) {
        String sql = """
                INSERT INTO orders (name, source, destination, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, newOrder.getName(), newOrder.getSource(), newOrder.getDestination(), newOrder.getCreatedAt(), newOrder.getUpdatedAt());

        return newOrder;
    }

    @Override
    public Order updateOrder(Order updatedOrder) {
        String sql = """
                UPDATE orders
                SET name = ?, source = ?, destination = ?,created_at = ?, updated_at = ?
                WHERE id = ?""";

        jdbcTemplate.update(sql, updatedOrder.getName(),  updatedOrder.getSource(), updatedOrder.getDestination(), updatedOrder.getCreatedAt(), updatedOrder.getUpdatedAt(), updatedOrder.getId());

        return updatedOrder;
    }

    @Override
    public void deleteOrder(UUID id) {
        jdbcTemplate.update("DELETE FROM orders WHERE id = ?", id);
    }
}
