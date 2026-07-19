package com.example.orderservice.controller;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.PartnerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "repository.type=jpa",
        "spring.test.database.replace=none"
})
@ActiveProfiles("test")
class PartnerControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private OrderService orderService;

    private PartnerResponse createPartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        return partnerService.createPartner(request);
    }

    @Test
    @DisplayName("POST /partners -> 201")
    void createPartner_ShouldReturnCreated() throws Exception {

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        mockMvc.perform(post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Partner"))
                .andExpect(jsonPath("$.email").value("partner@test.com"));
    }

    @Test
    @DisplayName("GET /partners/{id}/orders -> 200")
    void getOrdersByPartnerId_ShouldReturnOrders() throws Exception {

        PartnerResponse partner = createPartner();

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

        mockMvc.perform(get("/partners/" + partner.getId() + "/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].partnerId").value(partner.getId().toString()))
                .andExpect(jsonPath("$[1].partnerId").value(partner.getId().toString()));
    }

    @Test
    @DisplayName("DELETE /partners/{id} -> 204")
    void deletePartner_ShouldReturnNoContent() throws Exception {

        PartnerResponse partner = createPartner();

        mockMvc.perform(delete("/partners/" + partner.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/partners/" + partner.getId() + "/orders"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /partners invalid body -> 400")
    void createPartner_WithInvalidBody_ShouldReturn400() throws Exception {

        PartnerRequest request = new PartnerRequest();
        request.setName("");
        request.setEmail("wrong-email");

        mockMvc.perform(post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /partners/{id}/orders -> 404")
    void getOrdersByPartnerId_ShouldReturn404() throws Exception {

        mockMvc.perform(get("/partners/" + UUID.randomUUID() + "/orders"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /partners/{id} -> 404")
    void deletePartner_ShouldReturn404() throws Exception {

        mockMvc.perform(delete("/partners/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /partners/{id}/orders с нечисловым/невалидным ID -> 400")
    void getOrdersByPartnerId_nonUuidId_returnsBadRequest() throws Exception {

        mockMvc.perform(get("/partners/not-a-uuid/orders"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /partners/{id} с нечисловым/невалидным ID -> 400")
    void deletePartner_nonUuidId_returnsBadRequest() throws Exception {

        mockMvc.perform(delete("/partners/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}