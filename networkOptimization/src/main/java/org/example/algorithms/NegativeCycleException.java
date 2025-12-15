package org.example.algorithms;

import java.util.List;

public class NegativeCycleException extends RuntimeException {

    private final List<?> cycle;

    public NegativeCycleException(List<?> cycle) {
        super("Negative cycle detected");
        this.cycle = cycle;
    }

    public List<?> getCycle() {
        return cycle;
    }
}
