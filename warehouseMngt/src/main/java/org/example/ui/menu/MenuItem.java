package org.example.ui.menu;

public class MenuItem {
    private final String label;
    private final Runnable action;

    public MenuItem(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public void run() {
        action.run();
    }
}
