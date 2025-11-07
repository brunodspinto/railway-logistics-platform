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

        BufferedReader br = new BufferedReader(new FileReader(path));
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
                Station s = parseLine(line, totalLines);
                if (s != null) stations.add(s);
            } catch (Exception e) {
                errors.put(totalLines, e.getMessage());
            }
        }
        br.close();

        stations.sort(Comparator.comparing(Station::getName));
        return new ArrayList<>(stations);
    }

    private Station parseLine(String line, int num) {
        String[] fields = parseCSV(line);

        if (fields.length < 9) {
            errors.put(num, "Expected 9 fields, got " + fields.length);
            return null;
        }

        try {
            String country = fields[0].trim();
            String tz = cleanTZ(fields[1]);
            String tzGroup = fields[2].trim();
            String name = fields[3].trim();
            String latStr = fields[4].trim();
            String lonStr = fields[5].trim();
            boolean city = parseBool(fields[6].trim());
            boolean main = parseBool(fields[7].trim());
            boolean airport = parseBool(fields[8].trim());

            if (latStr.isEmpty() || lonStr.isEmpty()) {
                errors.put(num, "Empty coordinates");
                return null;
            }

            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);

            Station s = new Station(name, lat, lon, country, tz, tzGroup, city, main, airport);

            String err = s.getValidationError();
            if (err != null) {
                errors.put(num, err);
                return null;
            }

            return s;

        } catch (NumberFormatException e) {
            errors.put(num, "Invalid number: " + e.getMessage());
            return null;
        }
    }

    private String[] parseCSV(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        int parenLevel = 0;

        for (char c : line.toCharArray()) {
            if (c == '"' && parenLevel == 0) {
                inQuotes = !inQuotes;
                field.append(c);
            } else if (c == '(' && !inQuotes) {
                parenLevel++;
                field.append(c);
            } else if (c == ')' && !inQuotes) {
                parenLevel--;
                field.append(c);
            } else if (c == ',' && !inQuotes && parenLevel == 0) {
                fields.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString());

        return fields.toArray(new String[0]);
    }

    private String cleanTZ(String s) {
        if (s == null || s.isEmpty()) return "";

        s = s.trim();

        while (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1).trim();
        }

        while (s.startsWith("'") && s.endsWith("'")) {
            s = s.substring(1, s.length() - 1).trim();
        }

        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1).trim();
        }

        return s;
    }

    private boolean parseBool(String s) {
        if (s == null || s.isEmpty()) return false;
        String v = s.toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("yes") || v.equals("t");
    }

    public String getReport() {
        int valid = stations.size();
        int invalid = errors.size();
        int total = totalLines - 1;

        StringBuilder sb = new StringBuilder();
        sb.append("CSV Import Report\n");
        sb.append(String.format("Total: %d | Valid: %d (%.1f%%) | Invalid: %d (%.1f%%)\n",
                total, valid, 100.0 * valid / total, invalid, 100.0 * invalid / total));

        if (!errors.isEmpty()) {
            sb.append("\nFirst 10 errors:\n");
            errors.entrySet().stream().limit(10)
                    .forEach(e -> sb.append(String.format("Line %d: %s\n", e.getKey(), e.getValue())));

            if (errors.size() > 10) {
                sb.append(String.format("... and %d more\n", errors.size() - 10));
            }
        }

        return sb.toString();
    }

    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }

    public Map<Integer, String> getErrors() {
        return new HashMap<>(errors);
    }
}