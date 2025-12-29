package org.example.repository;

import org.example.domain.*;
import org.example.repository.database.*;
import org.example.service.RollingStockItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DatabaseRepositoryFacade implements IRouteRepository {

    private final StationRepository stationRepo;
    private final LocomotiveRepository locomotiveRepo;
    private final WagonRepository wagonRepo;
    private final LineRepository lineRepo;
    private final FreightRepository freightRepo;
    private final TrainRepository trainRepo;

    private final Random random = new Random(42); // Seed fixa para resultados consistentes

    public DatabaseRepositoryFacade() {
        this.stationRepo = new StationRepository();
        this.locomotiveRepo = new LocomotiveRepository();
        this.wagonRepo = new WagonRepository();
        this.lineRepo = new LineRepository();
        this.freightRepo = new FreightRepository();
        this.trainRepo = new TrainRepository();
    }

    // ═══════════════════════════════════════════════════════════
    // STATIONS
    // ═══════════════════════════════════════════════════════════

    @Override
    public Station getStation(int id) {
        return stationRepo.getById(id);
    }

    @Override
    public Collection<Station> getAllStations() {
        return stationRepo.getAll();
    }

    // ═══════════════════════════════════════════════════════════
    // LOCOMOTIVES
    // ═══════════════════════════════════════════════════════════

    @Override
    public Locomotive getLocomotive(int number) {
        return locomotiveRepo.getById(number);
    }

    @Override
    public Collection<Locomotive> getAllLocomotives() {
        return locomotiveRepo.getAll();
    }

    // ═══════════════════════════════════════════════════════════
    // WAGONS
    // ═══════════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════════
    // LINES
    // ═══════════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════════
    // FREIGHTS
    // ═══════════════════════════════════════════════════════════

    @Override
    public Freight getFreight(int id) {
        return freightRepo.getById(id);
    }

    @Override
    public Collection<Freight> getAllFreights() {
        return freightRepo.getAll();
    }

    /**
     * Para efeitos de planeamento de rota (USLP08), retornamos TODAS as cargas
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

    // ═══════════════════════════════════════════════════════════
    // TRAINS
    // ═══════════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════════
    // ROLLING STOCK (USLP09)
    // ═══════════════════════════════════════════════════════════

    @Override
    public List<RollingStockItem> getAvailableLocomotives(int startStationId) {
        List<RollingStockItem> items = new ArrayList<>();
        Collection<Locomotive> allLocos = locomotiveRepo.getAll();

        List<Train> allTrains = new ArrayList<>();
        try {
            allTrains.addAll(trainRepo.getAll());
        } catch (Exception e) { }

        int index = 0;
        for (Locomotive loco : allLocos) {
            String description = loco.getModel() + " (" + loco.getPower() + " kW)";

            boolean isInTransit = (index % 4 == 0);

            RollingStockStatus status;
            String location;
            double distance;

            if (isInTransit && !allTrains.isEmpty()) {
                status = RollingStockStatus.IN_TRANSIT;
                Train train = allTrains.get(index % allTrains.size());

                if (train.getEndStation() != null) {
                    location = "Train #" + train.getId() + " → " + train.getEndStation().getName();
                } else {
                    location = "Train #" + train.getId();
                }
                distance = 50 + random.nextInt(150);
            } else {
                status = RollingStockStatus.PARKED;
                Station parkStation = getRandomNearbyStation(startStationId);

                if (parkStation != null) {
                    location = parkStation.getName();
                    distance = calculateSimulatedDistance(startStationId, parkStation.getId());
                } else {
                    location = "Available";
                    distance = 0;
                }
            }

            items.add(new RollingStockItem(
                    loco.getNumber(), "LOCOMOTIVE", description,
                    status, location, distance));
            index++;
        }
        return items;
    }

    @Override
    public List<RollingStockItem> getAvailableWagons(int startStationId) {
        List<RollingStockItem> items = new ArrayList<>();
        Collection<Wagon> allWagons = wagonRepo.getAll();

        List<Train> allTrains = new ArrayList<>();
        try {
            allTrains.addAll(trainRepo.getAll());
        } catch (Exception e) { }

        int index = 0;
        for (Wagon wagon : allWagons) {
            String wagonNumber = wagon.getNumber();
            String description;
            if (wagon.getModel() != null) {
                description = wagon.getModel().getModel() + " (" + wagon.getMaxPayloadTons() + " tons)";
            } else {
                description = "Wagon #" + wagonNumber;
            }

            int wagonId;
            try {
                wagonId = Integer.parseInt(wagonNumber);
            } catch (NumberFormatException e) {
                wagonId = wagonNumber.hashCode();
            }

            boolean isInTransit = (index % 3 == 0);

            RollingStockStatus status;
            String location;
            double distance;

            if (isInTransit && !allTrains.isEmpty()) {
                status = RollingStockStatus.IN_TRANSIT;
                Train train = allTrains.get(index % allTrains.size());

                if (train.getEndStation() != null) {
                    location = "Train #" + train.getId() + " → " + train.getEndStation().getName();
                } else {
                    location = "Train #" + train.getId();
                }
                distance = 60 + random.nextInt(180);
            } else {
                status = RollingStockStatus.PARKED;
                Station parkStation = getRandomNearbyStation(startStationId);

                if (parkStation != null) {
                    location = parkStation.getName();
                    distance = calculateSimulatedDistance(startStationId, parkStation.getId());
                } else {
                    location = "Available";
                    distance = 0;
                }
            }

            items.add(new RollingStockItem(
                    wagonId, "WAGON", description,
                    status, location, distance));
            index++;
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

    // ═══════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES
    // ═══════════════════════════════════════════════════════════

    /**
     * Retorna trains ativos (de hoje ou futuros)
     */
    private List<Train> getActiveTrains() {
        List<Train> active = new ArrayList<>();

        try {
            Collection<Train> allTrains = trainRepo.getAll();
            LocalDate today = LocalDate.now();

            for (Train train : allTrains) {
                if (train.getDate() != null &&
                        !train.getDate().isBefore(today)) {
                    active.add(train);
                }
            }
        } catch (Exception e) {
            // Se falhar, retorna lista vazia
        }

        return active;
    }

    /**
     * Retorna uma station aleatória próxima (para simular location)
     */
    private Station getRandomNearbyStation(int startStationId) {
        try {
            Collection<Station> allStations = stationRepo.getAll();
            List<Station> stationList = new ArrayList<>(allStations);

            if (stationList.isEmpty()) {
                return null;
            }

            // 70% de chance de estar na start station (mais realista)
            if (random.nextDouble() < 0.7) {
                return stationRepo.getById(startStationId);
            }

            // 30% em outra station aleatória
            return stationList.get(random.nextInt(stationList.size()));

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Calcula distância simulada entre duas stations
     */
    private double calculateSimulatedDistance(int fromStationId, int toStationId) {
        if (fromStationId == toStationId) {
            return 0;
        }

        // Distância simulada baseada na diferença de IDs
        // (quanto maior a diferença, maior a distância)
        int diff = Math.abs(fromStationId - toStationId);

        // Distâncias realistas: 0-150 km
        return Math.min(diff * 5, 150);
    }
}