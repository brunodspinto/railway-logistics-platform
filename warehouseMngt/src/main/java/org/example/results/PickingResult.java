package org.example.results;

import org.example.domain.Trolley;
import java.util.List;

public class PickingResult {

    public static void printSummary(String title, List<Trolley> trolleys) {
        System.out.println("=====" + title + "=====");
        System.out.printf("Total trolleys used: %d%n%n", trolleys.size());
        for (Trolley t : trolleys) System.out.println(t);
    }
}
