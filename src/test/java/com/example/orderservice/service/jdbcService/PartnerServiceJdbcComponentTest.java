package com.example.orderservice.service.jdbcService;

import com.example.orderservice.database.TestDatabaseContainerService;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.exception.PartnerNotFoundException;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PartnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "repository.type=jdbc",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.test.database.replace=none"
})
@ActiveProfiles("test")
class PartnerServiceJdbcComponentTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        TestDatabaseContainerService.configureProperties(registry);
    }

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void cleanDatabase() {
        TestDatabaseContainerService.cleanDatabase();
    }

    @Test
    @DisplayName("Создание партнера")
    void createPartner_ShouldCreatePartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        PartnerResponse response = partnerService.createPartner(request);

        assertNotNull(response.getId());
        assertEquals("Partner", response.getName());
        assertEquals("partner@test.com", response.getEmail());
    }

    @Test
    @DisplayName("Получение партнера")
    void getPartnerById_ShouldReturnPartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        PartnerResponse created = partnerService.createPartner(request);

        PartnerResponse found =
                partnerService.getPartnerById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Partner", found.getName());
    }

    @Test
    @DisplayName("Получение заказов партнера")
    void getOrdersByPartnerId_ShouldReturnOrders() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        PartnerResponse partner =
                partnerService.createPartner(request);

        OrderRequest first = new OrderRequest();
        first.setName("Order1");
        first.setSource("A");
        first.setDestination("B");
        first.setPartnerId(partner.getId());

        OrderRequest second = new OrderRequest();
        second.setName("Order2");
        second.setSource("C");
        second.setDestination("D");
        second.setPartnerId(partner.getId());

        orderService.createOrder(first);
        orderService.createOrder(second);

        List<OrderResponse> orders =
                partnerService.getOrdersByPartnerId(partner.getId());

        assertEquals(2, orders.size());

        assertTrue(
                orders.stream()
                        .allMatch(o -> o.getPartnerId().equals(partner.getId()))
        );
    }

    @Test
    @DisplayName("Удаление партнера")
    void deletePartner_ShouldDeletePartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        PartnerResponse partner =
                partnerService.createPartner(request);

        partnerService.deletePartner(partner.getId());

        assertThrows(
                PartnerNotFoundException.class,
                () -> partnerService.getPartnerById(partner.getId())
        );
    }

    @Test
    @DisplayName("Удаление несуществующего партнера")
    void deletePartner_ShouldThrowException_WhenPartnerDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(
                PartnerNotFoundException.class,
                () -> partnerService.deletePartner(id)
        );
    }

    @Test
    @DisplayName("Получение несуществующего партнера")
    void getPartnerById_ShouldThrowException() {

        assertThrows(
                PartnerNotFoundException.class,
                () -> partnerService.getPartnerById(java.util.UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Получение заказов отсутствующего партнера")
    void getOrdersByPartnerId_ShouldThrowException() {

        assertThrows(
                PartnerNotFoundException.class,
                () -> partnerService.getOrdersByPartnerId(java.util.UUID.randomUUID())
        );
    }
}