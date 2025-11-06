package org.example.data;

import org.example.domain.Station;
import java.io.*;
import java.util.*;

/**
 * Loads railway stations from CSV file.
 * Validates each row and provides detailed error reporting.
 *
 * Expected CSV format (actual format from train_stations_europe.csv):
 * country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
 *
 * Notes:
 * - time_zone field contains format: ('Europe/Paris',) with comma inside parentheses
 * - latitude/longitude may be empty (will reject these rows)
 * - Boolean values are True/False (Python format)
 */
public class StationLoader {
    private final List<Station> validStations;
    private final Map<Integer, String> errors;
    private int totalLines;

    public StationLoader() {
        this.validStations = new ArrayList<>();
        this.errors = new HashMap<>();
        this.totalLines = 0;
    }

    /**
     * Load stations from CSV file.
     *
     * @param filePath Path to CSV file
     * @return List of valid stations
     * @throws IOException if file cannot be read
     */
    public List<Station> loadFromCSV(String filePath) throws IOException {
        validStations.clear();
        errors.clear();
        totalLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                totalLines++;

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Station station = parseLine(line, totalLines);
                    if (station != null) {
                        validStations.add(station);
                    }
                } catch (Exception e) {
                    errors.put(totalLines, "Unexpected error: " + e.getMessage());
                }
            }
        }

        validStations.sort(Comparator.comparing(Station::getName));
        return new ArrayList<>(validStations);
    }

    /**
     * Parse a single CSV line into a Station.
     * CSV Format: country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
     */
    private Station parseLine(String line, int lineNumber) {
        String[] fields = parseCSVLineWithParentheses(line);

        if (fields.length < 9) {
            errors.put(lineNumber,
                    String.format("Insufficient fields: expected 9, got %d", fields.length));
            return null;
        }

        try {
            String country = fields[0].trim();
            String timeZone = cleanTimeZone(fields[1]);
            String timeZoneGroup = fields[2].trim();
            String name = fields[3].trim();
            String latStr = fields[4].trim();
            String lonStr = fields[5].trim();
            boolean isCity = parseBoolean(fields[6].trim());
            boolean isMainStation = parseBoolean(fields[7].trim());
            boolean isAirport = parseBoolean(fields[8].trim());

            if (latStr.isEmpty() || lonStr.isEmpty()) {
                errors.put(lineNumber, "Empty latitude or longitude");
                return null;
            }

            double latitude = parseDouble(latStr, lineNumber);
            double longitude = parseDouble(lonStr, lineNumber);

            Station station = new Station(
                    name, latitude, longitude, country, timeZone, timeZoneGroup,
                    isCity, isMainStation, isAirport
            );

            String validationError = station.getValidationError();
            if (validationError != null) {
                errors.put(lineNumber, validationError);
                return null;
            }

            return station;

        } catch (NumberFormatException e) {
            errors.put(lineNumber,
                    String.format("Number format error: %s", e.getMessage()));
            return null;
        } catch (Exception e) {
            errors.put(lineNumber,
                    String.format("Parse error: %s", e.getMessage()));
            return null;
        }
    }

    /**
     * Parse CSV line respecting parentheses and quotes.
     * CRITICAL: Commas inside parentheses are NOT delimiters!
     * Example: ('Europe/Paris',) should be ONE field, not split at the internal comma.
     */
    private String[] parseCSVLineWithParentheses(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        int parenthesesLevel = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"' && parenthesesLevel == 0) {
                inQuotes = !inQuotes;
                currentField.append(c);
            } else if (c == '(' && !inQuotes) {
                parenthesesLevel++;
                currentField.append(c);
            } else if (c == ')' && !inQuotes) {
                parenthesesLevel--;
                currentField.append(c);
            } else if (c == ',' && !inQuotes && parenthesesLevel == 0) {
                // This is a REAL field delimiter
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Clean time zone field: ('Europe/Paris',) -> Europe/Paris
     */
    private String cleanTimeZone(String field) {
        if (field == null || field.isEmpty()) {
            return "";
        }

        field = field.trim();

        // Remove outer parentheses
        while (field.startsWith("(") && field.endsWith(")")) {
            field = field.substring(1, field.length() - 1).trim();
        }

        // Remove quotes
        while (field.startsWith("'") && field.endsWith("'")) {
            field = field.substring(1, field.length() - 1).trim();
        }

        // Remove trailing comma
        if (field.endsWith(",")) {
            field = field.substring(0, field.length() - 1).trim();
        }

        return field;
    }

    private double parseDouble(String value, int lineNumber) {
        if (value == null || value.isEmpty()) {
            throw new NumberFormatException("Empty coordinate at line " + lineNumber);
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    String.format("Cannot parse '%s' as double at line %d", value, lineNumber));
        }
    }

    private boolean parseBoolean(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        String v = value.toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("yes") ||
                v.equals("t") || v.equals("y");
    }

    /**
     * Get import report with statistics.
     */
    public String getImportReport() {
        int validCount = validStations.size();
        int errorCount = errors.size();
        int totalProcessed = totalLines - 1;

        StringBuilder sb = new StringBuilder();
        sb.append("=== CSV IMPORT REPORT ===\n");
        sb.append(String.format("Total lines processed: %d\n", totalProcessed));
        sb.append(String.format("Valid stations: %d (%.1f%%)\n",
                validCount, 100.0 * validCount / totalProcessed));
        sb.append(String.format("Invalid stations: %d (%.1f%%)\n",
                errorCount, 100.0 * errorCount / totalProcessed));

        if (!errors.isEmpty()) {
            sb.append("\n=== ERRORS (first 10) ===\n");
            errors.entrySet().stream()
                    .limit(10)
                    .forEach(e -> sb.append(String.format("Line %d: %s\n", e.getKey(), e.getValue())));

            if (errors.size() > 10) {
                sb.append(String.format("... and %d more errors\n", errors.size() - 10));
            }
        }

        return sb.toString();
    }

    public List<Station> getValidStations() {
        return new ArrayList<>(validStations);
    }

    public Map<Integer, String> getErrors() {
        return new HashMap<>(errors);
    }

    public int getTotalLines() {
        return totalLines;
    }
}
