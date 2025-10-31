package com.emart.ops.repository;

import com.emart.ops.model.Order;
import com.emart.ops.model.OrderItem;
import com.emart.ops.model.OrderStatus;
import com.emart.ops.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    @Test
    void testSaveAndFindById() {
        Order order = new Order(Arrays.asList(new OrderItem("Phone", 1, 999.0)));
        repository.save(order);

        Order found = repository.findById(order.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void testFindByStatus() {
        Order order1 = new Order(Arrays.asList(new OrderItem("Laptop", 1, 1000.0)));
        Order order2 = new Order(Arrays.asList(new OrderItem("Mouse", 1, 100.0)));
        order2.setStatus(OrderStatus.SHIPPED);

        repository.saveAll(Arrays.asList(order1, order2));

        List<Order> pendingOrders = repository.findByStatus(OrderStatus.PENDING);
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).getItems().get(0).getProductName()).isEqualTo("Laptop");
    }
}