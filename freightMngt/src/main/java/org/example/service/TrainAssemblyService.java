package org.example.service;

import org.example.domain.RollingStockStatus;
import org.example.repository.IRouteRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Serviço para montagem de trains (USLP09).
 */
public class TrainAssemblyService {

    private final IRouteRepository repository;

    public TrainAssemblyService(IRouteRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtém locomotives disponíveis, ordenadas por:
     * - PARKED primeiro (por distância crescente)
     * - IN_TRANSIT depois
     */
    public List<RollingStockItem> getAvailableLocomotives(int startStationId) {
        List<RollingStockItem> items = repository.getAvailableLocomotives(startStationId);

        // Ordenar: PARKED (por distância) -> IN_TRANSIT
        items.sort(Comparator
                .comparing(RollingStockItem::isInTransit)  // false (parked) primeiro
                .thenComparing(RollingStockItem::getDistanceFromStart));

        return items;
    }

    /**
     * Obtém wagons disponíveis, ordenados da mesma forma.
     */
    public List<RollingStockItem> getAvailableWagons(int startStationId) {
        List<RollingStockItem> items = repository.getAvailableWagons(startStationId);

        items.sort(Comparator
                .comparing(RollingStockItem::isInTransit)
                .thenComparing(RollingStockItem::getDistanceFromStart));

        return items;
    }

    /**
     * Associa rolling stock ao train.
     */
    public boolean assignRollingStock(int trainId, List<Integer> locoIds,
                                      List<Integer> wagonIds) {
        try {
            return repository.assignTrainRollingStock(trainId, locoIds, wagonIds);
        } catch (Exception e) {
            System.out.println("(!) Erro ao associar rolling stock: " + e.getMessage());
            return false;
        }
    }
}
