package com.example.orderservice.repository;

public interface PartnerQueries {

    String GET_PARTNER_BY_ID = """
            SELECT id, name, email, created_at, updated_at
            FROM partners
            WHERE id = ?
            """;

    String GET_ALL_PARTNERS = """
            SELECT id, name, email, created_at, updated_at
            FROM partners
            ORDER BY created_at DESC
            """;

    String CREATE_PARTNER = """
            INSERT INTO partners (id, name, email)
            VALUES (?, ?, ?)
            RETURNING id, name, email, created_at, updated_at
            """;

    String UPDATE_PARTNER = """
            UPDATE partners
            SET name = ?, email = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            RETURNING id, name, email, created_at, updated_at
            """;

    String DELETE_PARTNER = """
            DELETE FROM partners
            WHERE id = ?
            """;

    String EXISTS_BY_ID = """
            SELECT EXISTS(SELECT 1 FROM partners WHERE id = ?)
            """;

    String GET_ORDERS_BY_PARTNER_ID = """
            SELECT id, name, source, destination, partner_id, created_at, updated_at
            FROM orders
            WHERE partner_id = ?
            ORDER BY created_at DESC
            """;

    String DELETE_ORDERS_BY_PARTNER_ID = "DELETE FROM orders WHERE partner_id = ?";
}