package com.emart.ops.service;

import com.emart.ops.exception.ResourceNotFoundException;
import com.emart.ops.model.Order;
import com.emart.ops.model.OrderStatus;
import com.emart.ops.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        return repository.save(order);
    }

    public boolean cancelOrder(UUID id) {
        Optional<Order> order = repository.findById(id);
        if(order.isEmpty()) {
        	throw new ResourceNotFoundException("The given order id not found.");
        }
        if (order.get().getStatus().equals(OrderStatus.PENDING)) {
            order.get().setCancelled(true);
            order.get().setStatus(OrderStatus.CANCELLED);
            repository.save(order.get());
            return true;
        }
        return false;
    }

    public Optional<Order> getOrderById(UUID id) {
        return repository.findById(id);
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public boolean updateStatus(UUID id, OrderStatus status) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            order.get().setStatus(status);
            repository.save(order.get());
            return true;
        }
        return false;
    }

    @Scheduled(fixedRate = 300000)
    public void promotePendingOrders() {
        repository.findByStatus(OrderStatus.PENDING)
                .forEach(o -> {
                    o.setStatus(OrderStatus.PROCESSING);
                    repository.save(o);
                });
    }
}