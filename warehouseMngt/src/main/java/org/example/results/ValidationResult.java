package org.example.results;

import org.example.domain.Wagon;
import java.util.ArrayList;
import java.util.List;

/**
 * Consolidated result of the complete data import process.
 * Covers: items → bays → wagons → unloading into warehouse.
 */
public class ValidationResult {

    private boolean success = false;
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private int boxesFlaggedForInspection = 0;

    // Import statistics
    private int itemsImported = 0;
    private int baysImported = 0;
    private int wagonsImported = 0;
    private int boxesUnloaded = 0;

    // Detailed results
    private List<Wagon> importedWagons = new ArrayList<>();
    private UnloadingResult unloadingResult;

    // ==================== ERROR HANDLING ====================

    public void addError(String error) {
        this.success = false;
        this.errors.add(error);
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    // ==================== SUCCESS STATUS ====================

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @Deprecated
    public boolean isValid() {
        return success; // Legacy compatibility
    }

    // ==================== IMPORT STATISTICS ====================

    public void setItemsImported(int count) {
        this.itemsImported = count;
    }

    public int getItemsImported() {
        return itemsImported;
    }

    public void setBaysImported(int count) {
        this.baysImported = count;
    }

    public int getBaysImported() {
        return baysImported;
    }

    public void setWagonsImported(int count) {
        this.wagonsImported = count;
    }

    public int getWagonsImported() {
        return wagonsImported;
    }

    public void setBoxesUnloaded(int count) {
        this.boxesUnloaded = count;
    }

    public int getBoxesUnloaded() {
        return boxesUnloaded;
    }

    public void setBoxesFlaggedForInspection(int count) {
        this.boxesFlaggedForInspection = count;
    }

    public int getBoxesFlaggedForInspection() {
        return boxesFlaggedForInspection;
    }

    // ==================== DETAILED RESULTS ====================

    public void setImportedWagons(List<Wagon> wagons) {
        this.importedWagons = wagons != null ? new ArrayList<>(wagons) : new ArrayList<>();
    }

    public List<Wagon> getImportedWagons() {
        return new ArrayList<>(importedWagons);
    }

    public void setUnloadingResult(UnloadingResult result) {
        this.unloadingResult = result;
    }

    public UnloadingResult getUnloadingResult() {
        return unloadingResult;
    }

    // ==================== STRING REPRESENTATION ====================

    @Override
    public String toString() {
        if (!success) {
            StringBuilder sb = new StringBuilder();
            sb.append("╔════════════════════════════════════════════════════╗\n");
            sb.append("║                IMPORT FAILED                       ║\n");
            sb.append("╠════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ Errors: %-43d║\n", errors.size()));
            for (String error : errors) {
                String truncated = error.length() > 48 ? error.substring(0, 45) + "..." : error;
                sb.append(String.format("║ • %-49s║\n", truncated));
            }
            sb.append("╚════════════════════════════════════════════════════╝");
            return sb.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════╗\n");
        sb.append("║             IMPORT SUCCESSFUL                      ║\n");
        sb.append("╠════════════════════════════════════════════════════╣\n");
        sb.append("║ Import Summary:                                    ║\n");
        sb.append(String.format("║   Items      : %-5d imported                      ║\n", itemsImported));
        sb.append(String.format("║   Bays       : %-5d imported                      ║\n", baysImported));
        sb.append(String.format("║   Wagons     : %-5d imported                      ║\n", wagonsImported));
        sb.append(String.format("║   Boxes      : %-5d unloaded into warehouse       ║\n", boxesUnloaded));
        sb.append("╠════════════════════════════════════════════════════╣\n");

        if (boxesFlaggedForInspection > 0) {
            sb.append(String.format("║   Flagged    : %-5d require inspection           ║\n", boxesFlaggedForInspection));
        }
        if (hasWarnings()) {
            sb.append("╠════════════════════════════════════════════════════╣\n");
            sb.append("║ ⚠️  Warnings:                                       ║\n");
            for (String warning : warnings) {
                String truncated = warning.length() > 48 ? warning.substring(0, 45) + "..." : warning;
                sb.append(String.format("║   • %-49s║\n", truncated));
            }
        }

        if (unloadingResult != null) {
            sb.append("║ Unloading Details:                                 ║\n");
            sb.append(String.format("║   Successful : %-3d wagons                        ║\n",
                    unloadingResult.getSuccessfulWagons()));
            sb.append(String.format("║   Failed     : %-3d wagons                        ║\n",
                    unloadingResult.getFailedWagons()));

            if (unloadingResult.hasErrors()) {
                sb.append("║      Some wagons failed to unload                 ║\n");
            }
        }

        sb.append("╚════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    /**
     * Returns a concise summary for logging.
     */
    public String getSummary() {
        if (!success) {
            return String.format("Import failed with %d error(s)", errors.size());
        }
        return String.format("Import complete: %d items, %d bays, %d wagons (%d boxes unloaded)",
                itemsImported, baysImported, wagonsImported, boxesUnloaded);
    }
}
