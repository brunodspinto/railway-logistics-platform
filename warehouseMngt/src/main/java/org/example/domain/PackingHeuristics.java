
package org.example.domain;

import java.util.*;

public class PackingHeuristics {

    public static List<Trolley> firstFit(List<OrderLine> lines,
                                         double trolleyCapacity,
                                         Map<String, Item> itemMap,
                                         boolean splitLargeLines) {
        List<Trolley> trolleys = new ArrayList<>();
        for (OrderLine original : lines) {
            OrderLine remaining = original;
            // keep placing until nothing remains
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
                        OrderLine after = t.addPartial(remaining, itemMap);
                        if (after == null) {
                            remaining = null;
                            partialDone = true;
                            break;
                        } else if (after != remaining) {
                            // partial was added, update remaining and continue
                            remaining = after;
                            partialDone = true;
                            break;
                        }
                    }
                    if (partialDone) continue;
                }

                // create new trolley
                Trolley newT = new Trolley(trolleyCapacity);
                // check if the whole remaining fits in empty trolley
                double remainingWeight = remaining.getOrderLineWeight(itemMap);
                if (remainingWeight <= newT.remainingCapacity()) {
                    newT.add(remaining, itemMap);
                    trolleys.add(newT);
                    remaining = null;
                } else {
                    // doesn't fit in empty trolley
                    if (!splitLargeLines) {
                        // cannot split -> this line cannot be satisfied (would need to defer to next trolley,
                        // but an empty trolley already can't fit the whole line => reject)
                        throw new IllegalArgumentException("OrderLine too large for trolley capacity and splitting is disabled: " + remaining);
                    } else {
                        // split: add as much as possible into new trolley, keep remainder
                        OrderLine after = newT.addPartial(remaining, itemMap);
                        trolleys.add(newT);
                        if (after == remaining) {
                            // nothing could be added even to empty trolley -> item unit weight > capacity
                            throw new IllegalArgumentException("Item unit weight exceeds trolley capacity for SKU: " + remaining.getSku());
                        }
                        remaining = after;
                    }
                }
            }
        }
        return trolleys;
    }

    public static List<Trolley> firstFitDecreasing(List<OrderLine> lines,
                                                   double trolleyCapacity,
                                                   Map<String, Item> itemMap,
                                                   boolean splitLargeLines) {
        List<OrderLine> sorted = new ArrayList<>(lines);
        sorted.sort((a, b) -> Double.compare(b.getOrderLineWeight(itemMap), a.getOrderLineWeight(itemMap)));
        return firstFit(sorted, trolleyCapacity, itemMap, splitLargeLines);
    }

    public static List<Trolley> bestFitDecreasing(List<OrderLine> lines,
                                                  double trolleyCapacity,
                                                  Map<String, Item> itemMap,
                                                  boolean splitLargeLines) {
        List<OrderLine> sorted = new ArrayList<>(lines);
        sorted.sort((a, b) -> Double.compare(b.getOrderLineWeight(itemMap), a.getOrderLineWeight(itemMap)));

        List<Trolley> trolleys = new ArrayList<>();

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
                        OrderLine after = t.addPartial(remaining, itemMap);
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
                    newT.add(remaining, itemMap);
                    trolleys.add(newT);
                    remaining = null;
                } else {
                    if (!splitLargeLines) {
                        throw new IllegalArgumentException("OrderLine too large for trolley capacity and splitting is disabled: " + remaining);
                    } else {
                        OrderLine after = newT.addPartial(remaining, itemMap);
                        trolleys.add(newT);
                        if (after == remaining) {
                            throw new IllegalArgumentException("Item unit weight exceeds trolley capacity for SKU: " + remaining.getSku());
                        }
                        remaining = after;
                    }
                }
            }
        }

        return trolleys;
    }
}
