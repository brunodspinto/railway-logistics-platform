package org.example.domain;

import org.example.exception.QuarantineEmptyException;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Manages the quarantine queue for returned products.
 * Returns are processed in order: timestamp descending (most recent first),
 * then returnId ascending for ties.
 *
 * This class uses a PriorityQueue to maintain the correct processing order
 * based on the natural ordering defined in ReturnRecord.
 */
public class Quarantine {
    private final PriorityQueue<ReturnRecord> queue;
    private final List<ReturnRecord> processedReturns; // For audit/tracking purposes

    /**
     * Creates a new empty Quarantine.
     */
    public Quarantine() {
        // PriorityQueue uses natural ordering of ReturnRecord (timestamp desc, returnId asc)
        this.queue = new PriorityQueue<>();
        this.processedReturns = new ArrayList<>();
    }

    /**
     * Creates a Quarantine and initializes it with a collection of returns.
     *
     * @param returns the initial collection of returns to add
     */
    public Quarantine(List<ReturnRecord> returns) {
        this();
        if (returns != null) {
            returns.forEach(this::add);
        }
    }

    /**
     * Adds a return record to the quarantine queue.
     *
     * @param returnRecord the return to add
     * @throws IllegalArgumentException if returnRecord is null
     */
    public void add(ReturnRecord returnRecord) {
        if (returnRecord == null) {
            throw new IllegalArgumentException("Cannot add null return record to quarantine");
        }
        queue.offer(returnRecord);
    }

    /**
     * Adds multiple return records to the quarantine queue.
     *
     * @param returns the collection of returns to add
     * @throws IllegalArgumentException if returns is null
     */
    public void addAll(List<ReturnRecord> returns) {
        if (returns == null) {
            throw new IllegalArgumentException("Cannot add null collection to quarantine");
        }
        returns.forEach(this::add);
    }

    /**
     * Retrieves and removes the next return to be processed from the queue.
     * Returns are processed in order: most recent first, with returnId as tiebreaker.
     *
     * @return the next ReturnRecord to process
     * @throws QuarantineEmptyException if the queue is empty
     */
    public ReturnRecord poll() {
        if (isEmpty()) {
            throw new QuarantineEmptyException("Cannot poll from empty quarantine queue");
        }

        ReturnRecord record = queue.poll();
        if (record != null) {
            processedReturns.add(record);
        }
        return record;
    }

    /**
     * Retrieves but does not remove the next return to be processed.
     *
     * @return the next ReturnRecord, or null if queue is empty
     */
    public ReturnRecord peek() {
        return queue.peek();
    }

    /**
     * Checks if the quarantine queue is empty.
     *
     * @return true if the queue has no returns waiting for processing
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Gets the number of returns currently waiting in quarantine.
     *
     * @return the size of the queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Gets the number of returns that have been processed.
     *
     * @return the number of processed returns
     */
    public int getProcessedCount() {
        return processedReturns.size();
    }

    /**
     * Gets an unmodifiable view of the processed returns history.
     * This is useful for audit purposes.
     *
     * @return unmodifiable list of processed returns
     */
    public List<ReturnRecord> getProcessedReturns() {
        return Collections.unmodifiableList(processedReturns);
    }

    /**
     * Gets a snapshot of the current queue contents in processing order.
     * This does not modify the queue.
     *
     * @return list of returns in processing order
     */
    public List<ReturnRecord> getQueueSnapshot() {
        List<ReturnRecord> snapshot = new ArrayList<>(queue);
        Collections.sort(snapshot); // Sort according to natural ordering
        return snapshot;
    }

    /**
     * Clears all returns from the quarantine queue.
     * This does not clear the processed returns history.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Clears both the queue and the processed returns history.
     */
    public void clearAll() {
        queue.clear();
        processedReturns.clear();
    }

    /**
     * Checks if a specific return ID has been processed.
     *
     * @param returnId the return ID to check
     * @return true if the return has been processed
     */
    public boolean wasProcessed(String returnId) {
        if (returnId == null) {
            return false;
        }
        return processedReturns.stream()
                .anyMatch(r -> r.getReturnId().equals(returnId));
    }

    /**
     * Checks if a specific return ID is currently in the queue.
     *
     * @param returnId the return ID to check
     * @return true if the return is in the queue
     */
    public boolean isInQueue(String returnId) {
        if (returnId == null) {
            return false;
        }
        return queue.stream()
                .anyMatch(r -> r.getReturnId().equals(returnId));
    }


    public String getStatistics() {
        return String.format("Quarantine: %d pending, %d processed, %d total",
                size(), getProcessedCount(), size() + getProcessedCount());
    }


    @Override
    public String toString() {
        return String.format("Quarantine{pending=%d, processed=%d}",
                queue.size(), processedReturns.size());
    }

    /**
     * Creates a detailed string representation for debugging.
     *
     * @return detailed information about the quarantine state
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Quarantine Status:\n");
        sb.append(String.format("  Pending: %d returns\n", queue.size()));
        sb.append(String.format("  Processed: %d returns\n", processedReturns.size()));

        if (!queue.isEmpty()) {
            sb.append("\n  Next to process:\n");
            List<ReturnRecord> snapshot = getQueueSnapshot();
            int displayCount = Math.min(5, snapshot.size());
            for (int i = 0; i < displayCount; i++) {
                sb.append("    ").append(i + 1).append(". ")
                        .append(snapshot.get(i).toDisplayString()).append("\n");
            }
            if (snapshot.size() > displayCount) {
                sb.append(String.format("    ... and %d more\n", snapshot.size() - displayCount));
            }
        }

        return sb.toString();
    }
}

