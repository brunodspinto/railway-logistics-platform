package org.example.CsvReaders;

import org.example.domain.Item;
import org.example.exception.ValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemCsvReader {

    public List<Item> parse(String filePath) {
        List<Item> items = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] fields = line.split(",");
                if (fields.length != 6) {
                    throw new ValidationException("Invalid item CSV format: " + line);
                }

                String sku = fields[0].trim();
                String name = fields[1].trim();
                String category = fields[2].trim();
                String unit = fields[3].trim();
                double volume = Double.parseDouble(fields[4].trim());
                double unitWeight = Double.parseDouble(fields[5].trim());

                Item item = new Item(sku, name, category, unit, volume, unitWeight);
                items.add(item);
            }

        } catch (IOException e) {
            throw new ValidationException("Error reading items file: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid number format in items file");
        }

        return items;
    }
}
