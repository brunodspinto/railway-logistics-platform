package org.example.utils;

import java.io.*;
import java.util.*;

public class CSVReader {

    /**
     * Lê um ficheiro CSV e retorna lista de registos (cada registo = Map<header, value>)
     *
     * Suporta:
     * - Delimitadores: vírgula ou tab
     * - Campos com aspas: "valor1,valor2,valor3"
     * - BOM (Byte Order Mark) UTF-8
     * - Linhas vazias (ignoradas)
     *
     * @param filepath Caminho do ficheiro CSV
     * @return Lista de registos
     * @throws IOException Se erro ao ler ficheiro
     */
    public static List<Map<String, String>> readCsv(String filepath) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filepath), "UTF-8"))) {

            // Ler header
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("CSV vazio: " + filepath);
            }

            // Detectar delimitador (vírgula ou tab)
            String delimiter = headerLine.contains("\t") ? "\t" : ",";

            // Parse headers (com suporte a aspas)
            String[] headers = parseCsvLine(headerLine, delimiter);

            // Limpar headers (BOM, espaços)
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim().replace("\uFEFF", "");
            }

            // Ler registos
            String line;
            int lineNumber = 1; // Para debug
            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Ignorar linhas vazias
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Parse valores (com suporte a aspas)
                String[] values = parseCsvLine(line, delimiter);

                // Criar registo
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

    /**
     * Faz parse de uma linha CSV considerando campos entre aspas.
     *
     * Exemplos:
     * - "a,b,c" → ["a", "b", "c"]
     * - "a,\"b,c\",d" → ["a", "b,c", "d"]
     * - "a,\"b,c,d\",e" → ["a", "b,c,d", "e"]
     *
     * @param line Linha a parsear
     * @param delimiter Delimitador (normalmente "," ou "\t")
     * @return Array de valores
     */
    private static String[] parseCsvLine(String line, String delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Toggle estado de "dentro de aspas"
                insideQuotes = !insideQuotes;
                // Não adicionar as aspas ao valor
            } else if (c == delimiter.charAt(0) && !insideQuotes) {
                // Delimitador fora de aspas = fim do campo
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                // Caráter normal
                currentValue.append(c);
            }
        }

        // Adicionar último valor
        values.add(currentValue.toString());

        return values.toArray(new String[0]);
    }

    /**
     * Versão auxiliar para ler CSV com delimitador específico
     */
    public static List<Map<String, String>> readCsv(String filepath, String delimiter)
            throws IOException {
        // Se precisares forçar um delimitador específico
        // (não recomendado, mas útil para casos especiais)

        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filepath), "UTF-8"))) {

            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("CSV vazio: " + filepath);
            }

            String[] headers = parseCsvLine(headerLine, delimiter);

            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim().replace("\uFEFF", "");
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = parseCsvLine(line, delimiter);

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
