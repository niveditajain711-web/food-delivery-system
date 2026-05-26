package com.orderflow.inventory.service;

import com.orderflow.inventory.domain.InventoryItem;
import com.orderflow.inventory.grpc.OrderLineItem;
import com.orderflow.inventory.repository.InventoryItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Core inventory logic: reserve and release stock for an order.
 */
@Service
public class InventoryBusinessService {

    private static final Logger log = LoggerFactory.getLogger(InventoryBusinessService.class);

    private final InventoryItemRepository repository;

    public InventoryBusinessService(InventoryItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void reserve(String orderId, List<OrderLineItem> items) {
        log.info("Reserving stock for orderId={} itemCount={}", orderId, items.size());
        for (OrderLineItem line : items) {
            reserveLine(line);
        }
    }

    @Transactional
    public void release(String orderId, List<OrderLineItem> items) {
        log.info("Releasing stock for orderId={} itemCount={}", orderId, items.size());
        for (OrderLineItem line : items) {
            releaseLine(line);
        }
    }

    private void reserveLine(OrderLineItem line) {
        InventoryItem item = findItem(line.getProductId());
        if (item.getQuantityAvailable() < line.getQuantity()) {
            throw new IllegalStateException(
                    "Insufficient stock for product " + line.getProductId()
                            + ": requested=" + line.getQuantity()
                            + ", available=" + item.getQuantityAvailable());
        }
        item.setQuantityAvailable(item.getQuantityAvailable() - line.getQuantity());
        repository.save(item);
    }

    private void releaseLine(OrderLineItem line) {
        InventoryItem item = findItem(line.getProductId());
        item.setQuantityAvailable(item.getQuantityAvailable() + line.getQuantity());
        repository.save(item);
    }

    private InventoryItem findItem(String productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + productId));
    }
}
