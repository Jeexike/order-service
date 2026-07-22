package com.example.orderservice.service.jdbcService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PartnerService;
import com.example.orderservice.testdata.TestDataFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"repository.type=jdbc"})
class OrderServiceJdbcComponentTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PartnerService partnerService;

    private UUID createPartner() {
        PartnerRequest partnerRequest = TestDataFactory.createPartnerRequest();
        PartnerResponse partner = partnerService.createPartner(partnerRequest);
        return partner.getId();
    }

    @Test
    @DisplayName("Создание заказа")
    void createOrder_ShouldCreateOrder() {

        OrderRequest request = TestDataFactory.createOrderRequest(createPartner());
        request.setName("Order");
        request.setSource("Moscow");
        request.setDestination("SPB");

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response.getId());
        assertEquals("Order", response.getName());

        OrderResponse fromDb = orderService.getOrderById(response.getId());

        assertEquals(response.getId(), fromDb.getId());
    }

    @Test
    @DisplayName("Получение всех заказов")
    void getOrders_ShouldReturnOrders() {

        OrderRequest first = TestDataFactory.createOrderRequest(createPartner());
        first.setName("One");
        first.setSource("A");
        first.setDestination("B");

        OrderRequest second = TestDataFactory.createOrderRequest(createPartner());
        second.setName("Two");
        second.setSource("C");
        second.setDestination("D");

        orderService.createOrder(first);
        orderService.createOrder(second);

        List<OrderResponse> orders = orderService.getOrders();

        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("Обновление заказа")
    void updateOrder_ShouldUpdateOrder() {

        UUID partnerId = createPartner();

        OrderRequest request = TestDataFactory.createOrderRequest(partnerId);
        request.setName("Old");
        request.setSource("A");
        request.setDestination("B");

        OrderResponse created = orderService.createOrder(request);

        OrderRequest update = TestDataFactory.createOrderRequest(partnerId);
        update.setName("New");
        update.setSource("X");
        update.setDestination("Y");

        OrderResponse updated = orderService.updateOrder(created.getId(), update);

        assertEquals("New", updated.getName());
        assertEquals("X", updated.getSource());
        assertEquals("Y", updated.getDestination());
    }

    @Test
    @DisplayName("Удаление заказа")
    void deleteOrder_ShouldDeleteOrder() {

        OrderRequest request = TestDataFactory.createOrderRequest(createPartner());
        request.setName("Delete");
        request.setSource("A");
        request.setDestination("B");

        OrderResponse created = orderService.createOrder(request);

        UUID id = created.getId();

        orderService.deleteOrder(id);

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(id));
    }

    @Test
    @DisplayName("Получение несуществующего заказа")
    void getOrderById_ShouldThrowException_WhenOrderDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(id));
    }

    @Test
    @DisplayName("Удаление несуществующего")
    void deleteOrder_ShouldThrowException_WhenOrderDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(id));
    }

    @Test
    @DisplayName("Создание заказа с партнером")
    void createOrder_WithPartner_ShouldCreateOrder() {

        UUID partnerId = createPartner();

        OrderRequest request = TestDataFactory.createOrderRequest(partnerId);
        request.setName("Order");
        request.setSource("A");
        request.setDestination("B");

        OrderResponse response = orderService.createOrder(request);

        assertEquals(partnerId, response.getPartnerId());
    }
}
