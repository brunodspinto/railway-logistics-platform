package org.example.repository;

import org.example.domain.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface IRouteRepository {
    // ===== INFRAESTRUTURA (Station, Line, etc.) =====
    Line findDirectLine(int originId, int destinationId);
    Station getStation(int id);
    Locomotive getLocomotive(int number);

    Collection<Line> getAllLines();
    Collection<Locomotive> getAllLocomotives();
    Collection<Station> getAllStations();

    // ===== RECURSOS MÓVEIS (Wagon, Train) =====
    WagonModel getWagonModel(int id);
    Wagon getWagon(String number);

    Collection<WagonModel> getAllWagonModels();
    Collection<Wagon> getAllWagons();

    Train getTrain(int id);
    Collection<Train> getAllTrains();
    List<Train> getTrainsByDate(LocalDate date);

    // ===== CARGAS (Freights) =====
    Freight getFreight(int id);
    Collection<Freight> getAllFreights();

    /**
     * Metodo essencial para substituir o getMockFreights() na UI.
     * Deve retornar apenas as cargas que ainda nao foram entregues/processadas.
     */
    List<Freight> getAllPendingFreights();

    // ===== AUXILIARES (Segments) =====
    List<LineSegment> getSegmentsByLine(int lineId);
    Line getLineById(int lineId);
}