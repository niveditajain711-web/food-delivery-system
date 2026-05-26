package com.orderflow.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id", length = 36)
    private String orderId;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    @Column(name = "restaurant_id", nullable = false, length = 64)
    private String restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineEntity> lines = new ArrayList<>();

    protected OrderEntity() {
    }

    public OrderEntity(String orderId, String customerId, String restaurantId, OrderStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.status = status;
    }

    public void addLine(OrderLineEntity line) {
        lines.add(line);
        line.setOrder(this);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLineEntity> getLines() {
        return lines;
    }
}
