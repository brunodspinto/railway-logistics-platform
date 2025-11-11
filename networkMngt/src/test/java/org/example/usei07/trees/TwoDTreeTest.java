package org.example.usei07.trees;

import org.example.domain.Station;
import org.example.trees.Node2D;
import org.example.trees.TwoDTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class TwoDTreeTest {

    private TwoDTree tree;
    private Station sA, sB, sC;
    private Station lisbonOriente, lisbonSantaApolonia, porto, faro;
    private List<Station> latSorted, lonSorted;

    @BeforeEach
    void setUp() {
        tree = new TwoDTree();

        // Estações para o exemplo de Lisboa (coordenadas duplicadas [687])
        lisbonOriente = new Station("Lisbon Oriente", 38.71387, -9.122271, "PT", "WET", "WET/GMT", true, true, false);
        lisbonSantaApolonia = new Station("Lisbon Santa Apolonia", 38.71387, -9.122271, "PT", "WET", "WET/GMT", true, true, false);

        // Outras estações
        porto = new Station("Porto Campanha", 41.14961, -8.58397, "PT", "WET", "WET/GMT", true, true, false);
        faro = new Station("Faro", 37.01757, -7.93041, "PT", "WET", "WET/GMT", true, true, false);

        // Estações simples para testes de equilíbrio
        sA = new Station("A", 40, -8, "PT", "WET", "WET/GMT", true, true, false); // Mediana Lat
        sB = new Station("B", 39, -9, "PT", "WET", "WET/GMT", true, true, false); // Esquerda
        sC = new Station("C", 41, -7, "PT", "WET", "WET/GMT", true, true, false); // Direita
    }

    @Test
    void testBuildEmptyTree() {
        tree.build(new ArrayList<>(), new ArrayList<>());
        assertNull(tree.getRoot());
        assertEquals(0, tree.size());
        assertEquals(0, tree.height());
        assertTrue(tree.getDistinctBucketSizes().isEmpty());
    }

    @Test
    void testBuildBalancedTreeSimple() {
        // Listas pré-ordenadas como viriam das AVLs da USEI06 [686]
        latSorted = List.of(sB, sA, sC); // Ordenado por Lat: 39, 40, 41
        lonSorted = List.of(sB, sA, sC); // Ordenado por Lon: -9, -8, -7

        tree.build(latSorted, lonSorted);

        assertEquals(3, tree.size());
        assertEquals(2, tree.height()); // Árvore equilibrada de 3 nós

        // Raiz (Mediana Lat)
        Node2D root = tree.getRoot();
        assertNotNull(root);
        assertEquals(sA, root.getStations().get(0));
        assertEquals(0, root.getAxis()); // Eixo 0 (Lat)

        // Filhos (Eixo 1 - Lon)
        assertNotNull(root.getLeft());
        assertEquals(sB, root.getLeft().getStations().get(0));
        assertEquals(1, root.getLeft().getAxis());

        assertNotNull(root.getRight());
        assertEquals(sC, root.getRight().getStations().get(0));
        assertEquals(1, root.getRight().getAxis());
    }

    @Test
    void testBuildWithDuplicateCoordinates() {
        latSorted = List.of(faro, lisbonOriente, lisbonSantaApolonia, porto); // Ordenado por Lat
        lonSorted = List.of(porto, lisbonOriente, lisbonSantaApolonia, faro); // Ordenado por Lon

        tree.build(latSorted, lonSorted);

        // A árvore deve ter 3 NÓS (Lisboa é 1 nó com 2 estações)
        assertEquals(3, tree.size());
        assertEquals(2, tree.height()); // Equilibrada

        // Mediana de Lat é uma das de Lisboa
        Node2D root = tree.getRoot();
        assertNotNull(root);
        assertTrue(root.getStations().stream().anyMatch(s -> s.getName().equals("Lisbon Oriente")));
        assertTrue(root.getStations().stream().anyMatch(s -> s.getName().equals("Lisbon Santa Apolonia")));

        // Verifica o "bucket" da raiz
        assertEquals(2, root.getBucketSize());

        // Verifica a ordem por nome no bucket [687]
        assertEquals("Lisbon Oriente", root.getStations().get(0).getName());
        assertEquals("Lisbon Santa Apolonia", root.getStations().get(1).getName());

        // Verifica os filhos
        assertEquals(faro, root.getLeft().getStations().get(0));
        assertEquals(porto, root.getRight().getStations().get(0));

        // Verifica os "Returns" [689]
        assertEquals(Set.of(1, 2), tree.getDistinctBucketSizes());
    }
}