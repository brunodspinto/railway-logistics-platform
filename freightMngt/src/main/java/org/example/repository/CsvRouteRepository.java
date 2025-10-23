package org.example.repository;

import org.example.domain.*;
import org.example.utils.CsvReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CsvRouteRepository implements IRouteRepository {

    private final Map<Integer, Station> stations;
    private final Map<Integer, Locomotive> locomotives;
    private final Map<Integer, Line> lines;

    public CsvRouteRepository(String dataFolder) throws IOException {
        this.stations = new HashMap<>();
        this.locomotives = new HashMap<>();
        this.lines = new HashMap<>();
        loadStations(dataFolder + "/facilities.csv");
        loadLines(dataFolder + "/lines.csv");
        loadSegments(dataFolder + "/segments.csv");
        loadLocomotives(dataFolder + "/locomotives.csv");
    }

    private void loadStations(String filepath) throws IOException {
        List<Map<String, String>> records = CsvReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            int id = Integer.parseInt(row.get("ID"));
            String name = row.get("Name");

            Station station = new Station(id, name);
            stations.put(id, station);
        }
        System.out.printf("Loaded %d stations%n", stations.size());
    }

    private void loadLines(String filepath) throws IOException {
        List<Map<String, String>> records = CsvReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            int id = Integer.parseInt(row.get("ID"));
            String name = row.get("Name");
            String owner = row.get("Owner");
            int startId = Integer.parseInt(row.get("Start ID"));
            int endId = Integer.parseInt(row.get("End ID"));
            int gauge = Integer.parseInt(row.get("Gauge"));

            Station start = stations.get(startId);
            Station end = stations.get(endId);

            if (start == null || end == null) {
                System.err.printf("Line %d: invalid station IDs (%d, %d)%n",
                        id, startId, endId);
                continue;
            }

            Line line = new Line(id, name, owner, start, end, gauge);
            lines.put(id, line);
        }
        System.out.printf("Loaded %d lines%n", lines.size());
    }

    private void loadSegments(String filepath) throws IOException {
        List<Map<String, String>> records = CsvReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            int id = Integer.parseInt(row.get("ID"));
            int lineId = Integer.parseInt(row.get("Line"));
            int order = Integer.parseInt(row.get("Order"));
            boolean electrified = row.get("Electrified").equalsIgnoreCase("Yes");
            int maxWeight = Integer.parseInt(row.get("Max Weight (kg/m)"));
            int length = Integer.parseInt(row.get("Length (m)"));
            int numberTracks = Integer.parseInt(row.get("NumberTracks"));

            LineSegment segment = new LineSegment(id, lineId, order, electrified,
                    maxWeight, length, numberTracks);

            Line line = lines.get(lineId);
            if (line != null) {
                line.addSegment(segment);
            } else {
                System.err.printf("Segment %d: line %d not found%n", id, lineId);
            }
        }
        System.out.printf("Loaded segments for %d lines%n", lines.size());
    }

    private void loadLocomotives(String filepath) throws IOException {
        List<Map<String, String>> records = CsvReader.readCsv(filepath);

        for (Map<String, String> row : records) {
            try {
                int number = Integer.parseInt(row.get("Number"));
                String name = row.get("Name");
                String make = row.get("Make");
                String model = row.get("Model");

                int serviceYear = parseIntSafe(row.get("Service"), 0);

                int power = parseIntSafe(row.get("Power"), 0);

                double length = parseDoubleSafe(row.get("Length"), 0.0);
                double weight = parseDoubleSafe(row.get("Weight (t)"), 0.0);

                int maxSpeed = parseIntSafe(row.get("Max Speed"), 0);
                int opSpeed = parseIntSafe(row.get("Operational Speed"), 0);

                String type = row.get("Type");
                int gauge = parseIntSafe(row.get("Bitola"), 1668); // default bitola ibérica

                // Combustível só existe para diesel
                Integer fuelCap = null;
                String fuelStr = row.get("Combustível (l)");
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
                System.err.printf(" Error parsing locomotive: %s - %s%n",
                        row.get("Number"), e.getMessage());
            }
        }
        System.out.printf("Loaded %d locomotives%n", locomotives.size());
    }

    // Helper methods para parsing seguro
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

    @Override
    public Line findDirectLine(int originId, int destinationId) {
        return lines.values().stream()
                .filter(line -> line.getStartStation().getId() == originId &&
                        line.getEndStation().getId() == destinationId)
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
}

