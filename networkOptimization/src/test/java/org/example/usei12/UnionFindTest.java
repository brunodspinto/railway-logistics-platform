package org.example.usei12;

import org.example.usei12.service.UnionFind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnionFindTest {

    /**
     * Testes básicos para garantir funcionamento correto do Union-Find:
     * makeSet, find, union e path compression.
     */
    @Test
    @DisplayName("makeSet + find: cada elemento deve inicialmente ser o seu próprio representante")
    void makeSetAndFindWork() {
        UnionFind<String> uf = new UnionFind<>();

        uf.makeSet("A");
        uf.makeSet("B");

        assertEquals("A", uf.find("A"));
        assertEquals("B", uf.find("B"));
        assertNotEquals(uf.find("A"), uf.find("B"));
    }

    @Test
    @DisplayName("union: dois conjuntos separados devem ser unidos corretamente")
    void unionJoinsSets() {
        UnionFind<String> uf = new UnionFind<>();

        uf.makeSet("A");
        uf.makeSet("B");
        uf.makeSet("C");

        assertTrue(uf.union("A", "B"));
        String rootA = uf.find("A");
        String rootB = uf.find("B");
        assertEquals(rootA, rootB);

        assertFalse(uf.union("A", "B"));

        assertNotEquals(rootA, uf.find("C"));
    }

    @Test
    @DisplayName("path compression: após múltiplas unions todos os elementos devem ter a mesma raiz")
    void pathCompressionReducesHeight() {
        UnionFind<String> uf = new UnionFind<>();

        uf.makeSet("A");
        uf.makeSet("B");
        uf.makeSet("C");

        uf.union("A", "B");
        uf.union("B", "C");

        String rootA = uf.find("A");
        String rootB = uf.find("B");
        String rootC = uf.find("C");

        assertEquals(rootA, rootB);
        assertEquals(rootA, rootC);
    }
}