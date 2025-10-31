package com.emart.ops.service;

import com.emart.ops.model.Order;
import com.emart.ops.model.OrderItem;
import com.emart.ops.model.OrderStatus;
import com.emart.ops.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testCreateOrder_Success() {
        Order order = new Order(Arrays.asList(new OrderItem("Laptop", 1, 1000)));
        Order created = service.createOrder(order);

        assertNotNull(created.getId());
        assertEquals(1, created.getItems().size());
    }

    @Test
    void testCancelOrder_Success() {
        Order order = new Order(Arrays.asList(new OrderItem("Monitor", 1, 1200)));
        service.createOrder(order);

        boolean cancelled = service.cancelOrder(order.getId());
        assertTrue(cancelled);

        Optional<Order> found = repository.findById(order.getId());
        assertTrue(found.isPresent());
        assertEquals(OrderStatus.CANCELLED, found.get().getStatus());
    }

    @Test
    void testCancelOrder_NotFound() {
        boolean result = service.cancelOrder(UUID.randomUUID());
        assertFalse(result);
    }

    @Test
    void testGetAllOrders() {
        service.createOrder(new Order(Arrays.asList(new OrderItem("Mouse", 1, 500))));
        service.createOrder(new Order(Arrays.asList(new OrderItem("Keyboard", 1, 700))));

        List<Order> allOrders = service.getAllOrders();
        assertEquals(2, allOrders.size());
    }

    @Test
    void testCreateOrder_EmptyItems_ThrowsException() {
        Order order = new Order(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(order));
    }

    @Test
    void testUpdateStatus_Success() {
        Order order = service.createOrder(new Order(Arrays.asList(new OrderItem("Tablet", 1, 25000))));
        boolean updated = service.updateStatus(order.getId(), OrderStatus.SHIPPED);

        assertTrue(updated);
        assertEquals(OrderStatus.SHIPPED, repository.findById(order.getId()).get().getStatus());
    }

    @Test
    void testPromotePendingOrders() {
        Order pending = service.createOrder(new Order(Arrays.asList(new OrderItem("Headphones", 1, 800))));
        service.promotePendingOrders();

        Order updated = repository.findById(pending.getId()).get();
        assertEquals(OrderStatus.PROCESSING, updated.getStatus());
    }
}