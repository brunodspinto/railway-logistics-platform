package org.example.trees;

import java.util.*;

public class AVLTree<K extends Comparable<K>, V extends Comparable<V>> {
    private Node<K, V> root;
    private int size;

    public AVLTree() {
        this.root = null;
        this.size = 0;
    }

    public void insert(K key, V value) {
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.addValue(value);
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public List<V> search(K key) {
        Node<K, V> node = search(root, key);
        return node == null ? new ArrayList<>() : node.values;
    }

    private Node<K, V> search(Node<K, V> node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) return search(node.left, key);
        if (cmp > 0) return search(node.right, key);
        return node;
    }

    public List<V> rangeSearch(K min, K max) {
        List<V> result = new ArrayList<>();
        rangeSearch(root, min, max, result);
        return result;
    }

    private void rangeSearch(Node<K, V> node, K min, K max, List<V> result) {
        if (node == null) return;

        if (node.key.compareTo(min) < 0) {
            rangeSearch(node.right, min, max, result);
        } else if (node.key.compareTo(max) > 0) {
            rangeSearch(node.left, min, max, result);
        } else {
            rangeSearch(node.left, min, max, result);
            result.addAll(node.values);
            rangeSearch(node.right, min, max, result);
        }
    }

    public List<V> inOrder() {
        List<V> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node<K, V> node, List<V> result) {
        if (node == null) return;
        inOrder(node.left, result);
        result.addAll(node.values);
        inOrder(node.right, result);
    }

    private Node<K, V> rightRotate(Node<K, V> y) {
        Node<K, V> x = y.left;
        Node<K, V> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    private Node<K, V> leftRotate(Node<K, V> x) {
        Node<K, V> y = x.right;
        Node<K, V> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
    }

    private int height(Node<K, V> node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node<K, V> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    public int size() {
        return size;
    }

    public int height() {
        return height(root);
    }

    static class Node<K extends Comparable<K>, V extends Comparable<V>> {
        K key;
        List<V> values;
        Node<K, V> left;
        Node<K, V> right;
        int height;

        Node(K key, V value) {
            this.key = key;
            this.values = new ArrayList<>();
            this.values.add(value);
            this.height = 1;
        }

        void addValue(V value) {
            int pos = Collections.binarySearch(values, value);
            if (pos < 0) {
                values.add(-pos - 1, value);
            }
        }
    }
}