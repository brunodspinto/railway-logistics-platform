package org.example.repository.database;

import org.example.domain.Line;
import org.example.domain.LineSegment;
import org.example.domain.Station;
import org.example.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LineRepository {
    private final StationRepository stationRepo = new StationRepository();

    public Line getById(int lineId) {
        String query = """
            SELECT l.id, l.nameLine, l.ownerLine, l.startStation, 
                   l.endStation, g.measure as gauge_measure
            FROM Line l
            JOIN Gauge g ON l.gaugeId = g.idGauge
            WHERE l.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, lineId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int startId = rs.getInt("startStation");
                int endId = rs.getInt("endStation");
                String name = rs.getString("nameLine");
                String owner = rs.getString("ownerLine");
                int gauge = rs.getInt("gauge_measure");

                Station start = stationRepo.getById(startId);
                Station end = stationRepo.getById(endId);

                if (start == null || end == null) {
                    System.err.println("Start or end station not found for line " + lineId);
                    return null;
                }

                Line line = new Line(lineId, name, owner, start, end, gauge);

                // Carregar segmentos
                List<LineSegment> segments = getSegmentsByLineId(lineId);
                segments.forEach(line::addSegment);

                return line;
            }

        } catch (SQLException e) {
            System.err.println("Error loading line " + lineId + ": " + e.getMessage());
        }

        return null;
    }

    public Line findDirectLine(int originId, int destinationId) {
        String query = """
            SELECT id
            FROM Line
            WHERE startStation = ? AND endStation = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, originId);
            stmt.setInt(2, destinationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return getById(rs.getInt("id"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding direct line: " + e.getMessage());
        }

        return null;
    }

    public Collection<Line> getAll() {
        Collection<Line> lines = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT id FROM Line ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading line IDs: " + e.getMessage());
            return lines;
        }

        for (Integer id : ids) {
            Line line = getById(id);
            if (line != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    public List<LineSegment> getSegmentsByLineId(int lineId) {
        List<LineSegment> segments = new ArrayList<>();
        String query = """
            SELECT ls.id, ls.lineId, ls.segmentOrder, ls.isElectrified,
                   ls.maximumWeigh, ls.lenght, lst.description as track_desc,
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
                String trackDesc = rs.getString("track_desc");
                int numberTracks = trackDesc.contains("double") ? 2 :
                        trackDesc.contains("quadruple") ? 4 : 1;

                Integer sidingPos = rs.getObject("sidingPosition") != null ?
                        ((BigDecimal) rs.getObject("sidingPosition")).intValue() : null;
                Integer sidingLen = rs.getObject("sidingLength") != null ?
                        ((BigDecimal) rs.getObject("sidingLength")).intValue() : null;

                segments.add(new LineSegment(
                        rs.getInt("id"),
                        rs.getInt("lineId"),
                        rs.getInt("segmentOrder"),
                        rs.getInt("isElectrified") == 1,
                        rs.getInt("maximumWeigh"),
                        rs.getInt("lenght"),
                        numberTracks,
                        sidingPos,
                        sidingLen
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading segments for line " + lineId + ": " + e.getMessage());
        }

        return segments;
    }
}
