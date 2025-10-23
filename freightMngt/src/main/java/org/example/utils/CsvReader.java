package org.example.utils;

import java.io.*;
import java.util.*;

public class CsvReader {

    public static List<Map<String, String>> readCsv(String filepath) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filepath), "UTF-8"))) {

            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("CSV vazio: " + filepath);
            }

            String delimiter = headerLine.contains("\t") ? "\t" : ",";
            String[] headers = headerLine.split(delimiter);

            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim().replace("\uFEFF", "");
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(delimiter, -1);

                Map<String, String> record = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String value = i < values.length ? values[i].trim() : "";
                    record.put(headers[i], value);
                }
                records.add(record);
            }
        }

        return records;
    }
}
