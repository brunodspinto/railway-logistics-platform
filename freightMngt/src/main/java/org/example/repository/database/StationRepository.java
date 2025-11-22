package org.example.repository.database;

import org.example.domain.Station;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class StationRepository {

    public Station getById(int id) {
        String query = "SELECT idStation, nameStation FROM Station WHERE idStation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Station(
                        rs.getInt("idStation"),
                        rs.getString("nameStation")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error loading station " + id + ": " + e.getMessage());
        }

        return null;
    }

    public Collection<Station> getAll() {
        Collection<Station> stations = new ArrayList<>();
        String query = "SELECT idStation, nameStation FROM Station ORDER BY idStation";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stations.add(new Station(
                        rs.getInt("idStation"),
                        rs.getString("nameStation")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading stations: " + e.getMessage());
        }

        return stations;
    }
}