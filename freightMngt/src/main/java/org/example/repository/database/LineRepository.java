package org.example.repository.database;

import org.example.domain.Line;
import org.example.domain.LineSegment;
import org.example.domain.Station;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LineRepository {

    private final StationRepository stationRepo = new StationRepository();

    public LineRepository() {
    }

    public Collection<Line> getAll() {
        List<Line> lines = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        String query = "SELECT id FROM Line ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt("id"));
        } catch (SQLException e) {
            System.err.println("Erro ao buscar IDs: " + e.getMessage());
            return lines;
        }

        // Removi o print de DEBUG aqui para limpar a consola
        for (Integer id : ids) {
            Line line = getById(id);
            if (line != null) lines.add(line);
        }
        return lines;
    }

    public Line getById(int lineId) {
        String query = """
            SELECT l.id, l.nameLine, l.ownerLine, l.startStation, 
                   l.endStation, g.measure as gauge_val
            FROM Line l
            JOIN Gauge g ON l.gaugeId = g.idGauge
            WHERE l.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, lineId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("nameLine");
                String owner = rs.getString("ownerLine");
                int gauge = rs.getInt("gauge_val");
                int startId = rs.getInt("startStation");
                int endId = rs.getInt("endStation");

                Station start = stationRepo.getById(startId);
                Station end = stationRepo.getById(endId);

                if (start != null && end != null) {
                    Line line = new Line(lineId, name, owner, start, end, gauge);
                    List<LineSegment> segments = getSegmentsByLineId(lineId);
                    if (segments != null) segments.forEach(line::addSegment);
                    return line;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL Line " + lineId + ": " + e.getMessage());
        }
        return null;
    }

    // ✅ MÉTODO OTIMIZADO (Resolve o "Loop Infinito")
    // Em vez de carregar tudo, pergunta à BD apenas se existe uma linha entre A e B
    public Line findDirectLine(int originId, int destinationId) {
        String query = """
            SELECT id FROM Line 
            WHERE (startStation = ? AND endStation = ?) 
               OR (startStation = ? AND endStation = ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, originId);
            stmt.setInt(2, destinationId);
            stmt.setInt(3, destinationId);
            stmt.setInt(4, originId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Se encontrar, carrega apenas essa linha
                return getById(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Erro findDirectLine: " + e.getMessage());
        }
        return null;
    }

    public List<LineSegment> getSegmentsByLineId(int lineId) {
        List<LineSegment> segments = new ArrayList<>();
        String query = """
            SELECT ls.id, ls.lineId, ls.segmentOrder, ls.isElectrified,
                   ls.maximumWeigh, ls.lenght, ls.lineSegmentsTypeid,
                   lst.description as track_desc,
                   ls.sidingPosition, ls.sidingLength
            FROM LineSegment ls
            JOIN LineSegmentType lst ON ls.lineSegmentsTypeid = lst.id
            WHERE ls.lineId = ?
            ORDER BY ls.segmentOrder
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, lineId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int numberTracks = 1;
                String trackDesc = rs.getString("track_desc");
                if (trackDesc != null) {
                    if (trackDesc.toLowerCase().contains("double")) numberTracks = 2;
                    else if (trackDesc.toLowerCase().contains("quadruple")) numberTracks = 4;
                }

                Integer sidingPos = null;
                Object objPos = rs.getObject("sidingPosition");
                if (objPos instanceof Number) sidingPos = ((Number) objPos).intValue();

                Integer sidingLen = null;
                Object objLen = rs.getObject("sidingLength");
                if (objLen instanceof Number) sidingLen = ((Number) objLen).intValue();

                segments.add(new LineSegment(
                        rs.getInt("id"),
                        rs.getInt("lineId"),
                        rs.getInt("segmentOrder"),
                        rs.getInt("isElectrified") == 1,
                        rs.getInt("maximumWeigh"),
                        rs.getInt("lenght"),
                        numberTracks,
                        // Se o construtor pedir typeId, descomenta: rs.getInt("lineSegmentsTypeid"),
                        sidingPos,
                        sidingLen
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro segmentos linha " + lineId + ": " + e.getMessage());
        }
        return segments;
    }
}