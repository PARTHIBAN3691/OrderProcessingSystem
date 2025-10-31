package com.emart.ops.model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private boolean cancelled;

    public Order() {
        this.id = UUID.randomUUID();
        this.status = OrderStatus.PENDING;
        this.cancelled = false;
    }

    public Order(List<OrderItem> items) {
        this();
        this.items = items;
    }

    public UUID getId() { return id; }
    public List<OrderItem> getItems() { return items; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}