package org.example.repository.database;

import org.example.domain.*;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TrainRepository {
    private final StationRepository stationRepo = new StationRepository();
    private final LocomotiveRepository locomotiveRepo = new LocomotiveRepository();
    private final FreightRepository freightRepo = new FreightRepository();

    public Train getById(int id) {
        String query = """
            SELECT t.id, t.dateTrain, t.timeTrain,
                   ot.operatorVatNumber as operator
            FROM Train t
            JOIN OperatorTrain ot ON t.id = ot.trainId
            WHERE t.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDate date = rs.getDate("dateTrain").toLocalDate();
                Time time = rs.getTime("timeTrain");
                LocalTime localTime = time != null ? time.toLocalTime() : LocalTime.MIDNIGHT;
                String operator = rs.getString("operator");

                // Buscar path stations
                List<Integer> pathStationIds = getPathStationIds(conn, id);

                // ✅ FIX: Se path vazio, criar train SEM path (em vez de retornar null)
                if (pathStationIds.isEmpty()) {
                    // Train sem route definida (ainda não agendado)
                    Train train = new Train(
                            id,
                            operator,
                            date,
                            localTime,
                            0,  // startId temporário
                            0,  // endId temporário
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>()
                    );
                    return train;
                }

                if (pathStationIds.size() < 2) {
                    // Path incompleto, mas não falha
                    // System.err.println("Incomplete path for train " + id);
                    // Continua mesmo assim
                }

                int startId = pathStationIds.isEmpty() ? 0 : pathStationIds.get(0);
                int endId = pathStationIds.isEmpty() ? 0 : pathStationIds.get(pathStationIds.size() - 1);

                // Buscar freight IDs e locomotive numbers
                List<Integer> freightIds = getFreightIds(conn, id);
                List<Integer> locoNumbers = getLocomotiveNumbers(conn, id);

                Train train = new Train(
                        id,
                        operator,
                        date,
                        localTime,
                        startId,
                        endId,
                        freightIds,
                        locoNumbers,
                        pathStationIds
                );

                // Lazy load (só se stations existem)
                if (startId > 0) {
                    train.setStartStation(stationRepo.getById(startId));
                }
                if (endId > 0) {
                    train.setEndStation(stationRepo.getById(endId));
                }

                List<Freight> freights = new ArrayList<>();
                for (int fid : freightIds) {
                    Freight f = freightRepo.getById(fid);
                    if (f != null) freights.add(f);
                }
                train.setFreights(freights);

                List<Locomotive> locos = new ArrayList<>();
                for (int num : locoNumbers) {
                    Locomotive l = locomotiveRepo.getById(num);
                    if (l != null) locos.add(l);
                }
                train.setLocomotives(locos);

                List<Station> pathStations = new ArrayList<>();
                for (int sid : pathStationIds) {
                    Station s = stationRepo.getById(sid);
                    if (s != null) pathStations.add(s);
                }
                train.setPathStations(pathStations);

                return train;
            }

        } catch (SQLException e) {
            System.err.println("Error loading train " + id + ": " + e.getMessage());
        }

        return null;
    }

    public Collection<Train> getAll() {
        Collection<Train> trains = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT id FROM Train ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading train IDs: " + e.getMessage());
            return trains;
        }

        for (Integer id : ids) {
            Train train = getById(id);
            if (train != null) {
                trains.add(train);
            }
        }

        return trains;
    }

    private List<Integer> getPathStationIds(Connection conn, int trainId) {
        List<Integer> ids = new ArrayList<>();

        String query = """
        SELECT stationId
        FROM Route
        WHERE trainId = ?
        ORDER BY ROWID
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("stationId"));
            }

        } catch (SQLException e) {
            // ✅ FIX: NÃO imprimir erro (Path pode não existir ainda)
            // System.err.println("Erro Crítico: Não foi possível ler a tabela Path.");
            // Path vazio = train ainda não tem route definida
        }

        return ids;
    }

    private List<Integer> getFreightIds(Connection conn, int trainId) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT id FROM Freights WHERE trainId = ? ORDER BY id";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            // Silencioso - freight pode não existir
        }

        return ids;
    }

    private List<Integer> getLocomotiveNumbers(Connection conn, int trainId) {
        List<Integer> numbers = new ArrayList<>();
        String query = "SELECT locomotiveNumber FROM Locomotive_Train WHERE trainId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                numbers.add(rs.getInt("locomotiveNumber"));
            }

        } catch (SQLException e) {
            // Silencioso - locos podem não estar associadas ainda
        }

        return numbers;
    }

    public List<Train> getByDate(LocalDate date) {
        List<Integer> trainIds = new ArrayList<>();

        String query = """
        SELECT t.id
        FROM Train t
        WHERE t.dateTrain = ?
        ORDER BY t.timeTrain
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trainIds.add(rs.getInt("id"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading train IDs by date: " + e.getMessage());
            return new ArrayList<>();
        }

        List<Train> trains = new ArrayList<>();
        for (Integer id : trainIds) {
            Train train = getById(id);
            if (train != null) {
                trains.add(train);
            }
        }

        System.out.printf("✓ Loaded %d trains for date %s\n", trains.size(), date);
        return trains;
    }
}