package org.example.service;

import org.example.domain.Quarantine;
import org.example.domain.ReturnRecord;
import org.example.exception.QuarantineEmptyException;
import org.example.exception.ValidationException;
import org.example.CsvReaders.ReturnsCsvParser;
import org.example.repository.ItemRepository;

import java.util.List;

/**
 * Service for managing the quarantine queue operations.
 * Handles loading returns from CSV, adding them to quarantine,
 * and retrieving returns for processing in the correct order (FIFO).
 *
 * Returns are processed oldest-first (timestamp ascending), with returnId
 * as tiebreaker (ascending).
 */
public class QuarantineService {

    private final Quarantine quarantine;
    private final ItemRepository itemRepository;

    /**
     * Creates a QuarantineService with required dependencies.
     *
     * @param itemRepository repository for item/SKU validation
     * @throws IllegalArgumentException if itemRepository is null
     */
    public QuarantineService(ItemRepository itemRepository) {
        if (itemRepository == null) {
            throw new IllegalArgumentException("ItemRepository cannot be null");
        }

        this.quarantine = new Quarantine();
        this.itemRepository = itemRepository;
    }

    /**
     * Imports returns from a CSV file and loads them into quarantine.
     * The file is validated and all valid returns are added to the quarantine queue.
     *
     * @param filePath path to the returns.csv file
     * @return number of returns successfully loaded
     * @throws ValidationException if file cannot be read or contains validation errors
     */
    public int importReturnsFromCsv(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new ValidationException("File path cannot be null or empty");
        }

        // Parse CSV file
        ReturnsCsvParser parser = new ReturnsCsvParser(itemRepository);
        List<ReturnRecord> returns = parser.parse(filePath);

        // Load returns into quarantine
        quarantine.addAll(returns);

        return returns.size();
    }

    /**
     * Adds a single return record to the quarantine.
     *
     * @param returnRecord the return to add
     * @throws ValidationException if returnRecord is null
     */
    public void addReturn(ReturnRecord returnRecord) {
        if (returnRecord == null) {
            throw new ValidationException("Cannot add null return record");
        }

        quarantine.add(returnRecord);
    }

    /**
     * Adds multiple return records to the quarantine.
     *
     * @param returns list of returns to add
     * @throws ValidationException if returns is null
     */
    public void addReturns(List<ReturnRecord> returns) {
        if (returns == null) {
            throw new ValidationException("Cannot add null returns list");
        }

        quarantine.addAll(returns);
    }

    /**
     * Retrieves and removes the next return to be processed from quarantine.
     * Returns are processed in FIFO order: oldest first (timestamp ascending),
     * with returnId ascending as tiebreaker.
     *
     * @return the next ReturnRecord to process
     * @throws QuarantineEmptyException if quarantine is empty
     */
    public ReturnRecord getNextReturn() {
        return quarantine.poll(); // Throws QuarantineEmptyException if empty
    }

    /**
     * Retrieves but does not remove the next return to be processed.
     * Useful for previewing what will be processed next.
     *
     * @return the next ReturnRecord, or null if quarantine is empty
     */
    public ReturnRecord peekNextReturn() {
        return quarantine.peek();
    }

    /**
     * Checks if there are any returns waiting in quarantine.
     *
     * @return true if at least one return is in the queue
     */
    public boolean hasReturns() {
        return !quarantine.isEmpty();
    }

    /**
     * Gets the number of returns currently waiting in quarantine.
     *
     * @return the count of pending returns
     */
    public int getPendingCount() {
        return quarantine.size();
    }

    /**
     * Gets the number of returns that have been processed.
     *
     * @return the count of processed returns
     */
    public int getProcessedCount() {
        return quarantine.getProcessedCount();
    }

    /**
     * Gets the total number of returns (pending + processed).
     *
     * @return total count
     */
    public int getTotalCount() {
        return getPendingCount() + getProcessedCount();
    }

    /**
     * Processes all returns in the quarantine queue using a provided processor.
     * This is a helper method that polls returns one by one and applies
     * the provided processing logic.
     *
     * @param processor functional interface to process each return
     * @return number of returns processed
     * @throws IllegalArgumentException if processor is null
     */
    public int processAllReturns(ReturnProcessor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("Processor cannot be null");
        }

        int processedCount = 0;
        while (hasReturns()) {
            ReturnRecord returnRecord = getNextReturn();
            processor.process(returnRecord);
            processedCount++;
        }

        return processedCount;
    }

    /**
     * Clears all pending returns from the quarantine queue.
     * Processed returns history is preserved.
     */
    public void clearPending() {
        quarantine.clear();
    }

    /**
     * Resets the quarantine completely (both pending and processed).
     */
    public void reset() {
        quarantine.clearAll();
    }

    /**
     * Gets a summary of the current quarantine state.
     *
     * @return formatted string with statistics
     */
    public String getSummary() {
        return quarantine.getStatistics();
    }

    /**
     * Gets detailed information about the quarantine state,
     * including a preview of the next returns to be processed.
     *
     * @return detailed formatted string
     */
    public String getDetailedSummary() {
        return quarantine.toDetailedString();
    }

    /**
     * Gets a snapshot of the current queue in processing order.
     * Useful for displaying pending returns without modifying the queue.
     *
     * @return list of returns in processing order
     */
    public List<ReturnRecord> getQueueSnapshot() {
        return quarantine.getQueueSnapshot();
    }

    /**
     * Gets the history of processed returns.
     * Useful for audit purposes.
     *
     * @return unmodifiable list of processed returns
     */
    public List<ReturnRecord> getProcessedReturns() {
        return quarantine.getProcessedReturns();
    }

    /**
     * Checks if a specific return ID has been processed.
     *
     * @param returnId the return ID to check
     * @return true if the return has been processed
     */
    public boolean wasProcessed(String returnId) {
        return quarantine.wasProcessed(returnId);
    }

    /**
     * Checks if a specific return ID is currently in the queue.
     *
     * @param returnId the return ID to check
     * @return true if the return is in the queue
     */
    public boolean isPending(String returnId) {
        return quarantine.isInQueue(returnId);
    }

    /**
     * Gets direct access to the quarantine instance.
     * Use with caution - prefer using service methods.
     *
     * @return the quarantine instance
     */
    public Quarantine getQuarantine() {
        return quarantine;
    }

    /**
     * Functional interface for processing return records.
     * Used with processAllReturns method.
     */
    @FunctionalInterface
    public interface ReturnProcessor {
        /**
         * Processes a single return record.
         *
         * @param returnRecord the return to process
         */
        void process(ReturnRecord returnRecord);
    }
}