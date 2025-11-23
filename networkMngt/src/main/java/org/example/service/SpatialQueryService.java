package org.example.service;

import org.example.domain.Station;
import org.example.queries.BoundingBoxQuery;
import org.example.trees.Node2D;
import org.example.trees.StationIndexes;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por executar pesquisas espaciais (USEI08), usando a KD-tree construída previamente (USEI07).
 *
 * Recebe os valores da área e filtros, cria uma BoundingBoxQuery e inicia a pesquisa recursiva pela árvore.
 */
public class SpatialQueryService {

    private final StationIndexes indexes;  // contém a KD-tree criada automaticamente na inicialização

    public SpatialQueryService(StationIndexes indexes) {
        this.indexes = indexes;
    }

    /**
     * Executa a pesquisa de estações numa área geográfica definida pelos limites de latitude/longitude, aplicando os filtros opcionais.
     *
     * 1. Cria uma BoundingBoxQuery com a área e filtros
     * 2. Obtém a raiz da KD-tree construída pela USEI07
     * 3. Chama a pesquisa rangeSearch() recursiva
     * 4. Devolve a lista de estações válidas
     */
    public List<Station> queryArea(double minLat, double maxLat, double minLon, double maxLon, Boolean isCity, Boolean isMain, String country) {

        // Objeto que guarda a área e filtros da query
        BoundingBoxQuery query = new BoundingBoxQuery(minLat, maxLat, minLon, maxLon, isCity, isMain, country);

        List<Station> results = new ArrayList<>();

        // Raiz da KD-tree criada automaticamente pela USEI07
        Node2D root = indexes.getSpatialIndex().getRoot();

        // Início da pesquisa
        rangeSearch(root, query, results);
        return results;
    }

    /**
     * Pesquisa recursiva na KD-tree.
     *
     * Passos:
     * 1. Se o nó for null → termina
     * 2. Vai buscar o pivô do nó (primeira estação do bucket)
     * 3. Verifica se o pivô está dentro da bounding box
     * 4. Se estiver, aplica filtros às estações do bucket (matches)
     * 5. Decide que ramos visitar com base no axis:
     *      - axis 0 → cortar por latitude
     *      - axis 1 → cortar por longitude
     *    Isto permite pruning de KD-tree.
     */
    private void rangeSearch(Node2D node, BoundingBoxQuery query, List<Station> out) {
        if (node == null) return; // caso base da recursão

        // Pivô: primeira estação deste nó
        Station pivot = node.getStations().get(0);
        double lat = pivot.getLatitude();
        double lon = pivot.getLongitude();

        // Verificação rápida se o pivô está dentro da área da query
        boolean insideLat = (lat >= query.getMinLat() && lat <= query.getMaxLat());
        boolean insideLon = (lon >= query.getMinLon() && lon <= query.getMaxLon());

        // Se o pivô estiver dentro da área, aplicar filtros às estações deste nó
        if (insideLat && insideLon) {
            for (Station s : node.getStations()) {
                if (query.matches(s)) out.add(s); // só entra se passar filtros
            }
        }

        int axis = node.getAxis();  // 0 = latitude, 1 = longitude

        // Decisão dos ramos a visitar usando pruning da KD-tree
        if (axis == 0) { // divisão por latitude
            if (query.getMinLat() <= lat) {
                rangeSearch(node.getLeft(), query, out);   // ramo esquerdo ainda pode ter estações válidas
            }
            if (query.getMaxLat() >= lat) {
                rangeSearch(node.getRight(), query, out);  // ramo direito ainda pode ter estações válidas
            }

        } else { // axis == 1 → divisão por longitude
            if (query.getMinLon() <= lon) {
                rangeSearch(node.getLeft(), query, out);
            }
            if (query.getMaxLon() >= lon) {
                rangeSearch(node.getRight(), query, out);
            }
        }
    }
}