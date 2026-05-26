package com.orderflow.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @Column(name = "product_id", length = 64)
    private String productId;

    @Column(name = "restaurant_id", nullable = false, length = 64)
    private String restaurantId;

    @Column(name = "product_name", nullable = false, length = 128)
    private String productName;

    @Column(name = "quantity_available", nullable = false)
    private int quantityAvailable;

    protected InventoryItem() {
    }

    public InventoryItem(String productId, String restaurantId, String productName, int quantityAvailable) {
        this.productId = productId;
        this.restaurantId = restaurantId;
        this.productName = productName;
        this.quantityAvailable = quantityAvailable;
    }

    public String getProductId() {
        return productId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
}
