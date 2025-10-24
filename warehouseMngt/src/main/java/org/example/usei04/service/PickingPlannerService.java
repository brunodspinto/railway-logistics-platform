package org.example.usei04.service;

import org.example.domain.*;
import java.util.*;

public class PickingPlannerService {

    public enum Heuristic { FF, FFD, BFD }

    public List<Trolley> generatePickingPlan(List<PickingItem> items, Heuristic heuristic) {

        List<PickingItem> sortedItems = new ArrayList<>(items);

        switch(heuristic) {
            case FFD, BFD -> sortedItems.sort(Comparator.comparingDouble(PickingItem::getTotalWeight).reversed());
            case FF -> {
                // No sorting needed for First Fit
            }
        }

        List<Trolley> trolleys = new ArrayList<>();
        int trolleyCounter = 1;

        for (PickingItem item : sortedItems) {
            Trolley chosen = null;

            switch (heuristic) {
                case FF, FFD -> chosen = findFirstFit(trolleys, item);
                case BFD -> chosen = findBestFit(trolleys, item);
            }

            if (chosen == null) {
                chosen = new Trolley(trolleyCounter++);
                trolleys.add(chosen);
            }

            chosen.addItem(item);
        }

        return trolleys;
    }

    private Trolley findFirstFit(List<Trolley> trolleys, PickingItem item) {
        for (Trolley t : trolleys) {
            if (t.canFit(item)) {
                return t;
            }
        }
        return null;
    }

    private Trolley findBestFit(List<Trolley> trolleys, PickingItem item) {
        Trolley bestTrolley = null;
        double minRemainingCapacity = Double.MAX_VALUE;

        for (Trolley t : trolleys) {
            double remaining = Trolley.getCapacity() - t.getUsedWeight();
            if (t.canFit(item)) {
                double leftover = remaining - item.getTotalWeight();
                if (leftover < minRemainingCapacity) {
                    minRemainingCapacity = leftover;
                    bestTrolley = t;
                }
            }
        }

        return bestTrolley;
    }
}
