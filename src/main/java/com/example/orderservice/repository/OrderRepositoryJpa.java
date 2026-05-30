package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
public class OrderRepositoryJpa implements  OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(UUID id) {
        return entityManager.find(Order.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return entityManager.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    @Override
    @Transactional(readOnly = false)
    public Order createOrder(Order newOrder) {
        entityManager.persist(newOrder);
        return newOrder;
    }

    @Override
    @Transactional(readOnly = false)
    public Order updateOrder(Order updatedOrder) {
        Order existing = entityManager.find(Order.class, updatedOrder.getId());
        existing.setName(updatedOrder.getName());
        existing.setSource(updatedOrder.getSource());
        existing.setDestination(updatedOrder.getDestination());
        return existing;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteOrder(UUID id) {
        entityManager.remove(getOrderById(id));
    }
}
