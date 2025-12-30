package org.example.algorithms;

import org.example.domain.Connection;
import org.example.graph.Edge;
import org.example.graph.Graph;

import java.util.*;

/**
 * Implementação do algoritmo de Edmonds-Karp para o cálculo de Fluxo Máximo.
 * Esta classe utiliza uma pesquisa em largura (BFS) para encontrar os caminhos de aumento
 * mais curtos no grafo residual, garantindo que o algoritmo termina e encontra o fluxo máximo
 * para capacidades racionais.
 *
 * @param <V> O tipo de dados dos vértices do grafo.
 * @param <E> O tipo de dados das arestas.
 */
public class EdmondsKarp<V, E> {

    /**
     * Calcula o fluxo máximo possível entre um vértice de origem e um vértice de destino num grafo de conexões.
     *
     * @param graph  O grafo onde o cálculo será realizado. Deve conter as arestas com as respetivas capacidades.
     * @param source O vértice de origem do fluxo.
     * @param sink   O vértice de destino do fluxo.
     * @return O valor numérico do fluxo máximo calculado entre a origem e o destino.
     * @throws IllegalArgumentException Se o grafo ou vértices forem nulos, se os vértices não existirem no grafo
     * ou se forem detetadas capacidades negativas nas arestas.
     */
    public double computeMaxFlow(Graph<V, Connection> graph, V source, V sink) {
        // Validações
        if (graph == null || source == null || sink == null)
            throw new IllegalArgumentException("Graph and vertices cannot be null.");
        if (!graph.validVertex(source) || !graph.validVertex(sink))
            throw new IllegalArgumentException("Source and Sink must exist in the graph.");
        if (source.equals(sink)) return 0.0;

        // Construir Grafo Residual
        // O grafo residual armazena as capacidades disponíveis (diretas e inversas)
        Map<V, Map<V, Double>> residualGraph = new HashMap<>();

        for (V v : graph.vertices()) {
            residualGraph.put(v, new HashMap<>());
        }

        for (Edge<V, Connection> edge : graph.edges()) {
            V u = edge.getVOrig();
            V v = edge.getVDest();

            double capacity = edge.getWeight().getCapacity();
            if (capacity < 0) {
                throw new IllegalArgumentException("Negative capacity detected: " + edge);
            }

            // Combinar capacidades para multigrafos (arestas paralelas são somadas)
            residualGraph.get(u).merge(v, capacity, Double::sum);

            // Inicializar aresta inversa com 0 se estiver ausente (para permitir o fluxo reverso)
            residualGraph.get(v).putIfAbsent(u, 0.0);
        }

        double maxFlow = 0.0;
        Map<V, V> parent = new HashMap<>();

        // Ciclo Principal: Executa BFS enquanto existirem caminhos de aumento no grafo residual
        while (bfs(residualGraph, source, sink, parent)) {

            double pathFlow = Double.MAX_VALUE;
            V v = sink;

            // Encontrar o gargalo (bottleneck capacity) ao longo do caminho encontrado pelo BFS
            while (!v.equals(source)) {
                V u = parent.get(v);
                double capacity = residualGraph.get(u).get(v);
                pathFlow = Math.min(pathFlow, capacity);
                v = u;
            }

            // Atualizar as capacidades residuais e as arestas inversas ao longo do caminho
            v = sink;
            while (!v.equals(source)) {
                V u = parent.get(v);

                // Subtrair fluxo na aresta direta
                residualGraph.get(u).put(v, residualGraph.get(u).get(v) - pathFlow);

                // Adicionar fluxo na aresta inversa (permitindo o cancelamento de fluxo futuro)
                residualGraph.get(v).put(u, residualGraph.get(v).get(u) + pathFlow);

                v = u;
            }

            // Adicionar o fluxo do caminho ao fluxo total
            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    /**
     * Realiza uma Pesquisa em Largura (BFS) no grafo residual para encontrar um caminho
     * da origem até ao destino onde todas as arestas tenham capacidade residual positiva.
     *
     * @param residualGraph O mapa que representa a estrutura e capacidades atuais do grafo residual.
     * @param source        O vértice de início da pesquisa.
     * @param sink          O vértice alvo da pesquisa.
     * @param parent        Um mapa para armazenar a árvore de predecessores, permitindo reconstruir o caminho.
     * @return {@code true} se for encontrado um caminho acessível até ao destino, {@code false} caso contrário.
     */
    private boolean bfs(Map<V, Map<V, Double>> residualGraph, V source, V sink, Map<V, V> parent) {
        Queue<V> queue = new LinkedList<>();
        Set<V> visited = new HashSet<>();

        queue.add(source);
        visited.add(source);
        parent.clear();

        while (!queue.isEmpty()) {
            V u = queue.poll();

            Map<V, Double> neighbors = residualGraph.get(u);
            if (neighbors == null) continue;

            for (Map.Entry<V, Double> entry : neighbors.entrySet()) {
                V v = entry.getKey();
                Double cap = entry.getValue();

                // Visitar apenas se não foi visitado e se houver capacidade residual positiva
                if (!visited.contains(v) && cap > 0) {
                    visited.add(v);
                    parent.put(v, u);
                    queue.add(v);

                    // Se atingimos o destino, o caminho foi encontrado
                    if (v.equals(sink)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}