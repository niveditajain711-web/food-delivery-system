package com.orderflow.inventory.repository;

import com.orderflow.inventory.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {
}
