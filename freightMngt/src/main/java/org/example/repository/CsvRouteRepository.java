package org.example.repository;

import org.example.domain.*;
import org.example.service.RollingStockItem;
import org.example.utils.CSVReader;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CsvRouteRepository implements IRouteRepository {

    // ===== EXISTENTE =====
    private final Map<Integer, Station> stations;
    private final Map<Integer, Locomotive> locomotives;
    private final Map<Integer, Line> lines;

    // ===== NOVO =====
    private final Map<Integer, WagonModel> wagonModels;
    private final Map<String, Wagon> wagons;
    private final Map<Integer, Freight> freights;
    private final Map<Integer, Train> trains;

    public CsvRouteRepository(String dataFolder) throws IOException {
        // Existente
        this.stations = new HashMap<>();
        this.locomotives = new HashMap<>();
        this.lines = new HashMap<>();

        // Novo
        this.wagonModels = new HashMap<>();
        this.wagons = new HashMap<>();
        this.freights = new HashMap<>();
        this.trains = new HashMap<>();

        // Carregar tudo na ordem correta (dependências)
        loadStations(dataFolder + "/facilities.csv");
        loadLines(dataFolder + "/lines.csv");
        loadSegments(dataFolder + "/segments.csv");
        loadLocomotives(dataFolder + "/locomotives.csv");
        loadWagonModels(dataFolder + "/wagon_models.csv");
        loadWagons(dataFolder + "/wagons.csv");
        loadFreights(dataFolder + "/freights.csv");
        loadTrains(dataFolder + "/trains.csv");

        // Resolver dependências (lazy loading)
        resolveWagonModels();
        resolveFreightDependencies();
        resolveTrainDependencies();
    }

    // ===== MÉTODOS EXISTENTES (manter como estão) =====

    private void loadStations(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            int id = Integer.parseInt(row.get("id"));
            String name = row.get("name");

            Station station = new Station(id, name);
            stations.put(id, station);
        }
        System.out.printf("✅ Loaded %d stations%n", stations.size());
    }

    private void loadLines(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            int id = Integer.parseInt(row.get("id"));
            String name = row.get("name");
            String owner = row.get("owner");
            int startId = Integer.parseInt(row.get("startId"));
            int endId = Integer.parseInt(row.get("endId"));
            int gauge = Integer.parseInt(row.get("gauge"));

            Station start = stations.get(startId);
            Station end = stations.get(endId);

            if (start == null || end == null) {
                System.err.printf("⚠️ Line %d: invalid station IDs (%d, %d)%n",
                        id, startId, endId);
                continue;
            }

            Line line = new Line(id, name, owner, start, end, gauge);
            lines.put(id, line);
        }
        System.out.printf("✅ Loaded %d lines%n", lines.size());
    }

    private void loadSegments(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            int id = Integer.parseInt(row.get("id"));
            int lineId = Integer.parseInt(row.get("lineId"));
            int order = Integer.parseInt(row.get("order"));
            boolean electrified = row.get("electrified").equalsIgnoreCase("Yes");
            int maxWeight = Integer.parseInt(row.get("maxWeightKgM"));
            int length = Integer.parseInt(row.get("lengthM"));
            int numberTracks = Integer.parseInt(row.get("numberTracks"));

            // ⭐ NOVO: Siding fields (nullable)
            Integer sidingPosition = parseIntNullable(row.get("sidingPosition"));
            Integer sidingLength = parseIntNullable(row.get("sidingLength"));

            LineSegment segment = new LineSegment(id, lineId, order, electrified,
                    maxWeight, length, numberTracks, sidingPosition, sidingLength);

            Line line = lines.get(lineId);
            if (line != null) {
                line.addSegment(segment);
            } else {
                System.err.printf("⚠️ Segment %d: line %d not found%n", id, lineId);
            }
        }
        System.out.printf("✅ Loaded segments for %d lines%n", lines.size());
    }

    private void loadLocomotives(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            try {
                int number = Integer.parseInt(row.get("number"));
                String name = row.get("name");
                if (name == null || name.trim().isEmpty()) {
                    name = "Loco-" + number; // Default se não tiver nome
                }

                String make = row.get("make");
                String model = row.get("model");
                int serviceYear = parseIntSafe(row.get("serviceYear"), 0);
                int power = parseIntSafe(row.get("power"), 0); // kW

                double length = parseDoubleSafe(row.get("length"), 0.0);
                double weight = parseDoubleSafe(row.get("weight"), 0.0);

                int maxSpeed = parseIntSafe(row.get("maxSpeed"), 0);
                int opSpeed = parseIntSafe(row.get("operationalSpeed"), 0);

                String type = row.get("type"); // "Electric" ou "Diesel"
                int gauge = parseIntSafe(row.get("gauge"), 1668);

                // Combustível só existe para diesel
                Integer fuelCap = null;
                String fuelStr = row.get("fuelCapacity");
                if (fuelStr != null && !fuelStr.trim().isEmpty()) {
                    try {
                        fuelCap = Integer.parseInt(fuelStr.trim());
                    } catch (NumberFormatException e) {
                        // Locomotiva elétrica - sem combustível
                    }
                }

                Locomotive loco = new Locomotive(number, name, make, model, serviceYear,
                        power, length, weight, maxSpeed, opSpeed, type, gauge, fuelCap);
                locomotives.put(number, loco);

            } catch (Exception e) {
                System.err.printf("⚠️ Error parsing locomotive: %s - %s%n",
                        row.get("number"), e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.printf("✅ Loaded %d locomotives%n", locomotives.size());
    }

    // ===== NOVOS MÉTODOS DE LOADING =====

    private void loadWagonModels(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            try {
                int id = Integer.parseInt(row.get("id"));
                String model = row.get("model");
                String maker = row.get("maker");
                int numberBogies = parseIntSafe(row.get("numberBogies"), 2);
                String bogies = row.get("bogies");
                int lengthMm = parseIntSafe(row.get("lengthMm"), 0);
                int widthMm = parseIntSafe(row.get("widthMm"), 0);
                int heightMm = parseIntSafe(row.get("heightMm"), 0);
                double weightTons = parseDoubleSafe(row.get("weightTons"), 0.0);
                int maxSpeed = parseIntSafe(row.get("maxSpeed"), 100);
                double payloadTons = parseDoubleSafe(row.get("payloadTons"), 0.0);
                double volumeM3 = parseDoubleSafe(row.get("volumeM3"), 0.0);
                String type = row.get("type");
                int bitola = parseIntSafe(row.get("bitola"), 1668);

                WagonModel wagonModel = new WagonModel(id, model, maker, numberBogies,
                        bogies, lengthMm, widthMm, heightMm, weightTons, maxSpeed,
                        payloadTons, volumeM3, type, bitola);

                wagonModels.put(id, wagonModel);

            } catch (Exception e) {
                System.err.printf("⚠️ Error parsing wagon model: %s - %s%n",
                        row.get("id"), e.getMessage());
            }
        }
        System.out.printf("✅ Loaded %d wagon models%n", wagonModels.size());
    }

    private void loadWagons(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            try {
                String number = row.get("number");
                int modelId = Integer.parseInt(row.get("modelId"));
                String operator = row.get("operator");
                int serviceYear = parseIntSafe(row.get("serviceYear"), 0);

                Wagon wagon = new Wagon(number, modelId, operator, serviceYear);
                wagons.put(number, wagon);

            } catch (Exception e) {
                System.err.printf("⚠️ Error parsing wagon: %s - %s%n",
                        row.get("number"), e.getMessage());
            }
        }
        System.out.printf("✅ Loaded %d wagons%n", wagons.size());
    }

    private void loadFreights(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Map<String, String> row : records) {
            try {
                int id = Integer.parseInt(row.get("id"));
                LocalDate date = LocalDate.parse(row.get("date"), dateFormatter);
                int originId = Integer.parseInt(row.get("originId"));
                String originName = row.get("originName");
                int destinationId = Integer.parseInt(row.get("destinationId"));
                String destinationName = row.get("destinationName");

                // Parse wagon numbers (pode ter vírgulas)
                String wagonNumbersStr = row.get("wagonNumbers");
                List<String> wagonNumbers = Arrays.asList(wagonNumbersStr.split(","));

                // Trim spaces
                wagonNumbers = wagonNumbers.stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Freight freight = new Freight(id, date, originId, originName,
                        destinationId, destinationName, wagonNumbers);

                freights.put(id, freight);

            } catch (Exception e) {
                System.err.printf("⚠️ Error parsing freight: %s - %s%n",
                        row.get("id"), e.getMessage());
            }
        }
        System.out.printf("✅ Loaded %d freights%n", freights.size());
    }

    private void loadTrains(String filepath) throws IOException {
        List<Map<String, String>> records = CSVReader.readCsv(filepath);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Map<String, String> row : records) {
            try {
                int id = Integer.parseInt(row.get("id"));
                String operator = row.get("operator");
                LocalDate date = LocalDate.parse(row.get("date"), dateFormatter);
                LocalTime time = LocalTime.parse(row.get("time"), timeFormatter);
                int startId = Integer.parseInt(row.get("startId"));
                int endId = Integer.parseInt(row.get("endId"));

                // Parse freight IDs (pode ter vírgulas)
                String freightIdsStr = row.get("freightIds");
                List<Integer> freightIds = parseIntegerList(freightIdsStr);

                // Parse locomotive numbers (pode ter vírgulas)
                String locoNumbersStr = row.get("locomotiveNumbers");
                List<Integer> locomotiveNumbers = parseIntegerList(locoNumbersStr);

                // Parse path station IDs (sempre tem vírgulas)
                String pathIdsStr = row.get("pathFacilityIds");
                List<Integer> pathStationIds = parseIntegerList(pathIdsStr);

                Train train = new Train(id, operator, date, time, startId, endId,
                        freightIds, locomotiveNumbers, pathStationIds);

                trains.put(id, train);

            } catch (Exception e) {
                System.err.printf("⚠️ Error parsing train: %s - %s%n",
                        row.get("id"), e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.printf("✅ Loaded %d trains%n", trains.size());
    }

    // ===== MÉTODOS DE RESOLUÇÃO DE DEPENDÊNCIAS =====

    private void resolveWagonModels() {
        for (Wagon wagon : wagons.values()) {
            WagonModel model = wagonModels.get(wagon.getModelId());
            if (model != null) {
                wagon.setModel(model);
            } else {
                System.err.printf("⚠️ Wagon %s: model %d not found%n",
                        wagon.getNumber(), wagon.getModelId());
            }
        }
        System.out.println("✅ Resolved wagon models");
    }

    private void resolveFreightDependencies() {
        for (Freight freight : freights.values()) {
            // Resolver origin/destination stations
            Station origin = stations.get(freight.getOriginId());
            Station destination = stations.get(freight.getDestinationId());

            if (origin != null) {
                freight.setOrigin(origin);
            } else {
                System.err.printf("⚠️ Freight %d: origin station %d not found%n",
                        freight.getId(), freight.getOriginId());
            }

            if (destination != null) {
                freight.setDestination(destination);
            } else {
                System.err.printf("⚠️ Freight %d: destination station %d not found%n",
                        freight.getId(), freight.getDestinationId());
            }

            // Resolver wagons
            List<Wagon> freightWagons = new ArrayList<>();
            for (String wagonNumber : freight.getWagonNumbers()) {
                Wagon wagon = wagons.get(wagonNumber);
                if (wagon != null) {
                    freightWagons.add(wagon);
                } else {
                    System.err.printf("⚠️ Freight %d: wagon %s not found%n",
                            freight.getId(), wagonNumber);
                }
            }
            freight.setWagons(freightWagons);
        }
        System.out.println("✅ Resolved freight dependencies");
    }

    private void resolveTrainDependencies() {
        for (Train train : trains.values()) {
            // Resolver stations
            Station start = stations.get(train.getStartId());
            Station end = stations.get(train.getEndId());

            if (start != null) {
                train.setStartStation(start);
            } else {
                System.err.printf("⚠️ Train %d: start station %d not found%n",
                        train.getId(), train.getStartId());
            }

            if (end != null) {
                train.setEndStation(end);
            } else {
                System.err.printf("⚠️ Train %d: end station %d not found%n",
                        train.getId(), train.getEndId());
            }

            // Resolver locomotives
            List<Locomotive> trainLocos = new ArrayList<>();
            for (int locoNumber : train.getLocomotiveNumbers()) {
                Locomotive loco = locomotives.get(locoNumber);
                if (loco != null) {
                    trainLocos.add(loco);
                } else {
                    System.err.printf("⚠️ Train %d: locomotive %d not found%n",
                            train.getId(), locoNumber);
                }
            }
            train.setLocomotives(trainLocos);

            // Resolver freights
            List<Freight> trainFreights = new ArrayList<>();
            for (int freightId : train.getFreightIds()) {
                Freight freight = freights.get(freightId);
                if (freight != null) {
                    trainFreights.add(freight);
                } else {
                    System.err.printf("⚠️ Train %d: freight %d not found%n",
                            train.getId(), freightId);
                }
            }
            train.setFreights(trainFreights);

            // Resolver path stations
            List<Station> pathStations = new ArrayList<>();
            for (int stationId : train.getPathStationIds()) {
                Station station = stations.get(stationId);
                if (station != null) {
                    pathStations.add(station);
                } else {
                    System.err.printf("⚠️ Train %d: path station %d not found%n",
                            train.getId(), stationId);
                }
            }
            train.setPathStations(pathStations);
        }
        System.out.println("✅ Resolved train dependencies");
    }

    // ===== HELPER METHODS =====

    private int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Integer parseIntNullable(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double parseDoubleSafe(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            String normalized = value.trim().replace(',', '.');
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private List<Integer> parseIntegerList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] parts = value.split(",");
        List<Integer> result = new ArrayList<>();

        for (String part : parts) {
            try {
                result.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException e) {
                System.err.printf("⚠️ Invalid integer in list: '%s'%n", part);
            }
        }

        return result;
    }


    // ===== MÉTODOS EXISTENTES (implementação) =====

    @Override
    public Line findDirectLine(int originId, int destinationId) {
        // Tentar direção normal (origin → destination)
        Line directLine = lines.values().stream()
                .filter(line -> line.getStartStation().getId() == originId &&
                        line.getEndStation().getId() == destinationId)
                .findFirst()
                .orElse(null);

        if (directLine != null) {
            return directLine;
        }

        // Tentar direção inversa (destination → origin)
        // As linhas ferroviárias são bidirecionais!
        return lines.values().stream()
                .filter(line -> line.getStartStation().getId() == destinationId &&
                        line.getEndStation().getId() == originId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Station getStation(int id) {
        return stations.get(id);
    }

    @Override
    public Locomotive getLocomotive(int number) {
        return locomotives.get(number);
    }

    @Override
    public Collection<Line> getAllLines() {
        return lines.values();
    }

    @Override
    public Collection<Locomotive> getAllLocomotives() {
        return locomotives.values();
    }

    @Override
    public Collection<Station> getAllStations() {
        return stations.values();
    }

    // ===== NOVOS MÉTODOS (implementação) =====

    @Override
    public WagonModel getWagonModel(int id) {
        return wagonModels.get(id);
    }

    @Override
    public Wagon getWagon(String number) {
        return wagons.get(number);
    }

    @Override
    public Collection<WagonModel> getAllWagonModels() {
        return wagonModels.values();
    }

    @Override
    public Collection<Wagon> getAllWagons() {
        return wagons.values();
    }

    @Override
    public Freight getFreight(int id) {
        return freights.get(id);
    }

    @Override
    public Collection<Freight> getAllFreights() {
        return freights.values();
    }

    /**
     * Retorna apenas as cargas que ainda NÃO foram associadas a nenhum comboio.
     */
    @Override
    public List<Freight> getAllPendingFreights() {
        // 1. Identificar IDs de cargas já agendadas
        Set<Integer> assignedFreightIds = new HashSet<>();
        for (Train t : trains.values()) {
            assignedFreightIds.addAll(t.getFreightIds());
        }

        // 2. Retornar apenas as cargas que não estão nesse conjunto
        return freights.values().stream()
                .filter(f -> !assignedFreightIds.contains(f.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Train getTrain(int id) {
        return trains.get(id);
    }

    @Override
    public Collection<Train> getAllTrains() {
        return trains.values();
    }

    @Override
    public List<LineSegment> getSegmentsByLine(int lineId) {
        Line line = lines.get(lineId);
        return line != null ? line.getSegments() : new ArrayList<>();
    }

    @Override
    public Line getLineById(int lineId) {
        return lines.get(lineId);
    }

    // ===== METODO PARA USLP07 - CONFLICT DETECTION =====

    @Override
    public List<Train> getTrainsByDate(LocalDate date) {
        // Filtrar trains que têm a data especificada
        return trains.values().stream()
                .filter(train -> train.getDate().equals(date))
                .sorted(Comparator.comparing(Train::getTime))  // Ordenar por hora
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════
// USLP09 - Stub methods (CSV não suporta esta funcionalidade)
// ═══════════════════════════════════════════════════════════

    @Override
    public List<RollingStockItem> getAvailableLocomotives(int startStationId) {
        System.out.println("(!) getAvailableLocomotives() not supported in CSV mode.");
        return new ArrayList<>();
    }

    @Override
    public List<RollingStockItem> getAvailableWagons(int startStationId) {
        System.out.println("(!) getAvailableWagons() not supported in CSV mode.");
        return new ArrayList<>();
    }

    @Override
    public boolean assignTrainRollingStock(int trainId, List<Integer> locoIds,
                                           List<Integer> wagonIds) {
        System.out.println("(!) assignTrainRollingStock() not supported in CSV mode.");
        return false;
    }
}