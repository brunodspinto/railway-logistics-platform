package org.example.repository;


import org.example.domain.*;
import java.util.Collection;

public interface IRouteRepository {
    Line findDirectLine(int originId, int destinationId);
    Station getStation(int id);
    Locomotive getLocomotive(int number);

    Collection<Line> getAllLines();
    Collection<Locomotive> getAllLocomotives();
    Collection<Station> getAllStations();
}
