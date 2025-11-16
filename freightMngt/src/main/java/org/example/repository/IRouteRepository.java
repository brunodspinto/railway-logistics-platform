package org.example.repository;

import org.example.domain.*;
import java.util.Collection;
import java.util.List;

public interface IRouteRepository {
    // ===== EXISTENTE (manter) =====
    Line findDirectLine(int originId, int destinationId);
    Station getStation(int id);
    Locomotive getLocomotive(int number);
    Collection<Line> getAllLines();
    Collection<Locomotive> getAllLocomotives();
    Collection<Station> getAllStations();

    // ===== NOVO - Para USLP07 =====

    // Wagon & WagonModel
    WagonModel getWagonModel(int id);
    Wagon getWagon(String number);
    Collection<WagonModel> getAllWagonModels();
    Collection<Wagon> getAllWagons();

    // Freight
    Freight getFreight(int id);
    Collection<Freight> getAllFreights();

    // Train
    Train getTrain(int id);
    Collection<Train> getAllTrains();

    // Line Segments (útil para path building)
    List<LineSegment> getSegmentsByLine(int lineId);
    Line getLineById(int lineId);
}