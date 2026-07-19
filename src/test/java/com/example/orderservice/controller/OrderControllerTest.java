package com.example.orderservice.controller;

import com.example.orderservice.database.AbstractIntegrationTest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.PartnerRequest;
import com.example.orderservice.dto.PartnerResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "repository.type=jpa",
        "spring.test.database.replace=none"
})
@ActiveProfiles("test")
class OrderControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PartnerService partnerService;

    private UUID createPartner() {

        PartnerRequest request = new PartnerRequest();
        request.setName("Partner");
        request.setEmail("partner@test.com");

        PartnerResponse response =
                partnerService.createPartner(request);

        return response.getId();
    }

    private String createOrder(UUID partnerId) throws Exception {

        OrderRequest request = new OrderRequest();
        request.setName("Order");
        request.setSource("Moscow");
        request.setDestination("SPB");
        request.setPartnerId(partnerId);

        return mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("POST /orders -> 201")
    void createOrder_ShouldReturnCreated() throws Exception {

        UUID partnerId = createPartner();

        OrderRequest request = new OrderRequest();
        request.setName("Laptop");
        request.setSource("Moscow");
        request.setDestination("SPB");
        request.setPartnerId(partnerId);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.source").value("Moscow"))
                .andExpect(jsonPath("$.destination").value("SPB"))
                .andExpect(jsonPath("$.partnerId").value(partnerId.toString()));
    }

    @Test
    @DisplayName("GET /orders -> 200")
    void getOrders_ShouldReturnOrders() throws Exception {

        UUID partnerId = createPartner();

        createOrder(partnerId);
        createOrder(partnerId);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /orders/{id} -> 200")
    void getOrder_ShouldReturnOrder() throws Exception {

        UUID partnerId = createPartner();

        String json = createOrder(partnerId);

        UUID orderId =
                objectMapper.readTree(json)
                        .get("id")
                        .traverse(objectMapper)
                        .readValueAs(UUID.class);

        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.partnerId").value(partnerId.toString()));
    }

    @Test
    @DisplayName("PUT /orders/{id} -> 200")
    void updateOrder_ShouldReturnUpdatedOrder() throws Exception {

        UUID partnerId = createPartner();

        String json = createOrder(partnerId);

        UUID orderId =
                objectMapper.readTree(json)
                        .get("id")
                        .traverse(objectMapper)
                        .readValueAs(UUID.class);

        OrderRequest update = new OrderRequest();
        update.setName("Updated");
        update.setSource("Paris");
        update.setDestination("Berlin");
        update.setPartnerId(partnerId);

        mockMvc.perform(
                        put("/orders/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.source").value("Paris"))
                .andExpect(jsonPath("$.destination").value("Berlin"));
    }

    @Test
    @DisplayName("DELETE /orders/{id} -> 204")
    void deleteOrder_ShouldReturnNoContent() throws Exception {

        UUID partnerId = createPartner();

        String json = createOrder(partnerId);

        UUID orderId =
                objectMapper.readTree(json)
                        .get("id")
                        .traverse(objectMapper)
                        .readValueAs(UUID.class);

        mockMvc.perform(delete("/orders/" + orderId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /orders invalid body -> 400")
    void createOrder_WithInvalidBody_ShouldReturn400() throws Exception {

        OrderRequest request = new OrderRequest();
        request.setName("");
        request.setSource("");
        request.setDestination("");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/{id} -> 404")
    void getOrder_ShouldReturn404() throws Exception {

        mockMvc.perform(get("/orders/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /orders/{id} -> 404")
    void updateOrder_ShouldReturn404() throws Exception {

        UUID partnerId = createPartner();

        OrderRequest request = new OrderRequest();
        request.setName("Test");
        request.setSource("AA");
        request.setDestination("BB");
        request.setPartnerId(partnerId);

        mockMvc.perform(
                        put("/orders/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /orders/{id} несуществующего заказа -> 404")
    void deleteOrder_ShouldReturn404_WhenOrderDoesNotExist() throws Exception {

        mockMvc.perform(delete("/orders/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /orders/{id} с нечисловым/невалидным ID -> 400")
    void getOrder_nonUuidId_returnsBadRequest() throws Exception {

        mockMvc.perform(get("/orders/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /orders/{id} с нечисловым/невалидным ID -> 400")
    void deleteOrder_nonUuidId_returnsBadRequest() throws Exception {

        mockMvc.perform(delete("/orders/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}