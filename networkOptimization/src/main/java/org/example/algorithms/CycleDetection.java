package org.example.algorithms;

import org.example.graph.Graph;

import java.util.*;

/**
 * Deteta ciclos em grafos dirigidos usando Colored DFS
 * Complexidade: O(V + E)
 */
public class CycleDetection<V, E> {

    private enum Color {
        WHITE,  // Não visitado
        GRAY,   // Em processamento
        BLACK   // Concluído
    }

    /**
     * Deteta ciclos no grafo
     *
     * @param graph Grafo dirigido
     * @return Resultado com ciclos encontrados (se existirem)
     */
    public CycleDetectionResult<V> detectCycles(Graph<V, E> graph) {
        Map<V, Color> color = new HashMap<>();
        List<List<V>> cycles = new ArrayList<>();

        // Inicializar todos como WHITE
        for (V vertex : graph.vertices()) {
            color.put(vertex, Color.WHITE);
        }

        // DFS a partir de cada vértice não visitado
        for (V vertex : graph.vertices()) {
            if (color.get(vertex) == Color.WHITE) {
                LinkedList<V> path = new LinkedList<>();
                if (coloredDFS(graph, vertex, color, path, cycles)) {
                    // Ciclo encontrado
                }
            }
        }

        return new CycleDetectionResult<>(cycles);
    }

    /**
     * DFS recursivo com deteção de ciclos
     */
    private boolean coloredDFS(Graph<V, E> graph,
                               V current,
                               Map<V, Color> color,
                               LinkedList<V> path,
                               List<List<V>> cycles) {

        // Marcar como GRAY (em processamento)
        color.put(current, Color.GRAY);
        path.addLast(current);

        boolean foundCycle = false;

        // Explorar adjacentes
        for (V adjacent : graph.adjVertices(current)) {

            if (color.get(adjacent) == Color.GRAY) {
                // CICLO ENCONTRADO!
                extractCycle(path, adjacent, cycles);
                foundCycle = true;
                // Continuar para encontrar todos os ciclos

            } else if (color.get(adjacent) == Color.WHITE) {
                if (coloredDFS(graph, adjacent, color, path, cycles)) {
                    foundCycle = true;
                }
            }
        }

        // Marcar como BLACK (concluído)
        color.put(current, Color.BLACK);
        path.removeLast();

        return foundCycle;
    }

    /**
     * Extrai o ciclo do caminho atual
     */
    private void extractCycle(LinkedList<V> path, V cycleStart, List<List<V>> cycles) {
        List<V> cycle = new ArrayList<>();
        boolean recording = false;

        for (V vertex : path) {
            if (vertex.equals(cycleStart)) {
                recording = true;
            }
            if (recording) {
                cycle.add(vertex);
            }
        }
        cycle.add(cycleStart); // Fechar o ciclo

        cycles.add(cycle);
    }

    /**
     * Resultado da detecção de ciclos
     */
    public static class CycleDetectionResult<V> {
        private final List<List<V>> cycles;

        public CycleDetectionResult(List<List<V>> cycles) {
            this.cycles = cycles;
        }

        public boolean hasCycles() {
            return !cycles.isEmpty();
        }

        public List<List<V>> getCycles() {
            return cycles;
        }

        public int getNumCycles() {
            return cycles.size();
        }
    }
}

