package org.example.trees;

import java.util.*;

/**
 * Node for AVL Tree supporting multiple values per key.
 * Values are kept sorted for deterministic behavior.
 *
 * @param <K> Key type
 * @param <V> Value type (must be Comparable for sorting)
 */
class AVLNode<K extends Comparable<K>, V extends Comparable<V>> {
    K key;
    List<V> values;  // Multiple values for same key (e.g., Lisbon stations)
    AVLNode<K, V> left;
    AVLNode<K, V> right;
    int height;

    public AVLNode(K key, V value) {
        this.key = key;
        this.values = new ArrayList<>();
        this.values.add(value);
        this.left = null;
        this.right = null;
        this.height = 1;
    }

    /**
     * Add value to this node, keeping list sorted (ASC).
     * Critical for duplicate coordinates: sorted by Station name.
     */
    public void addValue(V value) {
        // Binary search for insertion point
        int pos = Collections.binarySearch(values, value);
        if (pos < 0) {
            // Not found - insert at correct position
            values.add(-pos - 1, value);
        }
        // If found, we could ignore (no duplicates) or allow
        // For stations: same coords + same name = truly duplicate, ignore
    }

    /**
     * Get all values (sorted).
     */
    public List<V> getValues() {
        return new ArrayList<>(values);  // Defensive copy
    }

    /**
     * Update height based on children.
     */
    public void updateHeight() {
        int leftHeight = (left == null) ? 0 : left.height;
        int rightHeight = (right == null) ? 0 : right.height;
        this.height = 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * Get balance factor (left height - right height).
     * > 1 = left-heavy, < -1 = right-heavy
     */
    public int getBalance() {
        int leftHeight = (left == null) ? 0 : left.height;
        int rightHeight = (right == null) ? 0 : right.height;
        return leftHeight - rightHeight;
    }
}

