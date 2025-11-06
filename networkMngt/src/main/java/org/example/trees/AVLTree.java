package org.example.trees;

import java.util.*;

/**
 * Generic AVL Tree supporting multiple values per key.
 * Handles duplicate coordinates by maintaining sorted lists (by Station name).
 *
 * @param <K> Key type (must be Comparable)
 * @param <V> Value type (must be Comparable for sorting duplicates)
 */
public class AVLTree<K extends Comparable<K>, V extends Comparable<V>> {
    private AVLNode<K, V> root;
    private int size;

    public AVLTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Insert a value with given key.
     * If key exists, adds to the list (sorted).
     */
    public void insert(K key, V value) {
        root = insertRec(root, key, value);
    }

    private AVLNode<K, V> insertRec(AVLNode<K, V> node, K key, V value) {
        // 1. Standard BST insertion
        if (node == null) {
            size++;
            return new AVLNode<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            // Key exists - add to sorted list
            node.addValue(value);
            return node;
        }

        // 2. Update height
        node.updateHeight();

        // 3. Get balance factor
        int balance = node.getBalance();

        // 4. Balance if needed
        // Left-Left case
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rotateRight(node);
        }
        // Right-Right case
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return rotateLeft(node);
        }
        // Left-Right case
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // Right-Left case
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * Search for exact key match.
     * @return List of values (empty if not found)
     */
    public List<V> search(K key) {
        AVLNode<K, V> node = searchRec(root, key);
        return node == null ? new ArrayList<>() : node.getValues();
    }

    private AVLNode<K, V> searchRec(AVLNode<K, V> node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) return searchRec(node.left, key);
        if (cmp > 0) return searchRec(node.right, key);
        return node;
    }

    /**
     * Range search: all values with keys in [minKey, maxKey] (inclusive).
     * Critical for USEI06 latitude/longitude queries.
     */
    public List<V> rangeSearch(K minKey, K maxKey) {
        List<V> result = new ArrayList<>();
        rangeSearchRec(root, minKey, maxKey, result);
        return result;
    }

    private void rangeSearchRec(AVLNode<K, V> node, K minKey, K maxKey, List<V> result) {
        if (node == null) return;

        // If current key < minKey, go right only
        if (node.key.compareTo(minKey) < 0) {
            rangeSearchRec(node.right, minKey, maxKey, result);
        }
        // If current key > maxKey, go left only
        else if (node.key.compareTo(maxKey) > 0) {
            rangeSearchRec(node.left, minKey, maxKey, result);
        }
        // Current key in range
        else {
            rangeSearchRec(node.left, minKey, maxKey, result);
            result.addAll(node.getValues());
            rangeSearchRec(node.right, minKey, maxKey, result);
        }
    }

    /**
     * In-order traversal (sorted by key).
     */
    public List<V> inOrder() {
        List<V> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(AVLNode<K, V> node, List<V> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.addAll(node.getValues());
        inOrderRec(node.right, result);
    }

    // Rotation methods
    private AVLNode<K, V> rotateRight(AVLNode<K, V> y) {
        AVLNode<K, V> x = y.left;
        AVLNode<K, V> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.updateHeight();
        x.updateHeight();

        return x;
    }

    private AVLNode<K, V> rotateLeft(AVLNode<K, V> x) {
        AVLNode<K, V> y = x.right;
        AVLNode<K, V> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.updateHeight();
        y.updateHeight();

        return y;
    }

    // Metrics for USEI06 analysis
    public int getHeight() {
        return root == null ? 0 : root.height;
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, Integer> getBucketSizeDistribution() {
        Map<Integer, Integer> distribution = new HashMap<>();
        collectBucketSizes(root, distribution);
        return distribution;
    }

    private void collectBucketSizes(AVLNode<K, V> node, Map<Integer, Integer> dist) {
        if (node == null) return;

        int bucketSize = node.getValues().size();
        dist.put(bucketSize, dist.getOrDefault(bucketSize, 0) + 1);

        collectBucketSizes(node.left, dist);
        collectBucketSizes(node.right, dist);
    }

    /**
     * Returns temporal complexity analysis string.
     */
    public String getComplexityAnalysis() {
        int n = size;
        int h = getHeight();
        double expectedHeight = Math.log(n) / Math.log(2);

        return String.format(
                "Tree size: %d nodes | Height: %d | Expected: %.2f | " +
                        "Search: O(log n) = O(%d) | Insert: O(log n) = O(%d)",
                n, h, expectedHeight, h, h
        );
    }
}

