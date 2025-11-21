package org.example.repository;

import org.example.domain.*;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Implementação do repositório usando Oracle Database
 * Alternativa ao CsvRouteRepository
 */
public class DatabaseRouteRepository implements IRouteRepository {

    // ===== STATIONS =====

    @Override
    public Station getStation(int id) {
        String query = "SELECT station_id, name FROM STATION WHERE station_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Station(
                        rs.getInt("station_id"),
                        rs.getString("name")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error loading station " + id + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        String query = "SELECT station_id, name FROM STATION ORDER BY station_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stations.add(new Station(
                        rs.getInt("station_id"),
                        rs.getString("name")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading stations: " + e.getMessage());
        }

        return stations;
    }

    // ===== LINES =====

    @Override
    public Line getLineById(int lineId) {
        String query = """
            SELECT l.line_id, l.name, l.owner, l.start_station_id, 
                   l.end_station_id, l.gauge_mm
            FROM LINE l
            WHERE l.line_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, lineId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Carregar estações
                Station start = getStation(rs.getInt("start_station_id"));
                Station end = getStation(rs.getInt("end_station_id"));

                if (start == null || end == null) {
                    System.err.println("Start or end station not found for line " + lineId);
                    return null;
                }

                Line line = new Line(
                        rs.getInt("line_id"),
                        rs.getString("name"),
                        rs.getString("owner"),
                        start,
                        end,
                        rs.getInt("gauge_mm")
                );

                // Carregar segmentos da linha
                List<LineSegment> segments = getSegmentsByLine(lineId);
                segments.forEach(line::addSegment);

                return line;
            }

        } catch (SQLException e) {
            System.err.println("Error loading line " + lineId + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public Line findDirectLine(int originId, int destinationId) {
        String query = """
            SELECT DISTINCT l.line_id
            FROM LINE l
            WHERE l.start_station_id = ? AND l.end_station_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, originId);
            stmt.setInt(2, destinationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return getLineById(rs.getInt("line_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding direct line: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<Line> getAllLines() {
        List<Line> lines = new ArrayList<>();
        String query = "SELECT line_id FROM LINE ORDER BY line_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Line line = getLineById(rs.getInt("line_id"));
                if (line != null) {
                    lines.add(line);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading lines: " + e.getMessage());
        }

        return lines;
    }

    // ===== LINE SEGMENTS =====

    @Override
    public List<LineSegment> getSegmentsByLine(int lineId) {
        List<LineSegment> segments = new ArrayList<>();
        String query = """
            SELECT segment_id, line_id, segment_order, is_electrified,
                   max_weight_kg_m, length_m, number_tracks,
                   siding_position_m, siding_length_m
            FROM LINE_SEGMENT
            WHERE line_id = ?
            ORDER BY segment_order
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, lineId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer sidingPos = (Integer) rs.getObject("siding_position_m");
                Integer sidingLen = (Integer) rs.getObject("siding_length_m");

                segments.add(new LineSegment(
                        rs.getInt("segment_id"),
                        rs.getInt("line_id"),
                        rs.getInt("segment_order"),
                        rs.getInt("is_electrified") == 1,
                        rs.getInt("max_weight_kg_m"),
                        rs.getInt("length_m"),
                        rs.getInt("number_tracks"),
                        sidingPos,
                        sidingLen
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading segments for line " + lineId + ": " + e.getMessage());
        }

        return segments;
    }

    // ===== LOCOMOTIVES =====

    @Override
    public Locomotive getLocomotive(int number) {
        String query = """
            SELECT number, name, make, model, service_year, power_kw,
                   length_m, weight_tons, max_speed_kmh, operational_speed_kmh,
                   type, gauge_mm, fuel_capacity_l
            FROM LOCOMOTIVE
            WHERE number = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, number);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer fuelCapacity = (Integer) rs.getObject("fuel_capacity_l");

                return new Locomotive(
                        rs.getInt("number"),
                        rs.getString("name"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("service_year"),
                        rs.getInt("power_kw"),
                        rs.getDouble("length_m"),
                        rs.getDouble("weight_tons"),
                        rs.getInt("max_speed_kmh"),
                        rs.getInt("operational_speed_kmh"),
                        rs.getString("type"),
                        rs.getInt("gauge_mm"),
                        fuelCapacity
                );
            }

        } catch (SQLException e) {
            System.err.println("Error loading locomotive " + number + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<Locomotive> getAllLocomotives() {
        List<Locomotive> locomotives = new ArrayList<>();
        String query = "SELECT number FROM LOCOMOTIVE ORDER BY number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Locomotive loco = getLocomotive(rs.getInt("number"));
                if (loco != null) {
                    locomotives.add(loco);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading locomotives: " + e.getMessage());
        }

        return locomotives;
    }

    // ===== WAGON MODELS =====

    @Override
    public WagonModel getWagonModel(int id) {
        String query = """
            SELECT id, model, maker, number_bogies, bogies_type,
                   length_mm, width_mm, height_mm, weight_tons,
                   max_speed_kmh, payload_tons, volume_m3, type, gauge_mm
            FROM WAGON_MODEL
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new WagonModel(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("maker"),
                        rs.getInt("number_bogies"),
                        rs.getString("bogies_type"),
                        rs.getInt("length_mm"),
                        rs.getInt("width_mm"),
                        rs.getInt("height_mm"),
                        rs.getDouble("weight_tons"),
                        rs.getInt("max_speed_kmh"),
                        rs.getDouble("payload_tons"),
                        rs.getDouble("volume_m3"),
                        rs.getString("type"),
                        rs.getInt("gauge_mm")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon model " + id + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<WagonModel> getAllWagonModels() {
        List<WagonModel> models = new ArrayList<>();
        String query = "SELECT id FROM WAGON_MODEL ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                WagonModel model = getWagonModel(rs.getInt("id"));
                if (model != null) {
                    models.add(model);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon models: " + e.getMessage());
        }

        return models;
    }

    // ===== WAGONS =====

    @Override
    public Wagon getWagon(String number) {
        String query = """
            SELECT number, model_id, operator, service_year
            FROM WAGON
            WHERE number = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, number);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Wagon wagon = new Wagon(
                        rs.getString("number"),
                        rs.getInt("model_id"),
                        rs.getString("operator"),
                        rs.getInt("service_year")
                );

                // Lazy load do model
                WagonModel model = getWagonModel(wagon.getModelId());
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

    @Override
    public Collection<Wagon> getAllWagons() {
        List<Wagon> wagons = new ArrayList<>();
        String query = "SELECT number FROM WAGON ORDER BY number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Wagon wagon = getWagon(rs.getString("number"));
                if (wagon != null) {
                    wagons.add(wagon);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagons: " + e.getMessage());
        }

        return wagons;
    }

    // ===== FREIGHTS =====

    @Override
    public Freight getFreight(int id) {
        String query = """
            SELECT f.id, f.date, f.origin_id, s1.name as origin_name,
                   f.destination_id, s2.name as destination_name
            FROM FREIGHT f
            JOIN STATION s1 ON f.origin_id = s1.station_id
            JOIN STATION s2 ON f.destination_id = s2.station_id
            WHERE f.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Buscar números de vagões
                List<String> wagonNumbers = getFreightWagonNumbers(id);

                Freight freight = new Freight(
                        rs.getInt("id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("origin_id"),
                        rs.getString("origin_name"),
                        rs.getInt("destination_id"),
                        rs.getString("destination_name"),
                        wagonNumbers
                );

                // Lazy load das estações
                freight.setOrigin(getStation(freight.getOriginId()));
                freight.setDestination(getStation(freight.getDestinationId()));

                // Lazy load dos wagons
                List<Wagon> wagons = new ArrayList<>();
                for (String wagonNum : wagonNumbers) {
                    Wagon wagon = getWagon(wagonNum);
                    if (wagon != null) {
                        wagons.add(wagon);
                    }
                }
                freight.setWagons(wagons);

                return freight;
            }

        } catch (SQLException e) {
            System.err.println("Error loading freight " + id + ": " + e.getMessage());
        }

        return null;
    }

    private List<String> getFreightWagonNumbers(int freightId) {
        List<String> numbers = new ArrayList<>();
        String query = "SELECT wagon_number FROM FREIGHT_WAGON WHERE freight_id = ? ORDER BY position";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, freightId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                numbers.add(rs.getString("wagon_number"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading wagon numbers for freight " + freightId + ": " + e.getMessage());
        }

        return numbers;
    }

    @Override
    public Collection<Freight> getAllFreights() {
        List<Freight> freights = new ArrayList<>();
        String query = "SELECT id FROM FREIGHT ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Freight freight = getFreight(rs.getInt("id"));
                if (freight != null) {
                    freights.add(freight);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading freights: " + e.getMessage());
        }

        return freights;
    }

    // ===== TRAINS =====

    @Override
    public Train getTrain(int id) {
        String query = """
            SELECT id, operator, departure_date, departure_time,
                   start_station_id, end_station_id
            FROM TRAIN
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Buscar freights, locomotivas e path
                List<Integer> freightIds = getTrainFreightIds(id);
                List<Integer> locoNumbers = getTrainLocomotiveNumbers(id);
                List<Integer> pathStationIds = getTrainPathStationIds(id);

                Train train = new Train(
                        rs.getInt("id"),
                        rs.getString("operator"),
                        rs.getDate("departure_date").toLocalDate(),
                        rs.getTime("departure_time").toLocalTime(),
                        rs.getInt("start_station_id"),
                        rs.getInt("end_station_id"),
                        freightIds,
                        locoNumbers,
                        pathStationIds
                );

                // Lazy load
                train.setStartStation(getStation(train.getStartId()));
                train.setEndStation(getStation(train.getEndId()));

                List<Freight> freights = new ArrayList<>();
                for (int fid : freightIds) {
                    Freight f = getFreight(fid);
                    if (f != null) freights.add(f);
                }
                train.setFreights(freights);

                List<Locomotive> locos = new ArrayList<>();
                for (int num : locoNumbers) {
                    Locomotive l = getLocomotive(num);
                    if (l != null) locos.add(l);
                }
                train.setLocomotives(locos);

                List<Station> pathStations = new ArrayList<>();
                for (int sid : pathStationIds) {
                    Station s = getStation(sid);
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

    private List<Integer> getTrainFreightIds(int trainId) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT freight_id FROM TRAIN_FREIGHT WHERE train_id = ? ORDER BY position";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("freight_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading freight IDs for train " + trainId + ": " + e.getMessage());
        }

        return ids;
    }

    private List<Integer> getTrainLocomotiveNumbers(int trainId) {
        List<Integer> numbers = new ArrayList<>();
        String query = "SELECT locomotive_number FROM TRAIN_LOCOMOTIVE WHERE train_id = ? ORDER BY position";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                numbers.add(rs.getInt("locomotive_number"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading locomotive numbers for train " + trainId + ": " + e.getMessage());
        }

        return numbers;
    }

    private List<Integer> getTrainPathStationIds(int trainId) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT station_id FROM TRAIN_PATH WHERE train_id = ? ORDER BY sequence_order";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("station_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading path station IDs for train " + trainId + ": " + e.getMessage());
        }

        return ids;
    }

    @Override
    public Collection<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT id FROM TRAIN ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Train train = getTrain(rs.getInt("id"));
                if (train != null) {
                    trains.add(train);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading trains: " + e.getMessage());
        }

        return trains;
    }
}