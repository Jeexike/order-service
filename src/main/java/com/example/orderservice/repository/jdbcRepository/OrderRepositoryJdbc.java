package com.example.orderservice.repository.jdbcRepository;

import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderQueries;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class OrderRepositoryJdbc implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OrderEntity> rowMapper = (rs, rowNum) -> {
        OrderEntity order = new OrderEntity();
        order.setId(rs.getObject("id", UUID.class));
        order.setName(rs.getString("name"));
        order.setSource(rs.getString("source"));
        order.setDestination(rs.getString("destination"));
        order.setCreatedAt(rs.getObject("created_at", Timestamp.class));
        order.setUpdatedAt(rs.getObject("updated_at", Timestamp.class));

        UUID partnerId = rs.getObject("partner_id", UUID.class);
        if (partnerId != null) {
            PartnerEntity partner = new PartnerEntity();
            partner.setId(partnerId);
            order.setPartner(partner);
        }

        return order;
    };

    @Override
    public OrderEntity getOrderById(UUID id) {
        try {
            return jdbcTemplate.queryForObject(
                    OrderQueries.GET_ORDER_BY_ID,
                    rowMapper,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new OrderNotFoundException(id);
        }
    }

    @Override
    public List<OrderEntity> getOrders() {
        return jdbcTemplate.query(OrderQueries.GET_ALL_ORDERS, rowMapper);
    }

    @Override
    public List<OrderEntity> getOrdersByPartnerId(UUID partnerId) {
        return jdbcTemplate.query(OrderQueries.GET_ORDERS_BY_PARTNER_ID, rowMapper, partnerId);
    }

    @Override
    public OrderEntity createOrder(OrderEntity newOrder) {
        UUID partnerId = newOrder.getPartner() != null ? newOrder.getPartner().getId() : null;

        UUID id = (newOrder.getId() != null) ? newOrder.getId() : UUID.randomUUID();

        return jdbcTemplate.queryForObject(
                OrderQueries.CREATE_ORDER,
                rowMapper,
                id,
                newOrder.getName(),
                newOrder.getSource(),
                newOrder.getDestination(),
                partnerId
        );
    }

    @Override
    public OrderEntity updateOrder(OrderEntity updatedOrder) {
        UUID partnerId = updatedOrder.getPartner() != null ? updatedOrder.getPartner().getId() : null;

        return jdbcTemplate.queryForObject(
                OrderQueries.UPDATE_ORDER,
                rowMapper,
                updatedOrder.getName(),
                updatedOrder.getSource(),
                updatedOrder.getDestination(),
                partnerId,
                updatedOrder.getId()
        );
    }

    @Override
    public void deleteOrder(UUID id) {
        int affectedRows = jdbcTemplate.update(OrderQueries.DELETE_ORDER, id);
        if (affectedRows == 0) {
            throw new OrderNotFoundException(id);
        }
    }
}