package com.emart.ops.controller;

import com.emart.ops.dto.CreateOrderRequest;
import com.emart.ops.model.OrderItem;
import com.emart.ops.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController using MockMvc and H2 in-memory database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private CreateOrderRequest orderRequest;

    @BeforeEach
    void setup() {
        OrderItem item = new OrderItem("Laptop", 1, 1000);
        orderRequest = new CreateOrderRequest();
        orderRequest.setItems(List.of(item));
    }

    @Test
    void testCreateOrder() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", not(emptyString())))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetOrderById() throws Exception {
        // Step 1: Create an order
        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String id = mapper.readTree(response).get("id").asText();

        // Step 2: Fetch it
        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"));
    }

    @Test
    void testListOrders() throws Exception {
        // Create 2 orders
        CreateOrderRequest req1 = new CreateOrderRequest();
        req1.setItems(Arrays.asList(new OrderItem("Phone", 1, 700)));

        CreateOrderRequest req2 = new CreateOrderRequest();
        req2.setItems(Arrays.asList(new OrderItem("Book", 3, 150)));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req2)))
                .andExpect(status().isOk());

        // List orders
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testCancelOrder() throws Exception {
        // Create order
        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(orderRequest)))
                .andReturn().getResponse().getContentAsString();

        String id = mapper.readTree(response).get("id").asText();

        // Cancel order
        mockMvc.perform(post("/orders/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Order cancelled successfully")));
    }

    @Test
    void testUpdateStatus() throws Exception {
        // Create order
        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(orderRequest)))
                .andReturn().getResponse().getContentAsString();

        String id = mapper.readTree(response).get("id").asText();

        // Update status
        mockMvc.perform(put("/orders/{id}/status", id)
                        .param("status", OrderStatus.SHIPPED.name()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Order status updated")));
    }

    @Test
    void testGetOrderNotFound() throws Exception {
        mockMvc.perform(get("/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}