package org.example.results;

public class ProcessingResult {
    private final int totalProcessed;
    private final int restockedCount;
    private final int discardedCount;
    private final int partialRestockCount;
    private final int errorsCount;

    public ProcessingResult(int totalProcessed, int restockedCount, int discardedCount,
                            int partialRestockCount, int errorsCount) {
        this.totalProcessed = totalProcessed;
        this.restockedCount = restockedCount;
        this.discardedCount = discardedCount;
        this.partialRestockCount = partialRestockCount;
        this.errorsCount = errorsCount;
    }

    public int getTotalProcessed() { return totalProcessed; }
    public int getRestockedCount() { return restockedCount; }
    public int getDiscardedCount() { return discardedCount; }
    public int getPartialRestockCount() { return partialRestockCount; }
    public int getErrorsCount() { return errorsCount; }

    @Override
    public String toString() {
        return String.format("""
                Processing Summary:
                  Total processed: %d
                  Restocked: %d
                  Discarded: %d
                  Partial restocks: %d
                  Errors: %d
                """, totalProcessed, restockedCount, discardedCount,
                partialRestockCount, errorsCount);
    }
}
