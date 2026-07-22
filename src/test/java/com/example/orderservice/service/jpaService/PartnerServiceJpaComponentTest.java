package com.example.orderservice.service.jpaService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.example.orderservice.testdata.TestDataFactory;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
        properties = {
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

        PartnerRequest request = TestDataFactory.createPartnerRequest();
        request.setName("Amazon");
        request.setEmail("amazon@test.com");

        PartnerResponse response = partnerService.createPartner(request);

        assertNotNull(response.getId());

        PartnerEntity entity = partnerRepository.getPartnerById(response.getId());

        assertEquals("Amazon", entity.getName());
        assertEquals("amazon@test.com", entity.getEmail());
    }

    @Test
    @DisplayName("Получение партнера по id")
    void getPartnerById_ShouldReturnPartner() {

        PartnerRequest request = TestDataFactory.createPartnerRequest();
        request.setName("Google");
        request.setEmail("google@test.com");

        PartnerResponse created = partnerService.createPartner(request);

        PartnerResponse found = partnerService.getPartnerById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Google", found.getName());
        assertEquals("google@test.com", found.getEmail());
    }

    @Test
    @DisplayName("Удаление партнера")
    void deletePartner_ShouldDeletePartner() {

        PartnerRequest request = TestDataFactory.createPartnerRequest();
        request.setName("Delete");
        request.setEmail("delete@test.com");

        PartnerResponse created = partnerService.createPartner(request);

        UUID id = created.getId();

        partnerService.deletePartner(id);

        assertThrows(PartnerNotFoundException.class, () -> partnerRepository.getPartnerById(id));
    }

    @Test
    @DisplayName("Получение заказов партнера")
    void getOrdersByPartnerId_ShouldReturnOrders() {

        PartnerRequest partnerRequest = TestDataFactory.createPartnerRequest();
        PartnerResponse partner = partnerService.createPartner(partnerRequest);

        OrderRequest first = TestDataFactory.createOrderRequest(partner.getId());
        first.setName("First");
        first.setSource("A");
        first.setDestination("B");

        OrderRequest second = TestDataFactory.createOrderRequest(partner.getId());
        second.setName("Second");
        second.setSource("C");
        second.setDestination("D");

        orderService.createOrder(first);
        orderService.createOrder(second);

        List<OrderResponse> orders = partnerService.getOrdersByPartnerId(partner.getId());
        assertNotNull(orders);
        assertEquals(2, orders.size());

        assertTrue(orders.stream().allMatch(o -> partner.getId().equals(o.getPartnerId())));
        assertTrue(orders.stream().anyMatch(o -> o.getName().equals("First")));
        assertTrue(orders.stream().anyMatch(o -> o.getName().equals("Second")));
    }

    @Test
    @DisplayName("Удаление несуществующего партнера")
    void deletePartner_ShouldThrowException_WhenPartnerDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(PartnerNotFoundException.class, () -> partnerService.deletePartner(id));
    }

    @Test
    @DisplayName("Получение заказов отсутствующего партнера")
    void getOrdersByPartnerId_ShouldThrowException() {

        assertThrows(PartnerNotFoundException.class, () -> partnerService.getOrdersByPartnerId(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Получение отсутствующего партнера")
    void getPartnerById_ShouldThrowException() {

        assertThrows(PartnerNotFoundException.class, () -> partnerService.getPartnerById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Создание нескольких партнеров")
    void createManyPartners_ShouldPersistAll() {

        for (int i = 0; i < 5; i++) {

            PartnerRequest request = TestDataFactory.createPartnerRequest();
            request.setName("Partner " + i);
            request.setEmail("partner" + i + "@mail.com");

            partnerService.createPartner(request);
        }

        assertEquals(5, partnerRepository.getAllPartners().size());
    }
}
