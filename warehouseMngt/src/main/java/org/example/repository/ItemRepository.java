package org.example.repository;


import org.example.domain.Item;
import org.example.exception.ValidationException;

import java.util.*;

public class ItemRepository {
    private final Map<String, Item> items = new HashMap<>();

    public void save(Item item) {
        if (item == null) {
            throw new ValidationException("Cannot save null item");
        }
        items.put(item.getSku(), item);
    }

    public void saveAll(List<Item> items) {
        for (Item item : items) {
            save(item);
        }
    }

    public Item findBySku(String sku) {
        return items.get(sku);
    }

    public boolean existsBySku(String sku) {
        return items.containsKey(sku);
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public int count() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }
}

