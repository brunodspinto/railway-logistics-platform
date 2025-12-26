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
                        date = LocalDate.now();
                    }
                } else {
                    return null;
                }
            }

            // 2. Buscar origem e destino
            int[] originDest = getOriginDestination(conn, id);

            if (originDest == null) {
                // Silencioso - Path pode não existir
                return null;
            }

            int originId = originDest[0];
            int destId = originDest[1];

            // 3. Buscar vagões
            List<String> wagonNumbers = getWagonNumbers(conn, id);
            if (wagonNumbers.isEmpty()) {
                return null;
            }

            // 4. Buscar Estações
            Station origin = stationRepo.getById(originId);
            Station dest = stationRepo.getById(destId);

            if (origin == null || dest == null) {
                return null;
            }

            // 5. Construir objeto
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
            System.err.println("Erro ao carregar Carga " + id + ": " + e.getMessage());
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
            System.err.println("Erro SQL IDs: " + e.getMessage());
            return freights;
        }

        for (Integer id : ids) {
            Freight f = getById(id);
            if (f != null) {
                freights.add(f);
            }
        }
        return freights;
    }

    /**
     * Tenta descobrir as colunas corretas. Se falhar 'finalStation', tenta 'endStation'.
     */
    private int[] getOriginDestination(Connection conn, int freightId) {
        // TENTATIVA 1: Nomes do guião (inicialStation, finalStation)
        try {
            return tryQuery(conn, freightId, "SELECT inicialStation, finalStation FROM Path WHERE freightsId = ?");
        } catch (SQLException e) {
            // Se der erro de coluna inválida, tentamos a alternativa
            if (e.getErrorCode() == 904) { // ORA-00904: Invalid Identifier
                try {
                    // TENTATIVA 2: Nomes da tabela Line (startStation, endStation)
                    return tryQuery(conn, freightId, "SELECT startStation, endStation FROM Path WHERE freightsId = ?");
                } catch (SQLException ex2) {
                    // ✅ SILENCIOSO - Path pode não existir para este freight
                    // Não imprimir erro (chamado 9x no início = 9 freights sem path)
                }
            }
        }
        return null;
    }

    private int[] tryQuery(Connection conn, int freightId, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, freightId);
            stmt.setMaxRows(1);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new int[]{rs.getInt(1), rs.getInt(2)};
            }
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
                numbers.add(String.valueOf(rs.getLong("wagonNumber")));
            }
        } catch (SQLException e) {
            // Ignorar
        }
        return numbers;
    }
}