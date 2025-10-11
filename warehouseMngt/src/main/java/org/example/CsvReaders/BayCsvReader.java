package org.example.CsvReaders;

import org.example.domain.Bay;
import org.example.domain.Location;
import org.example.exception.ValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BayCsvReader {

    public List<Bay> parse(String filePath) {
        List<Bay> bays = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] fields = line.split(",");
                if (fields.length != 4) {
                    throw new ValidationException("Invalid bay CSV format: " + line);
                }

                String warehouseId = fields[0].trim();
                int aisle = Integer.parseInt(fields[1].trim());
                int bayNumber = Integer.parseInt(fields[2].trim());
                int capacity = Integer.parseInt(fields[3].trim());

                Bay bay = new Bay(warehouseId, aisle, bayNumber, capacity);
                bays.add(bay);
            }

        } catch (IOException e) {
            throw new ValidationException("Error reading bays file: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid number format in bays file");
        }

        return bays;
    }
}
