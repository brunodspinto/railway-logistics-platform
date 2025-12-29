package org.example.service;

import org.example.domain.Freight;
import org.example.domain.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa uma paragem na rota com as respetivas operações logísticas.
 */
public class RouteStop {
    private final Station station;
    private final List<Freight> freightsToLoad;
    private final List<Freight> freightsToUnload;

    public RouteStop(Station station) {
        this.station = station;
        this.freightsToLoad = new ArrayList<>();
        this.freightsToUnload = new ArrayList<>();
    }

    public void addFreightToLoad(Freight freight) {
        this.freightsToLoad.add(freight);
    }

    public void addFreightToUnload(Freight freight) {
        this.freightsToUnload.add(freight);
    }

    public boolean hasOperations() {
        return !freightsToLoad.isEmpty() || !freightsToUnload.isEmpty();
    }

    public Station getStation() {
        return station;
    }

    public List<Freight> getFreightsToLoad() {
        return Collections.unmodifiableList(freightsToLoad);
    }

    public List<Freight> getFreightsToUnload() {
        return Collections.unmodifiableList(freightsToUnload);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Station: %s (ID: %d)", station.getName(), station.getId()));

        if (!freightsToLoad.isEmpty()) {
            sb.append("\n    [LOAD] ");
            for (Freight f : freightsToLoad) {
                sb.append(String.format("Freight #%d (%s->%s) ", f.getId(), f.getOriginName(), f.getDestinationName()));
            }
        }

        if (!freightsToUnload.isEmpty()) {
            sb.append("\n    [UNLOAD] ");
            for (Freight f : freightsToUnload) {
                sb.append(String.format("Freight #%d ", f.getId()));
            }
        }

        if (freightsToLoad.isEmpty() && freightsToUnload.isEmpty()) {
            sb.append(" - Passagem (Sem operações)");
        }

        return sb.toString();
    }
}