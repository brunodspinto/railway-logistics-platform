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
                    Date sqlDate = rs.getDate("dateFreights");
                    if (sqlDate != null) {
                        date = sqlDate.toLocalDate();
                    } else {
                        // Fallback se a data for nula na BD (embora seja NOT NULL no script)
                        date = LocalDate.now();
                    }
                } else {
                    System.err.println("Freight " + id + " not found in Freights table.");
                    return null;
                }
            }

            // 2. CORREÇÃO: Buscar origem e destino usando as colunas EXPLICITAS da tabela Path
            int[] originDest = getOriginDestination(conn, id);
            if (originDest == null) {
                // Se não encontrar no Path, tenta ver se existe na tabela Route (para robustez)
                // mas assumindo que o teu script usa Path para Freights:
                System.err.println("No path found for freight " + id);
                return null;
            }

            int originId = originDest[0];
            int destId = originDest[1];

            // 3. Buscar wagon numbers
            List<String> wagonNumbers = getWagonNumbers(conn, id);
            if (wagonNumbers.isEmpty()) {
                System.err.println("No wagons found for freight " + id);
                // Opcional: retornar null ou criar frete sem vagões (mas o domínio proíbe)
                return null;
            }

            // 4. Buscar objetos Station completos
            Station origin = stationRepo.getById(originId);
            Station dest = stationRepo.getById(destId);

            if (origin == null || dest == null) {
                System.err.println("Critical Error: Stations for Freight " + id + " do not exist (Origin:" + originId + ", Dest:" + destId + ")");
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

        // Ordenar por ID garante consistência na UI
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

    // --- CORREÇÃO PRINCIPAL AQUI ---
    private int[] getOriginDestination(Connection conn, int freightId) {
        // Em vez de adivinhar pela ordem das linhas, lemos as colunas 'inicialStation' e 'finalStation'
        // Basta ler 1 linha, pois todas as linhas do mesmo freightId no Path têm a mesma origem/destino globais
        String query = "SELECT inicialStation, finalStation FROM Path WHERE freightsId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, freightId);
            // Usamos setMaxRows para otimizar, pois só precisamos de uma linha
            stmt.setMaxRows(1);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int start = rs.getInt("inicialStation");
                int end = rs.getInt("finalStation");
                return new int[]{start, end};
            }

        } catch (SQLException e) {
            System.err.println("Error loading path info for freight " + freightId + ": " + e.getMessage());
        }

        return null;
    }

    private List<String> getWagonNumbers(Connection conn, int freightId) {
        List<String> numbers = new ArrayList<>();
        String query = "SELECT wagonNumber FROM WagonFreights WHERE freightsId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, freightId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Converter para String porque o construtor de Freight espera List<String>
                // Se na tua classe Wagon o ID for long/int, ajusta conforme necessário.
                // Assumindo que Wagon::getNumber devolve o que está na BD:
                long wagonNum = rs.getLong("wagonNumber");
                numbers.add(String.valueOf(wagonNum));
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon numbers for freight " + freightId + ": " + e.getMessage());
        }

        return numbers;
    }
}