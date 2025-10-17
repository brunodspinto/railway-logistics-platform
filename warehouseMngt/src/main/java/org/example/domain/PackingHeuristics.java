package org.example.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PackingHeuristics {

    public static PackingResult firstFit(List<OrderLine> lines, double trolleyCapacity, Map<String, Item> itemMap, boolean splitLargeLines) {
        List<Trolley> trolleys = new ArrayList<>();
        List<String> logs = new ArrayList<>();

        for (OrderLine original : lines) {
            OrderLine remaining = original;
            while (remaining != null && remaining.getRequestedQty() > 0) {
                boolean placed = false;

                // try full-fit into existing trolleys
                for (Trolley t : trolleys) {
                    if (t.canFit(remaining, itemMap)) {
                        t.add(remaining, itemMap);
                        remaining = null;
                        placed = true;
                        break;
                    }
                }
                if (placed) break;

                // try partial fit into existing trolleys (if splitting allowed)
                if (splitLargeLines) {
                    boolean partialDone = false;
                    for (Trolley t : trolleys) {
                        int beforeQty = remaining.getRequestedQty();
                        OrderLine after = t.addPartial(remaining, itemMap);
                        int afterQty = (after == null) ? 0 : after.getRequestedQty();
                        int added = beforeQty - afterQty;
                        if (added > 0) {
                            logs.add(String.format("PARTIAL ALLOCATION: orderId=%s, lineNo=%d, addedQty=%d, remainingQty=%d",
                                    remaining.getOrderId(), remaining.getLineNo(), added, afterQty));
                        }
                        if (after == null) {
                            remaining = null;
                            partialDone = true;
                            break;
                        } else if (after != remaining) {
                            remaining = after;
                            partialDone = true;
                            break;
                        }
                    }
                    if (partialDone) continue;
                }

                // create new trolley
                Trolley newT = new Trolley(trolleyCapacity);
                double remainingWeight = remaining.getOrderLineWeight(itemMap);

                if (remainingWeight <= newT.remainingCapacity()) {
                    // If there were existing trolleys and we couldn't fit there, this is a defer (skip due to capacity)
                    if (!trolleys.isEmpty()) {
                        logs.add(String.format("SKIPPED DUE TO CAPACITY: orderId=%s, lineNo=%d, deferredToTrolley=%d",
                                remaining.getOrderId(), remaining.getLineNo(), trolleys.size() + 1));
                    }
                    newT.add(remaining, itemMap);
                    trolleys.add(newT);
                    remaining = null;
                } else {
                    // doesn't fit even in empty trolley
                    if (!splitLargeLines) {
                        throw new IllegalArgumentException("OrderLine too large for trolley capacity and splitting is disabled: " + remaining);
                    } else {
                        OrderLine after = newT.addPartial(remaining, itemMap);
                        int beforeQty = remaining.getRequestedQty();
                        int afterQty = (after == null) ? 0 : after.getRequestedQty();
                        int added = beforeQty - afterQty;
                        if (added > 0) {
                            logs.add(String.format("PARTIAL ALLOCATION: orderId=%s, lineNo=%d, addedQty=%d, remainingQty=%d",
                                    remaining.getOrderId(), remaining.getLineNo(), added, afterQty));
                        }
                        trolleys.add(newT);
                        if (after == remaining) {
                            throw new IllegalArgumentException("Item unit weight exceeds trolley capacity for SKU: " + remaining.getSku());
                        }
                        remaining = after;
                    }
                }
            }
        }

        return new PackingResult(trolleys, logs);
    }

    public static PackingResult firstFitDecreasing(List<OrderLine> lines,
                                                   double trolleyCapacity,
                                                   Map<String, Item> itemMap,
                                                   boolean splitLargeLines) {
        List<OrderLine> sorted = new ArrayList<>(lines);
        sorted.sort((a, b) -> Double.compare(b.getOrderLineWeight(itemMap), a.getOrderLineWeight(itemMap)));
        return firstFit(sorted, trolleyCapacity, itemMap, splitLargeLines);
    }

    public static PackingResult bestFitDecreasing(List<OrderLine> lines,
                                                  double trolleyCapacity,
                                                  Map<String, Item> itemMap,
                                                  boolean splitLargeLines) {
        List<OrderLine> sorted = new ArrayList<>(lines);
        sorted.sort((a, b) -> Double.compare(b.getOrderLineWeight(itemMap), a.getOrderLineWeight(itemMap)));

        List<Trolley> trolleys = new ArrayList<>();
        List<String> logs = new ArrayList<>();

        for (OrderLine original : sorted) {
            OrderLine remaining = original;
            while (remaining != null && remaining.getRequestedQty() > 0) {
                Trolley best = null;
                double bestAfter = Double.MAX_VALUE;
                double lineWeight = remaining.getOrderLineWeight(itemMap);

                // try find best trolley that can fit whole remaining
                for (Trolley t : trolleys) {
                    double rem = t.remainingCapacity();
                    if (lineWeight <= rem) {
                        double after = rem - lineWeight;
                        if (after < bestAfter) {
                            bestAfter = after;
                            best = t;
                        }
                    }
                }
                if (best != null) {
                    best.add(remaining, itemMap);
                    remaining = null;
                    break;
                }

                // try partial in existing trolleys if splitting allowed
                if (splitLargeLines) {
                    boolean partialPlaced = false;
                    for (Trolley t : trolleys) {
                        int beforeQty = remaining.getRequestedQty();
                        OrderLine after = t.addPartial(remaining, itemMap);
                        int afterQty = (after == null) ? 0 : after.getRequestedQty();
                        int added = beforeQty - afterQty;
                        if (added > 0) {
                            logs.add(String.format("PARTIAL ALLOCATION: orderId=%s, lineNo=%d, addedQty=%d, remainingQty=%d",
                                    remaining.getOrderId(), remaining.getLineNo(), added, afterQty));
                        }
                        if (after == null) {
                            remaining = null;
                            partialPlaced = true;
                            break;
                        } else if (after != remaining) {
                            remaining = after;
                            partialPlaced = true;
                            break;
                        }
                    }
                    if (partialPlaced) continue;
                }

                // no existing trolley fit -> create new trolley
                Trolley newT = new Trolley(trolleyCapacity);
                double remWeight = remaining.getOrderLineWeight(itemMap);
                if (remWeight <= newT.remainingCapacity()) {
                    if (!trolleys.isEmpty()) {
                        logs.add(String.format("SKIPPED DUE TO CAPACITY: orderId=%s, lineNo=%d, deferredToTrolley=%d",
                                remaining.getOrderId(), remaining.getLineNo(), trolleys.size() + 1));
                    }
                    newT.add(remaining, itemMap);
                    trolleys.add(newT);
                    remaining = null;
                } else {
                    if (!splitLargeLines) {
                        throw new IllegalArgumentException("OrderLine too large for trolley capacity and splitting is disabled: " + remaining);
                    } else {
                        OrderLine after = newT.addPartial(remaining, itemMap);
                        int beforeQty = remaining.getRequestedQty();
                        int afterQty = (after == null) ? 0 : after.getRequestedQty();
                        int added = beforeQty - afterQty;
                        if (added > 0) {
                            logs.add(String.format("PARTIAL ALLOCATION: orderId=%s, lineNo=%d, addedQty=%d, remainingQty=%d",
                                    remaining.getOrderId(), remaining.getLineNo(), added, afterQty));
                        }
                        trolleys.add(newT);
                        if (after == remaining) {
                            throw new IllegalArgumentException("Item unit weight exceeds trolley capacity for SKU: " + remaining.getSku());
                        }
                        remaining = after;
                    }
                }
            }
        }

        return new PackingResult(trolleys, logs);
    }
}
