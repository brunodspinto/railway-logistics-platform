package org.example.trees;

import java.util.*;

/**
 * Node for AVL Tree supporting multiple values per key.
 * Values are kept sorted (ASC) for deterministic behavior.
 *
 * Used for duplicate coordinates: multiple stations at same (lat, lon).
 * Example: Lisboa Santa Apolónia and Lisboa Oriente share coordinates.
 *
 * @param <K> Key type (must be Comparable)
 * @param <V> Value type (must be Comparable for sorting)
 */
class AVLNode<K extends Comparable<K>, V extends Comparable<V>> {
    K key;
    List<V> values;  // Multiple values for same key, sorted ASC
    AVLNode<K, V> left;
    AVLNode<K, V> right;
    int height;

    /**
     * Creates a new AVL node with given key and initial value.
     *
     * @param key the key for this node
     * @param value the first value to store
     */
    public AVLNode(K key, V value) {
        this.key = key;
        this.values = new ArrayList<>();
        this.values.add(value);
        this.left = null;
        this.right = null;
        this.height = 1;
    }


    public void addValue(V value) {
        int i = 0;
        while (i < values.size()) {
            int cmp = value.compareTo(values.get(i));

            if (cmp == 0) {
                return;
            }

            if (cmp < 0) {
                break;
            }

            i++;
        }

        values.add(i, value);
    }

    /**
     * Get all values stored at this node (sorted ASC).
     * Returns a defensive copy to prevent external modification.
     *
     * @return sorted list of values
     */
    public List<V> getValues() {
        return new ArrayList<>(values);
    }


    public void updateHeight() {
        int leftHeight = (left == null) ? 0 : left.height;
        int rightHeight = (right == null) ? 0 : right.height;
        this.height = 1 + Math.max(leftHeight, rightHeight);
    }


    public int getBalance() {
        int leftHeight = (left == null) ? 0 : left.height;
        int rightHeight = (right == null) ? 0 : right.height;
        return rightHeight - leftHeight;  // RIGHT - LEFT (correct formula)
    }
}