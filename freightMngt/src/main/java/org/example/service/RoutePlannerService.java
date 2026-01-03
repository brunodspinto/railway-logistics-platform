package org.example.service;

import org.example.domain.Freight;
import org.example.domain.Line;
import org.example.domain.Station;
import org.example.repository.IRouteRepository;

import java.util.*;

/**
 * Serviço responsável por planear rotas.
 * - USLP08: Planeamento Logístico (Carga/Descarga em rota pré-definida).
 * - USLP10: Planeamento Automático (Cálculo do melhor caminho via Dijkstra).
 */
public class RoutePlannerService {

    private IRouteRepository repository;

    /**
     * Construtor padrão (Mantido para compatibilidade com USLP08 se não houver acesso a BD).
     * Nota: O cálculo automático (USLP10) não funcionará se usar este construtor.
     */
    public RoutePlannerService() {
    }

    /**
     * Construtor para USLP10 (Requer acesso aos dados da rede).
     * @param repository Repositório para aceder a linhas e estações.
     */
    public RoutePlannerService(IRouteRepository repository) {
        this.repository = repository;
    }

    // ==================================================================================
    //  USLP08 - LOGISTICS PLANNING (Carga/Descarga) - CÓDIGO ORIGINAL MANTIDO
    // ==================================================================================

