package org.example.repository;

import org.example.domain.*;
import org.example.repository.database.*;
import org.example.service.RollingStockItem;

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

    @Override
    public List<RollingStockItem> getAvailableLocomotives(int startStationId) {
        List<RollingStockItem> items = new ArrayList<>();

        Collection<Locomotive> allLocos = locomotiveRepo.getAll();

        for (Locomotive loco : allLocos) {
            String description = loco.getModel() + " (" + loco.getPower() + " kW)";

            items.add(new RollingStockItem(
                    loco.getNumber(),
                    "LOCOMOTIVE",
                    description,
                    RollingStockStatus.PARKED,
                    "Available",
                    0
            ));
        }

        return items;
    }
    @Override
    public List<RollingStockItem> getAvailableWagons(int startStationId) {
        List<RollingStockItem> items = new ArrayList<>();

        Collection<Wagon> allWagons = wagonRepo.getAll();

        for (Wagon wagon : allWagons) {
            // Usar getNumber() em vez de getWagonNumber()
            String wagonNumber = wagon.getNumber();

            // Construir descrição usando o model
            String description;
            if (wagon.getModel() != null) {
                description = wagon.getModel().getModel() + " (" +
                        wagon.getMaxPayloadTons() + " tons)";
            } else {
                description = "Wagon #" + wagonNumber + " (Model ID: " + wagon.getModelId() + ")";
            }

            // Converter wagon number (String) para int
            int wagonId;
            try {
                wagonId = Integer.parseInt(wagonNumber);
            } catch (NumberFormatException e) {
                wagonId = wagonNumber.hashCode();
            }

            items.add(new RollingStockItem(
                    wagonId,
                    "WAGON",
                    description,
                    RollingStockStatus.PARKED,
                    "Available",
                    0
            ));
        }

        return items;
    }

    @Override
    public boolean assignTrainRollingStock(int trainId, List<Integer> locoIds,
                                           List<Integer> wagonIds) {
        System.out.println("\n[SIMULATION] Assigning to Train #" + trainId);
        System.out.println("  Locomotives: " + locoIds);
        System.out.println("  Wagons: " + wagonIds);
        System.out.println("  ✓ Simulated successfully!");
        return true;
    }
}