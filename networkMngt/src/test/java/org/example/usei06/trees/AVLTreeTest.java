package org.example.usei06.trees;

import org.example.trees.AVLTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Testes unitários para AVLTree - USEI06
 */
class AVLTreeTest {

    private AVLTree<Integer, String> tree;

    @BeforeEach
    void setUp() {
        tree = new AVLTree<>();
    }

    @Test
    @DisplayName("Empty tree initialization")
    void testEmptyTreeInitialization() {
        assertEquals(0, tree.size());
        assertEquals(0, tree.height());
    }

    @Test
    @DisplayName("Insert and search single element")
    void testInsertAndSearch() {
        tree.insert(10, "Station_10");

        assertEquals(1, tree.size());
        List<String> results = tree.search(10);
        assertEquals(1, results.size());
        assertEquals("Station_10", results.get(0));
    }

    @Test
    @DisplayName("Insert duplicate key adds value to existing node")
    void testInsertDuplicateKey() {
        tree.insert(10, "Station_A");
        tree.insert(10, "Station_B");

        assertEquals(1, tree.size());
        List<String> values = tree.search(10);
        assertEquals(2, values.size());
    }

    @Test
    @DisplayName("Insert maintains AVL balance property")
    void testAVLBalance() {
        // Inserções que causariam desbalanceamento
        for (int i = 1; i <= 7; i++) {
            tree.insert(i, "Station_" + i);
        }

        assertEquals(7, tree.size());
        assertTrue(tree.height() <= 4);
    }

    @Test
    @DisplayName("Search non-existing key returns empty list")
    void testSearchNonExisting() {
        tree.insert(10, "Station_10");
        List<String> results = tree.search(99);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Range search returns values in range")
    void testRangeSearch() {
        tree.insert(10, "Station_10");
        tree.insert(5, "Station_5");
        tree.insert(15, "Station_15");
        tree.insert(3, "Station_3");
        tree.insert(20, "Station_20");

        List<String> results = tree.rangeSearch(5, 15);

        assertEquals(3, results.size());
        assertTrue(results.contains("Station_5"));
        assertTrue(results.contains("Station_10"));
        assertTrue(results.contains("Station_15"));
    }

    @Test
    @DisplayName("In-order traversal returns sorted values")
    void testInOrderTraversal() {
        tree.insert(10, "C");
        tree.insert(5, "B");
        tree.insert(15, "D");
        tree.insert(3, "A");

        List<String> inOrder = tree.inOrder();

        assertEquals(4, inOrder.size());
        assertEquals("A", inOrder.get(0));
        assertEquals("B", inOrder.get(1));
        assertEquals("C", inOrder.get(2));
        assertEquals("D", inOrder.get(3));
    }

    @Test
    @DisplayName("USEI06 - Multiple stations at same coordinates")
    void testMultipleStationsAtCoordinates() {
        AVLTree<Double, String> coordTree = new AVLTree<>();

        coordTree.insert(38.71387, "Lisboa Oriente");
        coordTree.insert(38.71387, "Lisboa Santa Apolónia");

        List<String> stations = coordTree.search(38.71387);

        assertEquals(2, stations.size());
        assertEquals("Lisboa Oriente", stations.get(0));
        assertEquals("Lisboa Santa Apolónia", stations.get(1));
    }

    @Test
    @DisplayName("Height grows logarithmically")
    void testHeightGrowth() {
        for (int i = 1; i <= 15; i++) {
            tree.insert(i, "Station_" + i);
        }

        assertTrue(tree.height() <= 5);
    }

    @Test
    @DisplayName("Range search with no results")
    void testRangeSearchEmpty() {
        tree.insert(10, "A");
        tree.insert(20, "B");

        List<String> results = tree.rangeSearch(12, 18);
        assertTrue(results.isEmpty());
    }
}
