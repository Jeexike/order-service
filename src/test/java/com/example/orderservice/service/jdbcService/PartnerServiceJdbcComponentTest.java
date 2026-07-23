package com.example.orderservice.service.jdbcService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.exception.PartnerNotFoundException;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PartnerService;
import com.example.orderservice.testdata.TestDataFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
        properties = {
            "repository.type=jdbc",
            "spring.jpa.hibernate.ddl-auto=validate",
            "spring.test.database.replace=none"
        })
@ActiveProfiles("test")
class PartnerServiceJdbcComponentTest extends AbstractIntegrationTest {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("Создание партнера")
    void createPartner_ShouldCreatePartner() {

        PartnerRequest request = TestDataFactory.createPartnerRequest();

        PartnerResponse response = partnerService.createPartner(request);

        assertNotNull(response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
    }

    @Test
    @DisplayName("Получение партнера")
    void getPartnerById_ShouldReturnPartner() {

        PartnerRequest request = TestDataFactory.createPartnerRequest();

        PartnerResponse created = partnerService.createPartner(request);

        PartnerResponse found = partnerService.getPartnerById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(request.getName(), found.getName());
    }

    @Test
    @DisplayName("Получение заказов партнера")
    void getOrdersByPartnerId_ShouldReturnOrders() {

        PartnerRequest request = TestDataFactory.createPartnerRequest();

        PartnerResponse partner = partnerService.createPartner(request);

        OrderRequest first = TestDataFactory.createOrderRequest(partner.getId());
        first.setName("Order1");
        first.setSource("A");
        first.setDestination("B");

        OrderRequest second = TestDataFactory.createOrderRequest(partner.getId());
        second.setName("Order2");
        second.setSource("C");
        second.setDestination("D");

        orderService.createOrder(first);
        orderService.createOrder(second);

        List<OrderResponse> orders = partnerService.getOrdersByPartnerId(partner.getId());

        assertEquals(2, orders.size());

        assertTrue(orders.stream().allMatch(o -> o.getPartnerId().equals(partner.getId())));
    }

    @Test
    @DisplayName("Удаление партнера")
    void deletePartner_ShouldDeletePartner() {

        PartnerRequest request = TestDataFactory.createPartnerRequest();

        PartnerResponse partner = partnerService.createPartner(request);

        partnerService.deletePartner(partner.getId());

        assertThrows(PartnerNotFoundException.class, () -> partnerService.getPartnerById(partner.getId()));
    }

    @Test
    @DisplayName("Удаление несуществующего партнера")
    void deletePartner_ShouldThrowException_WhenPartnerDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(PartnerNotFoundException.class, () -> partnerService.deletePartner(id));
    }

    @Test
    @DisplayName("Получение несуществующего партнера")
    void getPartnerById_ShouldThrowException() {

        assertThrows(PartnerNotFoundException.class, () -> partnerService.getPartnerById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Получение заказов отсутствующего партнера")
    void getOrdersByPartnerId_ShouldThrowException() {

        assertThrows(PartnerNotFoundException.class, () -> partnerService.getOrdersByPartnerId(UUID.randomUUID()));
    }
}
