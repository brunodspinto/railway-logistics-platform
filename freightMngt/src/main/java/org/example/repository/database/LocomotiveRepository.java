package org.example.repository.database;

import org.example.domain.Locomotive;
import org.example.utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;  // ← ADICIONAR ESTE

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class LocomotiveRepository {

    public Locomotive getById(int number) {
        String query = """
            SELECT l.numberLocomotive, l.name, l.yearOfEntry, l.operationalSpeed,
                   lm.id as model_id, lm.make, lm.modelName, lm.power, 
                   lm.maxSpeed, lm.weight, lm.length, 
                   e.voltage, d.combustivelCapacity
            FROM Locomotive l
            JOIN LocomotiveModel lm ON l.model = lm.id
            LEFT JOIN Electric e ON lm.id = e.locomotiveModelId
            LEFT JOIN Diesel d ON lm.id = d.locomotiveModelId
            WHERE l.numberLocomotive = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, number);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Extrair dados ANTES de fechar ResultSet
                int modelId = rs.getInt("model_id");
                String name = rs.getString("name");
                String make = rs.getString("make");
                String modelName = rs.getString("modelName");
                int yearOfEntry = rs.getInt("yearOfEntry");
                int power = rs.getInt("power");
                double length = rs.getDouble("length");
                double weight = rs.getDouble("weight");
                int maxSpeed = rs.getInt("maxSpeed");
                int operationalSpeed = rs.getInt("operationalSpeed");

                Integer voltage = null;
                if (rs.getObject("voltage") != null) {
                    voltage = rs.getBigDecimal("voltage").intValue();
                }

                Integer fuelCapacity = null;
                if (rs.getObject("combustivelCapacity") != null) {
                    fuelCapacity = rs.getBigDecimal("combustivelCapacity").intValue();
                }

                String type = (voltage != null) ? "Electric" : "Diesel";

                // AGORA buscar gauge (ResultSet já foi lido)
                int gauge = getGauge(conn, modelId);

                return new Locomotive(
                        number,
                        name,
                        make,
                        modelName,
                        yearOfEntry,
                        power,
                        length,
                        weight,
                        maxSpeed,
                        operationalSpeed,
                        type,
                        gauge,
                        fuelCapacity
                );
            }

        } catch (SQLException e) {
            System.err.println("Error loading locomotive " + number + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Collection<Locomotive> getAll() {
        Collection<Locomotive> locomotives = new ArrayList<>();

        // PASSO 1: Coletar todos os IDs primeiro
        List<Integer> ids = new ArrayList<>();
        String queryIds = "SELECT numberLocomotive FROM Locomotive ORDER BY numberLocomotive";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryIds);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("numberLocomotive"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading locomotive IDs: " + e.getMessage());
            return locomotives;
        }

        // PASSO 2: Buscar cada locomotiva (conexões independentes)
        for (Integer id : ids) {
            Locomotive loco = getById(id);
            if (loco != null) {
                locomotives.add(loco);
            }
        }

        return locomotives;
    }

    // Método auxiliar que RECEBE a conexão (não cria nova)
    private int getGauge(Connection conn, int modelId) {
        String query = """
            SELECT g.measure
            FROM GaugeLocomotiveModel glm
            JOIN Gauge g ON glm.gaugeId = g.idGauge
            WHERE glm.locomotiveModelId = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, modelId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("measure");
            }
        } catch (SQLException e) {
            System.err.println("Error loading gauge: " + e.getMessage());
        }

        return 1668; // Default
    }
}