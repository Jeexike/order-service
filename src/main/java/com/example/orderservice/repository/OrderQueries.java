package com.example.orderservice.repository;

public interface OrderQueries {

    String GET_ORDER_BY_ID = "SELECT * FROM orders WHERE id = ?";

    String GET_ALL_ORDERS = "SELECT * FROM orders";

    String GET_ORDERS_BY_PARTNER_ID = """
            SELECT id, name, source, destination, link, partner_id, created_at, updated_at
            FROM orders
            WHERE partner_id = ?
            ORDER BY created_at DESC
            """;

    String CREATE_ORDER = """
        INSERT INTO orders (id, name, source, destination, link, partner_id)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING id, name, source, destination, link, partner_id, created_at, updated_at
        """;

    String UPDATE_ORDER = """
        UPDATE orders
        SET name = ?,
            source = ?,
            destination = ?,
            link = ?,
            partner_id = ?,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = ?
        RETURNING id, name, source, destination, link, partner_id, created_at, updated_at
        """;

    String DELETE_ORDER = "DELETE FROM orders WHERE id = ?";
}
