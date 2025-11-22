package org.example.repository.database;

import org.example.domain.Freight;
import org.example.domain.Station;
import org.example.domain.Wagon;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FreightRepository {
    private final StationRepository stationRepo = new StationRepository();
    private final WagonRepository wagonRepo = new WagonRepository();

    public Freight getById(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Buscar freight básico
            String query = "SELECT id, dateFreights FROM Freights WHERE id = ?";
            LocalDate date = null;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    date = rs.getDate("dateFreights").toLocalDate();
                } else {
                    System.err.println("Freight " + id + " not found");
                    return null;
                }
            }

            // 2. Buscar origem e destino (MESMA conexão)
            int[] originDest = getOriginDestination(conn, id);
            if (originDest == null) {
                System.err.println("No path found for freight " + id);
                return null;
            }

            int originId = originDest[0];
            int destId = originDest[1];

            // 3. Buscar wagon numbers (MESMA conexão)
            List<String> wagonNumbers = getWagonNumbers(conn, id);
            if (wagonNumbers.isEmpty()) {
                System.err.println("No wagons found for freight " + id);
                return null;
            }

            // 4. Agora buscar stations (pode usar nova conexão)
            Station origin = stationRepo.getById(originId);
            Station dest = stationRepo.getById(destId);

            if (origin == null || dest == null) {
                System.err.println("Origin or destination station not found for freight " + id);
                return null;
            }

            // 5. Criar freight
            Freight freight = new Freight(
                    id,
                    date,
                    originId,
                    origin.getName(),
                    destId,
                    dest.getName(),
                    wagonNumbers
            );

            freight.setOrigin(origin);
            freight.setDestination(dest);

            // 6. Lazy load wagons
            List<Wagon> wagons = new ArrayList<>();
            for (String wagonNum : wagonNumbers) {
                Wagon wagon = wagonRepo.getById(wagonNum);
                if (wagon != null) {
                    wagons.add(wagon);
                }
            }
            freight.setWagons(wagons);

            return freight;

        } catch (SQLException e) {
            System.err.println("Error loading freight " + id + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Collection<Freight> getAll() {
        Collection<Freight> freights = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT id FROM Freights ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading freight IDs: " + e.getMessage());
            return freights;
        }

        for (Integer id : ids) {
            Freight freight = getById(id);
            if (freight != null) {
                freights.add(freight);
            }
        }

        return freights;
    }

    // Método auxiliar que RECEBE a conexão
    private int[] getOriginDestination(Connection conn, int freightId) {
        String query = """
            SELECT stationId
            FROM Path
            WHERE freightsId = ?
            ORDER BY ROWNUM
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, freightId);
            ResultSet rs = stmt.executeQuery();

            List<Integer> stations = new ArrayList<>();
            while (rs.next()) {
                stations.add(rs.getInt("stationId"));
            }

            if (stations.size() >= 2) {
                return new int[]{stations.get(0), stations.get(stations.size() - 1)};
            }

        } catch (SQLException e) {
            System.err.println("Error loading path for freight " + freightId + ": " + e.getMessage());
        }

        return null;
    }

    // Método auxiliar que RECEBE a conexão
    private List<String> getWagonNumbers(Connection conn, int freightId) {
        List<String> numbers = new ArrayList<>();
        String query = "SELECT wagonNumber FROM WagonFreights WHERE freightsId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, freightId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int wagonNum = rs.getInt("wagonNumber");
                numbers.add(String.valueOf(wagonNum));
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon numbers for freight " + freightId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return numbers;
    }
}