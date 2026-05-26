package com.orderflow.gateway.web;

import com.orderflow.gateway.dto.MenuItemDto;
import com.orderflow.gateway.service.MenuCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @GetMapping
    public List<MenuItemDto> getMenu(@RequestParam(required = false) String restaurantId) {
        if (restaurantId == null || restaurantId.isBlank()) {
            return MenuCatalog.all();
        }
        return MenuCatalog.byRestaurant(restaurantId);
    }
}
