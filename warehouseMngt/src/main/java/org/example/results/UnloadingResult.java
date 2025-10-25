package org.example.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of wagon unloading operations (USEI01 Acceptance Criteria #1).
 * Tracks successful and failed wagon unloads.
 */
public class UnloadingResult {

    private final Map<String, Integer> successfulUnloads = new HashMap<>();
    private final Map<String, String> errors = new HashMap<>();
    private int totalBoxesUnloaded = 0;

    /**
     * Records a successful wagon unload.
     *
     * @param wagonId the ID of the wagon
     * @param boxesCount number of boxes unloaded from this wagon
     */
    public void addSuccess(String wagonId, int boxesCount) {
        successfulUnloads.put(wagonId, boxesCount);
        totalBoxesUnloaded += boxesCount;
    }

    /**
     * Records a failed wagon unload.
     *
     * @param wagonId the ID of the wagon
     * @param error the error message
     */
    public void addError(String wagonId, String error) {
        errors.put(wagonId, error);
    }

    // ==================== GETTERS ====================

    public Map<String, Integer> getSuccessfulUnloads() {
        return new HashMap<>(successfulUnloads);
    }

    public Map<String, String> getErrors() {
        return new HashMap<>(errors);
    }

    public int getSuccessfulWagons() {
        return successfulUnloads.size();
    }

    public int getFailedWagons() {
        return errors.size();
    }

    /**
     * Returns the number of successfully unloaded wagons.
     * Alias for getSuccessfulWagons() to match test expectations.
     */
    public int getSuccessCount() {
        return getSuccessfulWagons();
    }

    /**
     * Returns the number of errors/failed wagons.
     * Alias for getFailedWagons() to match test expectations.
     */
    public int getErrorCount() {
        return getFailedWagons();
    }

    public int getTotalWagons() {
        return successfulUnloads.size() + errors.size();
    }

    public int getTotalBoxesUnloaded() {
        return totalBoxesUnloaded;
    }

    // ==================== STATUS CHECKERS ====================

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean isFullSuccess() {
        return errors.isEmpty() && !successfulUnloads.isEmpty();
    }

    public boolean isPartialSuccess() {
        return !errors.isEmpty() && !successfulUnloads.isEmpty();
    }

    public boolean isCompleteFailure() {
        return successfulUnloads.isEmpty() && !errors.isEmpty();
    }

    public double getSuccessRate() {
        int total = getTotalWagons();
        return total > 0 ? (double) successfulUnloads.size() / total : 0.0;
    }

    // ==================== STRING REPRESENTATION ====================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════╗\n");
        sb.append("║           WAGON UNLOADING RESULTS                  ║\n");
        sb.append("╠════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Total Wagons    : %-3d                             ║\n", getTotalWagons()));
        sb.append(String.format("║ Successful      : %-3d (%.1f%%)                     ║\n",
                getSuccessfulWagons(), getSuccessRate() * 100));
        sb.append(String.format("║ Failed          : %-3d                             ║\n", getFailedWagons()));
        sb.append(String.format("║ Total Boxes     : %-5d boxes unloaded             ║\n", totalBoxesUnloaded));
        sb.append("╠════════════════════════════════════════════════════╣\n");

        if (!successfulUnloads.isEmpty()) {
            sb.append("║ ✅ Successful Unloads:                             ║\n");
            for (Map.Entry<String, Integer> entry : successfulUnloads.entrySet()) {
                sb.append(String.format("║   - %-20s : %3d boxes          ║\n",
                        entry.getKey(), entry.getValue()));
            }
        }

        if (hasErrors()) {
            sb.append("╠════════════════════════════════════════════════════╣\n");
            sb.append("║ ❌ Errors:                                          ║\n");
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                String errorMsg = entry.getValue();
                if (errorMsg.length() > 42) {
                    errorMsg = errorMsg.substring(0, 39) + "...";
                }
                sb.append(String.format("║   - %-20s : %-23s║\n",
                        entry.getKey(), errorMsg));
            }
        }

        sb.append("╚════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    /**
     * Returns a simple summary line.
     */
    public String getSummary() {
        return String.format("%d/%d wagons unloaded successfully (%d boxes total)",
                getSuccessfulWagons(), getTotalWagons(), totalBoxesUnloaded);
    }
}
