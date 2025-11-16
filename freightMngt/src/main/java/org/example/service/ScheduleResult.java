package org.example.service;

import org.example.domain.CrossingOperation;

import java.util.*;

/**
 * Resultado completo do scheduling (schedules + crossings)
 */
public class ScheduleResult {
    private final List<TrainSchedule> schedules;
    private final List<CrossingOperation> crossings;

    public ScheduleResult(List<TrainSchedule> schedules,
                          List<CrossingOperation> crossings) {
        this.schedules = new ArrayList<>(schedules);
        this.crossings = new ArrayList<>(crossings);
    }

    public List<TrainSchedule> getSchedules() {
        return Collections.unmodifiableList(schedules);
    }

    public List<CrossingOperation> getCrossings() {
        return Collections.unmodifiableList(crossings);
    }

    public boolean hasCrossings() {
        return !crossings.isEmpty();
    }

    public String formatCrossings() {
        if (crossings.isEmpty()) {
            return "✅ No crossings required - all trains can proceed without conflicts";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(100)).append("\n");
        sb.append("CROSSING OPERATIONS REQUIRED\n");
        sb.append("═".repeat(100)).append("\n\n");

        for (int i = 0; i < crossings.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, crossings.get(i).format()));
        }

        sb.append("\n").append("═".repeat(100)).append("\n");

        return sb.toString();
    }
}

