package org.example.domain;

import java.util.Map;
import java.util.HashMap;

public enum ReturnReason {
    CUSTOMER_REMORSE(true),
    DAMAGED(false),
    EXPIRED(false),
    CYCLE_COUNT(true); // adicionado

    private final boolean restockable;

    ReturnReason(boolean restockable) {
        this.restockable = restockable;
    }

    public boolean isRestockable() {
        return restockable;
    }

    private static final Map<String, ReturnReason> ALIASES = new HashMap<>();

    static {
        for (ReturnReason r : values()) {
            ALIASES.put(r.name(), r);
        }
        // aliases / sinónimos / variações comuns (lowercase key)
        ALIASES.put("customer-remorse", CUSTOMER_REMORSE);
        ALIASES.put("customer remorse", CUSTOMER_REMORSE);
        ALIASES.put("customer_remorse", CUSTOMER_REMORSE);
        ALIASES.put("customerremorse", CUSTOMER_REMORSE);

        ALIASES.put("cycle-count", CYCLE_COUNT);
        ALIASES.put("cycle count", CYCLE_COUNT);
        ALIASES.put("cycle_count", CYCLE_COUNT);
        ALIASES.put("cyclecount", CYCLE_COUNT);

        ALIASES.put("damaged", DAMAGED);
        ALIASES.put("expired", EXPIRED);
    }

    public static ReturnReason fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Return reason cannot be null");
        }
        String normalized = value.trim().toLowerCase();
        // map direct
        if (ALIASES.containsKey(normalized.toUpperCase())) {
            return ALIASES.get(normalized.toUpperCase());
        }
        if (ALIASES.containsKey(normalized)) {
            return ALIASES.get(normalized);
        }

        // fallback: replace hyphens/spaces with underscore and uppercase
        String fallback = normalized.replace("-", "_").replace(" ", "_").toUpperCase();
        try {
            return ReturnReason.valueOf(fallback);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid return reason: '" + value + "'. Valid values: " + java.util.Arrays.toString(ReturnReason.values()));
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", " ");
    }
}
