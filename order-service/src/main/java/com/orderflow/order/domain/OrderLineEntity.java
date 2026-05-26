package com.orderflow.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_lines")
public class OrderLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    protected OrderLineEntity() {
    }

    public OrderLineEntity(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
