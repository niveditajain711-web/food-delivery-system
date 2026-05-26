package com.orderflow.inventory.config;

import com.orderflow.inventory.domain.InventoryItem;
import com.orderflow.inventory.repository.InventoryItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds demo menu items so PlaceOrder can be tested without manual DB setup.
 */
@Component
public class SampleInventoryLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleInventoryLoader.class);

    private final InventoryItemRepository repository;

    public SampleInventoryLoader(InventoryItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new InventoryItem("burger-1", "restaurant-1", "Classic Burger", 50),
                new InventoryItem("fries-1", "restaurant-1", "French Fries", 100),
                new InventoryItem("pizza-1", "restaurant-2", "Margherita Pizza", 30)
        ));
        log.info("Loaded sample inventory (3 products)");
    }
}
