package org.example.domain;

/**
 * Enum representing the possible reasons for product returns.
 * Each reason has an associated flag indicating whether the returned
 * product can potentially be restocked into inventory.
 */
public enum ReturnReason {
    /**
     * Customer returned the item due to remorse (change of mind).
     * Can be restocked if product is in good condition.
     */
    CUSTOMER_REMORSE(true),

    /**
     * Wrong item was delivered or picked by mistake.
     * Can be restocked if product is in good condition.
     */
    WRONG_ITEM(true),

    /**
     * Package was opened but product may still be in good condition.
     * Cannot be restocked due to hygiene/safety policies.
     */
    PACKAGE_OPENED(false),

    /**
     * The product is physically damaged or defective.
     * Cannot be restocked.
     */
    DAMAGED(false),

    /**
     * The product has passed its expiry date.
     * Cannot be restocked.
     */
    EXPIRED(false);

    private final boolean restockable;

    /**
     * Constructor for ReturnReason.
     *
     * @param restockable whether items with this return reason can be restocked
     */
    ReturnReason(boolean restockable) {
        this.restockable = restockable;
    }

    /**
     * Determines if products with this return reason can potentially be restocked.
     * Note: Even if a reason is restockable, the actual decision may depend on
     * other factors like product condition during inspection (e.g., expiry date).
     *
     * @return true if the reason allows for potential restocking, false otherwise
     */
    public boolean isRestockable() {
        return restockable;
    }

    /**
     * Parses a string value to the corresponding ReturnReason enum.
     * This method is case-insensitive and handles underscores or spaces.
     *
     * @param value the string value to parse
     * @return the corresponding ReturnReason
     * @throws IllegalArgumentException if the value doesn't match any ReturnReason
     */
    public static ReturnReason fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Return reason cannot be null or empty");
        }

        // Normalize the input: uppercase and replace spaces with underscores
        String normalized = value.trim().toUpperCase().replace(" ", "_");

        try {
            return ReturnReason.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid return reason: '%s'. Valid values are: %s",
                            value,
                            java.util.Arrays.toString(ReturnReason.values()))
            );
        }
    }

    @Override
    public String toString() {
        // Convert enum name to lowercase with spaces for better readability
        return name().toLowerCase().replace("_", " ");
    }
}
