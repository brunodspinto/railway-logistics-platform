package org.example.ui.menu;

public class DevTeamUI implements Runnable {

    @Override
    public void run() {
        System.out.println("""
        === Development Team ===
        -------------------------
        Eduardo Oliveira (Scrum Master)
        David Ribeiro
        Diogo Azevedo
        Bruno Pinto
        Rafael Santos
        """);
    }
}

