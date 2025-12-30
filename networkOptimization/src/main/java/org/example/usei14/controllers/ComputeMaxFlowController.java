package org.example.usei14.controllers;

import org.example.algorithms.EdmondsKarp;
import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.loader.BelgianNetworkLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controlador responsável pelo cálculo do Fluxo Máximo na rede.
 * Interage com o grafo e utiliza o algoritmo de Edmonds-Karp.
 */
public class ComputeMaxFlowController {

    private Graph<Station, Connection> network;
    private final EdmondsKarp<Station, Connection> maxFlowAlgorithm;

    public ComputeMaxFlowController() {
        this.maxFlowAlgorithm = new EdmondsKarp<>();
    }

    /**
     * Carrega a rede ferroviária a partir dos ficheiros CSV especificados.
     *
     * @param stationsPath Caminho para o ficheiro de estações.
     * @param linesPath    Caminho para o ficheiro de linhas/conexões.
     * @throws IOException Se houver erro na leitura dos ficheiros.
     */
    public void loadNetwork(String stationsPath, String linesPath) throws IOException {
        this.network = BelgianNetworkLoader.loadNetwork(stationsPath, linesPath, true);
    }

    /**
     * Retorna a lista de todas as estações disponíveis, ordenadas alfabeticamente pelo nome.
     *
     * @return Lista de estações ou lista vazia se a rede for nula.
     */
    public List<Station> getStations() {
        if (network == null) {
            return new ArrayList<>();
        }
        List<Station> stations = network.vertices();
        stations.sort(Comparator.comparing(Station::getName));
        return stations;
    }

    /**
     * Calcula o fluxo máximo entre duas estações e mede o desempenho da operação.
     *
     * @param source Estação de origem.
     * @param sink   Estação de destino.
     * @return O valor do fluxo máximo calculado.
     */
    public Double calculateMaxFlow(Station source, Station sink) {
        if (network == null) {
            throw new IllegalStateException("The network has not been loaded.");
        }

        System.out.println("Running the Edmonds-Karp algorithm...");
        long startTime = System.nanoTime();

        // Execução do algoritmo
        double maxFlow = maxFlowAlgorithm.computeMaxFlow(network, source, sink);

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1e6;

        System.out.printf("[Performance] Time: %.4f ms | Complexity: O(V * E^2)%n", durationMs);

        return maxFlow;
    }
}