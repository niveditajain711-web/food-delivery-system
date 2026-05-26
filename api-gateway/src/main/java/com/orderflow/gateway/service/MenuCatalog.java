package com.orderflow.gateway.service;

import com.orderflow.gateway.dto.MenuItemDto;

import java.util.List;

/**
 * Static menu aligned with inventory-service sample data (no list RPC on inventory yet).
 */
public final class MenuCatalog {

    private static final List<MenuItemDto> ITEMS = List.of(
            new MenuItemDto("burger-1", "Classic Burger", "restaurant-1"),
            new MenuItemDto("fries-1", "French Fries", "restaurant-1"),
            new MenuItemDto("pizza-1", "Margherita Pizza", "restaurant-2")
    );

    private MenuCatalog() {
    }

    public static List<MenuItemDto> all() {
        return ITEMS;
    }

    public static List<MenuItemDto> byRestaurant(String restaurantId) {
        return ITEMS.stream()
                .filter(item -> item.restaurantId().equals(restaurantId))
                .toList();
    }
}
