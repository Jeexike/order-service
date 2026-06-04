package com.example.orderservice.repository;

import lombok.Getter;

@Getter
public enum OrderQueries {
    GET_ORDER_BY_ID("SELECT * FROM orders WHERE id = ?"),
    GET_ALL_ORDERS("SELECT * FROM orders"),
    CREATE_ORDER("""
            INSERT INTO orders (name, source, destination, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """),
    UPDATE_ORDER("""
            UPDATE orders
            SET name = ?, source = ?, destination = ?, updated_at = ?
            WHERE id = ?
            """),
    DELETE_ORDER("DELETE FROM orders WHERE id = ?");

    private final String query;

    OrderQueries(String query) {
        this.query = query;
    }

}
