package org.example.trees;

import java.util.*;


public class AVLTree<K extends Comparable<K>, V extends Comparable<V>> {
    private AVLNode<K, V> root;
    private int size;

    public AVLTree() {
        this.root = null;
        this.size = 0;
    }


    public void insert(K key, V value) {
        root = insert(root, key, value);
    }

    private AVLNode<K, V> insert(AVLNode<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new AVLNode<>(key, value);
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

        node.updateHeight();

        int balance = node.getBalance();


        if (balance < -1 && key.compareTo(node.left.key) < 0) {
            return rightRotate(node);
        }


        if (balance > 1 && key.compareTo(node.right.key) > 0) {
            return leftRotate(node);
        }


        if (balance < -1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }


        if (balance > 1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }


    public List<V> search(K key) {
        AVLNode<K, V> node = search(root, key);
        return node == null ? new ArrayList<>() : node.getValues();
    }

    private AVLNode<K, V> search(AVLNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return search(node.left, key);
        }
        if (cmp > 0) {
            return search(node.right, key);
        }
        return node;
    }

    public List<V> rangeSearch(K min, K max) {
        List<V> result = new ArrayList<>();
        rangeSearch(root, min, max, result);
        return result;
    }

    private void rangeSearch(AVLNode<K, V> node, K min, K max, List<V> result) {
        if (node == null) {
            return;
        }

        if (node.key.compareTo(min) < 0) {
            rangeSearch(node.right, min, max, result);
        }
        else if (node.key.compareTo(max) > 0) {
            rangeSearch(node.left, min, max, result);
        }
        else {
            rangeSearch(node.left, min, max, result);
            result.addAll(node.getValues());
            rangeSearch(node.right, min, max, result);
        }
    }

    public List<V> inOrder() {
        List<V> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(AVLNode<K, V> node, List<V> result) {
        if (node == null) {
            return;
        }
        inOrder(node.left, result);
        result.addAll(node.getValues());
        inOrder(node.right, result);
    }

    private AVLNode<K, V> rightRotate(AVLNode<K, V> y) {
        AVLNode<K, V> x = y.left;
        AVLNode<K, V> B = x.right;

        x.right = y;
        y.left = B;

        y.updateHeight();
        x.updateHeight();

        return x;
    }

    private AVLNode<K, V> leftRotate(AVLNode<K, V> x) {
        AVLNode<K, V> y = x.right;
        AVLNode<K, V> B = y.left;


        y.left = x;
        x.right = B;

        x.updateHeight();
        y.updateHeight();

        return y;
    }


    private int height(AVLNode<K, V> node) {
        return node == null ? 0 : node.height;
    }


    public int size() {
        return size;
    }


    public int height() {
        return height(root);
    }
}