package com.emart.ops.controller;

import com.emart.ops.dto.CreateOrderRequest;
import com.emart.ops.exception.ResourceNotFoundException;
import com.emart.ops.model.Order;
import com.emart.ops.model.OrderStatus;
import com.emart.ops.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        Order order = new Order(request.getItems());
        Order created = service.createOrder(order);
        return ResponseEntity.ok(created);
    }

    /**
     * Fetch an order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        Order order = service.getOrderById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return ResponseEntity.ok(order);
    }

    /**
     * List all orders (optionally filtered by status)
     */
    @GetMapping
    public ResponseEntity<List<Order>> listOrders(@RequestParam(required = false) OrderStatus status) {
        List<Order> orders = service.getAllOrders();
        if (status != null) {
            orders = orders.stream()
                           .filter(o -> o.getStatus() == status)
                           .collect(Collectors.toList());
        }
        return ResponseEntity.ok(orders);
    }

    /**
     * Cancel an order by ID
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        boolean cancelled = service.cancelOrder(uuid);
        if (!cancelled) {
            throw new ResourceNotFoundException("Order not in pending status: " + id);
        }
        return ResponseEntity.ok("Order cancelled successfully");
    }

    /**
     * Update an order's status (e.g., SHIPPED, DELIVERED)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable String id, @RequestParam OrderStatus status) {
        UUID uuid = UUID.fromString(id);
        boolean updated = service.updateStatus(uuid, status);
        if (!updated) {
            throw new ResourceNotFoundException("Order not found: " + id);
        }
        return ResponseEntity.ok("Order status updated successfully");
    }
}