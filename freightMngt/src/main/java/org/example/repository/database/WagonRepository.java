package org.example.repository.database;

import org.example.domain.Wagon;
import org.example.domain.WagonModel;
import org.example.utils.DatabaseConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;  // ← ADICIONAR ESTE

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class WagonRepository {

    public Wagon getById(String number) {
        String query = """
            SELECT w.numberWagon, w.wagonModelId, w.weight, w.yearOfEntry,
                   ow.operatorVatNumber as operator
            FROM Wagon w
            JOIN OperatorWagon ow ON w.numberWagon = ow.wagonNumber
            WHERE w.numberWagon = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, number);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Extrair dados ANTES de buscar WagonModel
                String wagonNumber = rs.getString("numberWagon");
                int modelId = rs.getInt("wagonModelId");
                double weight = rs.getDouble("weight");
                String operator = rs.getString("operator");
                int yearOfEntry = rs.getInt("yearOfEntry");

                Wagon wagon = new Wagon(
                        wagonNumber,
                        modelId,
                        operator,
                        yearOfEntry
                );

                // AGORA buscar WagonModel (ResultSet já foi lido)
                WagonModel model = loadWagonModel(conn, modelId, weight);
                if (model != null) {
                    wagon.setModel(model);
                }

                return wagon;
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon " + number + ": " + e.getMessage());
        }

        return null;
    }

    public Collection<Wagon> getAll() {
        Collection<Wagon> wagons = new ArrayList<>();

        // PASSO 1: Coletar todos os números primeiro
        List<String> numbers = new ArrayList<>();
        String queryNumbers = "SELECT numberWagon FROM Wagon ORDER BY numberWagon";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryNumbers);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                numbers.add(String.valueOf(rs.getInt("numberWagon")));
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon numbers: " + e.getMessage());
            return wagons;
        }

        // PASSO 2: Buscar cada wagon (conexões independentes)
        for (String number : numbers) {
            Wagon wagon = getById(number);
            if (wagon != null) {
                wagons.add(wagon);
            }
        }

        return wagons;
    }

    public WagonModel getModelById(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return loadWagonModel(conn, id, 1.0);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    public Collection<WagonModel> getAllModels() {
        Collection<WagonModel> models = new ArrayList<>();
        String query = "SELECT id FROM WagonModel ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                WagonModel model = getModelById(rs.getInt("id"));
                if (model != null) {
                    models.add(model);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon models: " + e.getMessage());
        }

        return models;
    }

    // Metodo auxiliar que RECEBE a conexão
    private WagonModel loadWagonModel(Connection conn, int id, double weight) {
        String query = """
            SELECT wm.id, wm.nameModel, wm.maker, wm.length, wm.width, wm.height,
                   wm.maxSpeed, wm.payload, wm.volume,
                   b.nameBogie, 
                   wt.description as wagon_type
            FROM WagonModel wm
            JOIN Bogies b ON wm.Bogiesid = b.id
            JOIN WagonsType wt ON wm.wagonsTypeId = wt.id
            WHERE wm.id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int gauge = getGauge(conn, id);

                return new WagonModel(
                        rs.getInt("id"),
                        rs.getString("nameModel"),
                        rs.getString("maker"),
                        2,
                        rs.getString("nameBogie"),
                        rs.getInt("length"),
                        rs.getInt("width"),
                        rs.getInt("height"),
                        weight,
                        rs.getInt("maxSpeed"),
                        rs.getDouble("payload"),
                        rs.getDouble("volume"),
                        rs.getString("wagon_type"),
                        gauge
                );
            }

        } catch (SQLException e) {
            // ALTERAÇÃO: Mudei de System.err para System.out para não aparecer a vermelho
            System.out.println("Aviso: Não foi possível carregar o modelo de vagão " + id + ": " + e.getMessage());
        }

        return null;
    }

    // Metodo auxiliar que RECEBE a conexão
    private int getGauge(Connection conn, int modelId) {
        String query = """
            SELECT g.measure
            FROM GaugeWagonModel gwm
            JOIN Gauge g ON gwm.gaugeId = g.idGauge
            WHERE gwm.wagonModelId = ?
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

        return 1668;
    }
}