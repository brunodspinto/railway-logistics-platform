package org.example.repository;

import org.example.domain.*;
import org.example.repository.database.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseRepositoryFacade implements IRouteRepository {

    private final StationRepository stationRepo;
    private final LocomotiveRepository locomotiveRepo;
    private final WagonRepository wagonRepo;
    private final LineRepository lineRepo;
    private final FreightRepository freightRepo;
    private final TrainRepository trainRepo;

    public DatabaseRepositoryFacade() {
        this.stationRepo = new StationRepository();
        this.locomotiveRepo = new LocomotiveRepository();
        this.wagonRepo = new WagonRepository();
        this.lineRepo = new LineRepository();
        this.freightRepo = new FreightRepository();
        this.trainRepo = new TrainRepository();
    }

    // Stations
    @Override
    public Station getStation(int id) {
        return stationRepo.getById(id);
    }

    @Override
    public Collection<Station> getAllStations() {
        return stationRepo.getAll();
    }

    // Locomotives
    @Override
    public Locomotive getLocomotive(int number) {
        return locomotiveRepo.getById(number);
    }

    @Override
    public Collection<Locomotive> getAllLocomotives() {
        return locomotiveRepo.getAll();
    }

    // Wagons
    @Override
    public Wagon getWagon(String number) {
        return wagonRepo.getById(number);
    }

    @Override
    public Collection<Wagon> getAllWagons() {
        return wagonRepo.getAll();
    }

    @Override
    public WagonModel getWagonModel(int id) {
        return wagonRepo.getModelById(id);
    }

    @Override
    public Collection<WagonModel> getAllWagonModels() {
        return wagonRepo.getAllModels();
    }

    // Lines
    @Override
    public Line getLineById(int lineId) {
        return lineRepo.getById(lineId);
    }

    @Override
    public Line findDirectLine(int originId, int destinationId) {
        return lineRepo.findDirectLine(originId, destinationId);
    }

    @Override
    public Collection<Line> getAllLines() {
        return lineRepo.getAll();
    }

    @Override
    public List<LineSegment> getSegmentsByLine(int lineId) {
        return lineRepo.getSegmentsByLineId(lineId);
    }

    // Freights
    @Override
    public Freight getFreight(int id) {
        return freightRepo.getById(id);
    }

    @Override
    public Collection<Freight> getAllFreights() {
        return freightRepo.getAll();
    }

    /**
     * * Para efeitos de planeamento de rota (USLP08), retornamos TODAS as cargas
     * disponíveis na BD, ignorando se já estão "tecnicamente" atribuídas,
     * permitindo assim ao utilizador planear rotas para os dados de teste existentes.
     */
    @Override
    public List<Freight> getAllPendingFreights() {
        Collection<Freight> allFreights = freightRepo.getAll();

        if (allFreights instanceof List) {
            return (List<Freight>) allFreights;
        }

        return new ArrayList<>(allFreights);
    }

    // Trains
    @Override
    public Train getTrain(int id) {
        return trainRepo.getById(id);
    }

    @Override
    public Collection<Train> getAllTrains() {
        return trainRepo.getAll();
    }

    @Override
    public List<Train> getTrainsByDate(LocalDate date) {
        return trainRepo.getByDate(date);
    }
}