package org.example.trees;

import java.util.*;



class AVLNode<K extends Comparable<K>, V extends Comparable<V>> {
    K key;
    List<V> values;
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
        return rightHeight - leftHeight;
    }
}