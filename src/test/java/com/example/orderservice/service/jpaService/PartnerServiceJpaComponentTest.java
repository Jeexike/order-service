package com.example.orderservice.service.jpaService;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.entity.PartnerEntity;
import com.example.orderservice.exception.PartnerNotFoundException;
import com.example.orderservice.repository.PartnerRepository;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PartnerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "repository.type=jpa",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=true",
        "spring.test.database.replace=none"
})
class PartnerServiceJpaComponentTest extends AbstractIntegrationTest {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    @DisplayName("Создание партнера")
    void createPartner_ShouldCreatePartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Amazon");
        request.setEmail("amazon@test.com");

        PartnerResponse response = partnerService.createPartner(request);

        assertNotNull(response.getId());

        PartnerEntity entity =
                partnerRepository.getPartnerById(response.getId());

        assertEquals("Amazon", entity.getName());
        assertEquals("amazon@test.com", entity.getEmail());
    }

    @Test
    @DisplayName("Получение партнера по id")
    void getPartnerById_ShouldReturnPartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Google");
        request.setEmail("google@test.com");

        PartnerResponse created =
                partnerService.createPartner(request);

        PartnerResponse found =
                partnerService.getPartnerById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Google", found.getName());
        assertEquals("google@test.com", found.getEmail());
    }

    @Test
    @DisplayName("Удаление партнера")
    void deletePartner_ShouldDeletePartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Delete");
        request.setEmail("delete@test.com");

        PartnerResponse created =
                partnerService.createPartner(request);

        UUID id = created.getId();

        partnerService.deletePartner(id);

        assertThrows(
                PartnerNotFoundException.class,
                () -> partnerRepository.getPartnerById(id)
        );
    }

    @Test
    @DisplayName("Получение заказов партнера")
    void getOrdersByPartnerId_ShouldReturnOrders() {

        PartnerRequest partnerRequest = new PartnerRequest();
        partnerRequest.setName("Partner");
        partnerRequest.setEmail("partner@test.com");

        PartnerResponse partner = partnerService.createPartner(partnerRequest);

        OrderRequest first = new OrderRequest();
        first.setName("First");
        first.setSource("A");
        first.setDestination("B");
        first.setPartnerId(partner.getId());

        OrderRequest second = new OrderRequest();
        second.setName("Second");
        second.setSource("C");
        second.setDestination("D");
        second.setPartnerId(partner.getId());

        orderService.createOrder(first);
        orderService.createOrder(second);

        List<OrderResponse> orders =
                partnerService.getOrdersByPartnerId(partner.getId());
        assertNotNull(orders);
        assertEquals(2, orders.size());

        assertTrue(
                orders.stream()
                        .allMatch(o -> partner.getId().equals(o.getPartnerId()))
        );
        assertTrue(
                orders.stream()
                        .anyMatch(o -> o.getName().equals("First"))
        );
        assertTrue(
                orders.stream()
                        .anyMatch(o -> o.getName().equals("Second"))
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
    @DisplayName("Получение заказов отсутствующего партнера")
    void getOrdersByPartnerId_ShouldThrowException() {

        assertThrows(
                PartnerNotFoundException.class,
                () -> partnerService.getOrdersByPartnerId(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Получение отсутствующего партнера")
    void getPartnerById_ShouldThrowException() {

        assertThrows(
                PartnerNotFoundException.class,
                () -> partnerService.getPartnerById(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Создание нескольких партнеров")
    void createManyPartners_ShouldPersistAll() {

        for (int i = 0; i < 5; i++) {

            PartnerRequest request = new PartnerRequest();
            request.setName("Partner " + i);
            request.setEmail("partner" + i + "@mail.com");

            partnerService.createPartner(request);
        }

        assertEquals(
                5,
                partnerRepository.getAllPartners().size()
        );
    }
}