    /**
     * Cria um plano de rota detalhado com base num caminho e numa lista de cargas.
     *
     * @param path     A sequência de estações (rota definida manual ou automaticamente).
     * @param freights A lista de cargas (Freights) que se pretende transportar.
     * @return Um RoutePlan contendo as operações a realizar em cada estação.
     */
    public RoutePlan planRoute(List<Station> path, List<Freight> freights) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("The path cannot be empty.");
        }
        if (freights == null || freights.isEmpty()) {
            throw new IllegalArgumentException("The freight list cannot be empty.");
        }

        // Usa o construtor básico do RoutePlan para logística
        RoutePlan plan = new RoutePlan();

        // Iterar por cada estação do caminho para determinar as operações
        for (Station currentStation : path) {
            RouteStop stop = new RouteStop(currentStation);

            for (Freight freight : freights) {
                // Verificar se esta estação é a origem da carga (Carregar)
                if (freight.getOriginId() == currentStation.getId()) {
                    stop.addFreightToLoad(freight);
                }

                // Verificar se esta estação é o destino da carga (Descarregar)
                if (freight.getDestinationId() == currentStation.getId()) {
                    stop.addFreightToUnload(freight);
                }
            }

            // Adiciona a paragem ao plano
            plan.addStop(stop);
        }

        validatePlan(plan, freights);

        return plan;
    }

    /**
     * Validação simples para garantir que todas as cargas são entregues.
     */
    private void validatePlan(RoutePlan plan, List<Freight> freights) {
        for (Freight f : freights) {
            boolean loaded = false;
            boolean unloaded = false;

            for (RouteStop stop : plan.getStops()) {
                if (stop.getFreightsToLoad().contains(f)) loaded = true;
                if (stop.getFreightsToUnload().contains(f)) unloaded = true;
            }

            if (!loaded) {
                System.out.printf("[WARNING] Freight #%d (Origin: %s) is not being LOADED in this route path.\n",
                        f.getId(), f.getOriginName());
            }
            if (!unloaded) {
                System.out.printf("[WARNING] Freight #%d (Dest: %s) is not being UNLOADED in this route path.\n",
                        f.getId(), f.getDestinationName());
            }
        }
    }

    // ==================================================================================
    //  USLP10 - AUTOMATIC PATH CALCULATION (Dijkstra) - NOVO CÓDIGO
    // ==================================================================================

    /**
     * Calcula a melhor rota entre duas estações usando o algoritmo de Dijkstra.
     *
     * @param start    Estação de partida.
     * @param end      Estação de chegada.
     * @param priority Critério de prioridade (Distância, Tempo, etc.).
     * @return Um RoutePlan contendo o caminho calculado e a distância total.
     */
    public RoutePlan planRoute(Station start, Station end, RoutePriority priority) {
        if (repository == null) {
            throw new IllegalStateException("Repository not initialized. Use the constructor with IRouteRepository for automatic planning.");
        }
        if (start.getId() == end.getId()) return null;

        // 1. Obter todas as estações (nós do grafo)
        Collection<Station> allStations = repository.getAllStations();

        // 2. Construir Grafo em memória (Lista de Adjacências)
        Map<Integer, List<Edge>> graph = buildGraph(priority);

        // 3. Estruturas para Dijkstra
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));

        // Inicializar distâncias
        for (Station s : allStations) {
            dist.put(s.getId(), Double.MAX_VALUE);
        }
        dist.put(start.getId(), 0.0);
        pq.add(new Node(start.getId(), 0.0));

        // 4. Executar Algoritmo
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.id;

            // Otimização: Se chegámos ao destino, parar.
            if (u == end.getId()) break;

            // Se encontrámos um caminho pior, ignorar
            if (current.cost > dist.get(u)) continue;

            if (graph.containsKey(u)) {
                for (Edge edge : graph.get(u)) {
                    int v = edge.target;
                    double weight = edge.weight;
                    double newDist = dist.get(u) + weight;

                    if (newDist < dist.get(v)) {
                        dist.put(v, newDist);
                        prev.put(v, u);
                        pq.add(new Node(v, newDist));
                    }
                }
            }
        }

        // 5. Reconstruir o Caminho
        if (!prev.containsKey(end.getId())) {
            return null; // Caminho não encontrado (grafo desconexo)
        }

        List<Station> path = new ArrayList<>();
        int curr = end.getId();
        while (curr != start.getId()) {
            path.add(repository.getStation(curr));
            curr = prev.get(curr);
        }
        path.add(start);
        Collections.reverse(path);

        // Retorna o RoutePlan usando o construtor compatível com Estações
        return new RoutePlan(path, dist.get(end.getId()));
    }

    /**
     * Constrói o grafo baseado nas linhas do repositório.
     */
    /**
     * Constrói o grafo baseado nas linhas do repositório.
     */
    private Map<Integer, List<Edge>> buildGraph(RoutePriority priority) {
        Map<Integer, List<Edge>> graph = new HashMap<>();
        Collection<Line> lines = repository.getAllLines();

        for (Line line : lines) {
            // VERIFICAÇÃO DE SEGURANÇA
            // Garante que a linha foi bem carregada e tem estações associadas
            if (line.getStartStation() == null || line.getEndStation() == null) {
                System.err.println("Aviso: Linha " + line.getId() + " ignorada (Estações em falta).");
                continue;
            }

            // CORREÇÃO AQUI: Em vez de line.getStartStationId()...
            // ...usamos line.getStartStation().getId()
            int u = line.getStartStation().getId();
            int v = line.getEndStation().getId();

            double weight = calculateWeight(line, priority);

            // Grafo não-direcionado (Ida e Volta)
            graph.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(v, weight));
            graph.computeIfAbsent(v, k -> new ArrayList<>()).add(new Edge(u, weight));
        }
        return graph;
    }

    /**
     * Define o peso da aresta com base na prioridade.
     */
    private double calculateWeight(Line line, RoutePriority priority) {
        switch (priority) {
            case TIME:
                // Peso = Distância / Velocidade Máxima (menor tempo é melhor)
                // Evitar divisão por zero
                int speed = line.getMinMaxSpeed() > 0 ? line.getMinMaxSpeed() : 1;
                return line.getTotalLengthKm() / (double) speed;
            case DISTANCE:
            default:
                // Peso = Distância em Km
                return line.getTotalLengthKm();
        }
    }

    // --- Classes Auxiliares Privadas para Dijkstra ---

    private static class Edge {
        int target;
        double weight;
        public Edge(int target, double weight) { this.target = target; this.weight = weight; }
    }

    private static class Node {
        int id;
        double cost;
        public Node(int id, double cost) { this.id = id; this.cost = cost; }
    }
}