package org.example.algorithms;

import org.example.graph.Graph;

import java.util.*;

/**
 * Ordenação topológica usando Kahn's Algorithm
 * Complexidade: O(V + E)
 */
public class TopologicalSort<V, E> {

    /**
     * Executa ordenação topológica
     *
     * @param graph Grafo dirigido acíclico (DAG)
     * @return Lista ordenada de vértices
     * @throws IllegalStateException Se o grafo tiver ciclos
     */
    public List<V> kahn(Graph<V, E> graph) {
        List<V> topologicalOrder = new ArrayList<>();
        Queue<V> queue = new LinkedList<>();
        Map<V, Integer> inDegree = new HashMap<>();

        // Calcular in-degree de cada vértice
        for (V vertex : graph.vertices()) {
            int degree = graph.inDegree(vertex);
            inDegree.put(vertex, degree);

            // Adicionar vértices com in-degree 0 à fila
            if (degree == 0) {
                queue.add(vertex);
            }
        }

        int processedVertices = 0;

        // Processar vértices
        while (!queue.isEmpty()) {
            V current = queue.poll();
            topologicalOrder.add(current);
            processedVertices++;

            // Decrementar in-degree dos adjacentes
            for (V adjacent : graph.adjVertices(current)) {
                int newInDegree = inDegree.get(adjacent) - 1;
                inDegree.put(adjacent, newInDegree);

                // Se in-degree chegar a 0, adicionar à fila
                if (newInDegree == 0) {
                    queue.add(adjacent);
                }
            }
        }

        // Verificar se processou todos os vértices
        if (processedVertices < graph.numVertices()) {
            throw new IllegalStateException(
                    "Graph has cycles! Cannot perform topological sort.");
        }

        return topologicalOrder;
    }
}

