package org.example.ui.menu;

import org.example.domain.Trolley;
import org.example.ui.executors.USEI04Executor;

import java.util.List;

public class USEI04UI implements Runnable {

    @Override
    public void run() {
        try {
            System.out.println("\n=== Running USEI04 - Pick Path Sequencing ===\n");

            List<Trolley> pickingPlan = SharedContext.getLastPickingPlan();

            if (pickingPlan != null && !pickingPlan.isEmpty()) {
                USEI04Executor.execute(pickingPlan);
            } else {
                System.out.println("\n⚠️ No picking plan found — run USEI03 first.");
            }

        } catch (Exception e) {
            System.err.println("❌ USEI04 failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
