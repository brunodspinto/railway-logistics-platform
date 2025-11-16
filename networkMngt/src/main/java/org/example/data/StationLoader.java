package org.example.data;

import org.example.domain.Station;
import java.io.*;
import java.util.*;

public class StationLoader {
    private List<Station> stations;
    private Map<Integer, String> errors;
    private int totalLines;

    public StationLoader() {
        this.stations = new ArrayList<>();
        this.errors = new HashMap<>();
    }

    public List<Station> loadFromCSV(String path) throws IOException {
        stations.clear();
        errors.clear();
        totalLines = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), "UTF-8"))) {

            String line;
            boolean header = true;

            while ((line = br.readLine()) != null) {
                totalLines++;
                if (header) {
                    header = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                try {
                    Station station = parseLine(line, totalLines);
                    if (station != null) {
                        stations.add(station);
                    }
                } catch (Exception e) {
                    errors.put(totalLines, "Parse error: " + e.getMessage());
                }
            }
        }

        sortStationsByName(stations);

        return new ArrayList<>(stations);
    }

    private Station parseLine(String line, int lineNumber) {
        String[] fields = parseCSV(line);

        if (fields.length < 9) {
            errors.put(lineNumber, "Insufficient fields: " + fields.length);
            return null;
        }

        try {
            String country = fields[0].trim();
            String timezone = cleanTimezoneField(fields[1]);
            String timezoneGroup = fields[2].trim();
            String name = fields[3].trim();
            String latitudeStr = fields[4].trim();
            String longitudeStr = fields[5].trim();
            boolean isCity = parseBoolean(fields[6].trim());
            boolean isMainStation = parseBoolean(fields[7].trim());
            boolean isAirport = parseBoolean(fields[8].trim());

            if (name.isEmpty()) {
                errors.put(lineNumber, "Station name cannot be empty");
                return null;
            }

            if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                errors.put(lineNumber, "Missing coordinates");
                return null;
            }

            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);

            if (latitude < -90 || latitude > 90) {
                errors.put(lineNumber, "Invalid latitude: " + latitude);
                return null;
            }
            if (longitude < -180 || longitude > 180) {
                errors.put(lineNumber, "Invalid longitude: " + longitude);
                return null;
            }

            Station station = new Station(name, latitude, longitude, country, timezone, timezoneGroup, isCity, isMainStation, isAirport);

            return station;

        } catch (NumberFormatException e) {
            errors.put(lineNumber, "Invalid numeric format: " + e.getMessage());
            return null;
        } catch (Exception e) {
            errors.put(lineNumber, "Unexpected error: " + e.getMessage());
            return null;
        }
    }

    private String[] parseCSV(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                insideQuotes = !insideQuotes;
                currentField.append(currentChar);
            } else if (currentChar == ',' && !insideQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(currentChar);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    private String cleanTimezoneField(String field) {
        if (field == null || field.isEmpty()) {
            return "";
        }

        field = field.trim();

        if (field.startsWith("(") && field.endsWith(")")) {
            field = field.substring(1, field.length() - 1).trim();
        }

        if (field.startsWith("'") && field.endsWith("'")) {
            field = field.substring(1, field.length() - 1).trim();
        }

        if (field.endsWith(",")) {
            field = field.substring(0, field.length() - 1).trim();
        }

        return field;
    }

    private boolean parseBoolean(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        String lowerValue = value.toLowerCase();
        return lowerValue.equals("true") || lowerValue.equals("1") ||
                lowerValue.equals("yes") || lowerValue.equals("t");
    }

    /**
     * Sort stations by name.
     */
    private void sortStationsByName(List<Station> stationList) {
        int n = stationList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Station s1 = stationList.get(j);
                Station s2 = stationList.get(j + 1);

                if (s1.getName().compareTo(s2.getName()) > 0) {
                    // Swap
                    stationList.set(j, s2);
                    stationList.set(j + 1, s1);
                }
            }
        }
    }

    public String getReport() {
        int validStations = stations.size();
        int errorCount = errors.size();
        int totalProcessed = totalLines - 1;

        StringBuilder report = new StringBuilder();
        report.append("=== CSV IMPORT REPORT ===\n");
        report.append(String.format("Total lines processed: %d\n", totalProcessed));
        report.append(String.format("Valid stations: %d (%.1f%%)\n", validStations, totalProcessed > 0 ? 100.0 * validStations / totalProcessed : 0));
        report.append(String.format("Invalid lines: %d (%.1f%%)\n", errorCount, totalProcessed > 0 ? 100.0 * errorCount / totalProcessed : 0));

        if (!errors.isEmpty()) {
            report.append("\n=== ERRORS (first 10) ===\n");
            int displayedErrors = 0;
            for (Map.Entry<Integer, String> error : errors.entrySet()) {
                if (displayedErrors >= 10) break;
                report.append(String.format("Line %d: %s\n", error.getKey(), error.getValue()));
                displayedErrors++;
            }

            if (errors.size() > 10) {
                report.append(String.format("... and %d more errors\n", errors.size() - 10));
            }
        }

        return report.toString();
    }

    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }

    public Map<Integer, String> getErrors() {
        return new HashMap<>(errors);
    }
}