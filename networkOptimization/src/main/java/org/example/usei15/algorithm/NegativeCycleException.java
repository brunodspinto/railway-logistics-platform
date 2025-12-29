package org.example.usei15.algorithm;

import java.util.List;

public class NegativeCycleException extends RuntimeException {

    private final List<?> cycle;
    private final String detailedMessage;

    public NegativeCycleException(List<?> cycle, String detailedMessage) {
        super("Negative cycle detected");
        this.cycle = cycle;
        this.detailedMessage = detailedMessage;
    }

    public List<?> getCycle() {
        return cycle;
    }

    public String getDetailedMessage() {
        return detailedMessage;

    }
}
