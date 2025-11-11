package org.example.trees;

import org.example.domain.Station;
import java.util.*;

public class TwoDTree {

    private Node2D root;
    private int nodeCount;

    public TwoDTree() {
        this.root = null;
        this.nodeCount = 0;
    }

    /**
     * Ponto de entrada público para construir a árvore.
     * Recebe duas listas: uma ordenada por latitude, outra por longitude.
     * Ambas devem conter o mesmo conjunto de N estações.
     * Esta é a otimização pedida na USEI07 .
     */
    public void build(List<Station> stationsSortedByLat, List<Station> stationsSortedByLon) {
        this.nodeCount = 0;
        // Começamos no nível 0 (axis=0, latitude)
        this.root = buildRecursive(stationsSortedByLat, stationsSortedByLon, 0);
    }

    /**
     * O algoritmo de construção O(N log N) recursivo.
     * * @param axisList Lista de estações ordenada pelo eixo ATUAL (axis).
     * @param otherList Mesmas estações, ordenadas pelo OUTRO eixo.
     * @param depth A profundidade atual da árvore.
     */
    private Node2D buildRecursive(List<Station> axisList, List<Station> otherList, int depth) {
        // 1. Caso Base
        if (axisList.isEmpty()) {
            return null;
        }

        // 2. Determinar o eixo de divisão (0=lat, 1=lon)
        int axis = depth % 2;

        // 3. Encontrar a Mediana (O(1) porque a axisList está ordenada)
        int medianIdx = axisList.size() / 2;
        Station medianStation = axisList.get(medianIdx);

        // 4. Criar o nó com a estação mediana
        Node2D node = new Node2D(medianStation, axis);
        this.nodeCount++;

        // --- 5. Lidar com Coordenadas Duplicadas (Bucket) ---
        // Precisamos encontrar o *intervalo* de todas as estações com as mesmas
        // coordenadas exatas (lat, lon) da mediana.

        Set<Station> duplicateBucket = new HashSet<>();
        duplicateBucket.add(medianStation);

        // Verifica para a esquerda da mediana
        int leftIdx = medianIdx - 1;
        while (leftIdx >= 0 &&
                axisList.get(leftIdx).getLatitude() == medianStation.getLatitude() &&
                axisList.get(leftIdx).getLongitude() == medianStation.getLongitude()) {
            node.addStation(axisList.get(leftIdx)); // Adiciona ao bucket (ordenado por nome)
            duplicateBucket.add(axisList.get(leftIdx));
            leftIdx--;
        }

        // Verifica para a direita da mediana
        int rightIdx = medianIdx + 1;
        while (rightIdx < axisList.size() &&
                axisList.get(rightIdx).getLatitude() == medianStation.getLatitude() &&
                axisList.get(rightIdx).getLongitude() == medianStation.getLongitude()) {
            node.addStation(axisList.get(rightIdx)); // Adiciona ao bucket (ordenado por nome)
            duplicateBucket.add(axisList.get(rightIdx));
            rightIdx++;
        }

        // --- 6. Particionar as listas (O(N)) ---

        // 6a. Particionar a axisList (fácil, é uma sub-lista)
        List<Station> leftAxisList = new ArrayList<>(axisList.subList(0, leftIdx + 1));
        List<Station> rightAxisList = new ArrayList<>(axisList.subList(rightIdx, axisList.size()));

        // 6b. Particionar a otherList (requer iteração O(N))
        List<Station> leftOtherList = new ArrayList<>(leftAxisList.size());
        List<Station> rightOtherList = new ArrayList<>(rightAxisList.size());

        double medianCoordinate = node.getSplitCoordinate();

        for (Station s : otherList) {
            // Ignora estações que estão no "bucket" da mediana
            if (duplicateBucket.contains(s)) {
                continue;
            }

            // Particiona com base *apenas* na coordenada do eixo atual
            double sCoord = Node2D.getCoordinate(s, axis);

            if (sCoord < medianCoordinate) {
                leftOtherList.add(s);
            } else {
                // Se sCoord >= medianCoordinate (e não é duplicado) vai para a direita
                rightOtherList.add(s);
            }
        }

        // 7. Chamar a recursão para os filhos
        // As listas são trocadas: a 'otherList' da esquerda torna-se a 'axisList' do filho
        node.setLeft(buildRecursive(leftOtherList, leftAxisList, depth + 1));
        node.setRight(buildRecursive(rightOtherList, rightAxisList, depth + 1));

        return node;
    }

    /**
     * Retorna o número de NÓS na árvore.
     */
    public int size() {
        return this.nodeCount;
    }

    /**
     * Retorna a altura da árvore.
     */
    public int height() {
        return heightRecursive(root);
    }

    private int heightRecursive(Node2D node) {
        if (node == null) return 0;
        return 1 + Math.max(heightRecursive(node.getLeft()), heightRecursive(node.getRight()));
    }

    /**
     * Retorna os tamanhos distintos dos "buckets" (listas de estações nos nós).
     */
    public Set<Integer> getDistinctBucketSizes() {
        Set<Integer> sizes = new HashSet<>();
        collectBucketSizes(root, sizes);
        return sizes;
    }

    private void collectBucketSizes(Node2D node, Set<Integer> sizes) {
        if (node == null) return;
        sizes.add(node.getBucketSize());
        collectBucketSizes(node.getLeft(), sizes);
        collectBucketSizes(node.getRight(), sizes);
    }

    public Node2D getRoot() {
        return root;
    }
}