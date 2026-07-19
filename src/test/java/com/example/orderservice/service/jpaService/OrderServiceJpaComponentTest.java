package com.example.orderservice.service.jpaService;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PartnerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "repository.type=jpa",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=true",
        "spring.test.database.replace=none",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
})
@ActiveProfiles("test")
class OrderServiceJpaComponentTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PartnerService partnerService;

    private UUID createPartner() {
        PartnerRequest partnerRequest = new PartnerRequest();
        partnerRequest.setName("Partner");
        partnerRequest.setEmail("partner@test.com");
        PartnerResponse partner = partnerService.createPartner(partnerRequest);
        return partner.getId();
    }

    @Test
    @DisplayName("Создание заказа")
    void createOrder_ShouldSaveOrder() {

        OrderRequest request = new OrderRequest();
        request.setName("Laptop");
        request.setSource("Moscow");
        request.setDestination("SPB");
        request.setPartnerId(createPartner());

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Laptop", response.getName());

        OrderEntity entity =
                orderRepository.getOrderById(response.getId());

        assertEquals("Laptop", entity.getName());
        assertEquals("Moscow", entity.getSource());
        assertEquals("SPB", entity.getDestination());
    }

    @Test
    @DisplayName("Получение заказа по id")
    void getOrderById_ShouldReturnOrder() {

        OrderRequest request = new OrderRequest();
        request.setName("Phone");
        request.setSource("A");
        request.setDestination("B");
        request.setPartnerId(createPartner());

        OrderResponse created =
                orderService.createOrder(request);

        OrderResponse found =
                orderService.getOrderById(created.getId());

        assertNotNull(found);

        assertEquals(created.getId(), found.getId());
        assertEquals("Phone", found.getName());
    }

    @Test
    @DisplayName("Получение всех заказов")
    void getOrders_ShouldReturnOrders() {

        OrderRequest first = new OrderRequest();
        first.setName("Order1");
        first.setSource("A");
        first.setDestination("B");
        first.setPartnerId(createPartner());

        OrderRequest second = new OrderRequest();
        second.setName("Order2");
        second.setSource("C");
        second.setDestination("D");
        second.setPartnerId(createPartner());

        orderService.createOrder(first);
        orderService.createOrder(second);

        List<OrderResponse> orders =
                orderService.getOrders();

        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("Обновление заказа")
    void updateOrder_ShouldUpdateOrder() {

        OrderRequest request = new OrderRequest();
        request.setName("Old");
        request.setSource("OldSource");
        request.setDestination("OldDestination");
        UUID partnerId = createPartner();
        request.setPartnerId(partnerId);

        OrderResponse created =
                orderService.createOrder(request);

        OrderRequest update = new OrderRequest();
        update.setName("New");
        update.setSource("NewSource");
        update.setDestination("NewDestination");
        update.setPartnerId(partnerId);

        OrderResponse updated =
                orderService.updateOrder(created.getId(), update);

        assertEquals("New", updated.getName());
        assertEquals("NewSource", updated.getSource());
        assertEquals("NewDestination", updated.getDestination());

        OrderEntity entity =
                orderRepository.getOrderById(created.getId());

        assertEquals("New", entity.getName());
        assertEquals("NewSource", entity.getSource());
        assertEquals("NewDestination", entity.getDestination());
    }

    @Test
    @DisplayName("Удаление заказа")
    void deleteOrder_ShouldDeleteOrder() {

        OrderRequest request = new OrderRequest();
        request.setName("Delete");
        request.setSource("A");
        request.setDestination("B");
        request.setPartnerId(createPartner());

        OrderResponse created =
                orderService.createOrder(request);

        UUID id = created.getId();

        orderService.deleteOrder(id);

        assertThrows(
                OrderNotFoundException.class,
                () -> orderRepository.getOrderById(id)
        );
    }

    @Test
    @DisplayName("Получение несуществующего заказа")
    void getOrderById_ShouldThrowException() {

        UUID id = UUID.randomUUID();

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(id)
        );
    }

    @Test
    @DisplayName("Удаление несуществующего заказа")
    void deleteOrder_ShouldThrowException_WhenOrderDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.deleteOrder(id)
        );
    }

}