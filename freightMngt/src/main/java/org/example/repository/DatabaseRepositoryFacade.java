package org.example.repository;

import org.example.domain.*;
import org.example.repository.database.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Cruza os dados dos repositórios para encontrar cargas não atribuídas.
     */
    @Override
    public List<Freight> getAllPendingFreights() {
        // 1. Obter todos os comboios para ver que cargas já estão ocupadas
        Collection<Train> allTrains = trainRepo.getAll();

        // 2. Criar um conjunto (Set) com os IDs das cargas já agendadas para busca rápida
        Set<Integer> assignedFreightIds = new HashSet<>();
        for (Train t : allTrains) {
            assignedFreightIds.addAll(t.getFreightIds());
        }

        // 3. Obter todas as cargas e filtrar as que NÃO estão no conjunto acima
        return freightRepo.getAll().stream()
                .filter(f -> !assignedFreightIds.contains(f.getId()))
                .collect(Collectors.toList());
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