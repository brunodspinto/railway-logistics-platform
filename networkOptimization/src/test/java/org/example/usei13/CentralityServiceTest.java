package org.example.usei13;

import org.example.usei12.domain.RailGraph;
import org.example.usei12.domain.Station;
import org.example.usei13.service.CentralityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CentralityServiceTest {

    private static final int DEGREE_COL = 2;
    private static final int BETWEENNESS_COL = 4;
    private static final int CLOSENESS_COL = 5;
    private static final int HUB_COL = 6;

    private String runAndCapture(Runnable r) {
        PrintStream old = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        try {
            r.run();
        } finally {
            System.setOut(old);
        }
        return out.toString();
    }

    private String lineFor(String output, String stationId) {
        return output.lines()
                .filter(l -> l.startsWith(stationId + " "))
                .findFirst()
                .orElseThrow();
    }

    private double col(String line, int idx) {
        String[] t = line.trim().split("\\s+");
        return Double.parseDouble(t[idx]);
    }

    @Test
    @DisplayName("Grafo em linha A–B–C: métricas e ranking corretos")
    void lineGraph_exactMetricsAndRanking() {
        Locale.setDefault(Locale.US);

        RailGraph g = new RailGraph();
        Station a = new Station("A","A",0,0);
        Station b = new Station("B","B",0,0);
        Station c = new Station("C","C",0,0);

        g.addStation(a);
        g.addStation(b);
        g.addStation(c);

        g.addEdge(a,b,1);
        g.addEdge(b,c,1);

        CentralityService s = new CentralityService(g);
        String out = runAndCapture(s::computeAndPrint);

        String la = lineFor(out,"A");
        String lb = lineFor(out,"B");
        String lc = lineFor(out,"C");

        // degree
        assertEquals(1, col(la, DEGREE_COL));
        assertEquals(2, col(lb, DEGREE_COL));
        assertEquals(1, col(lc, DEGREE_COL));

        // betweenness
        assertEquals(0.0, col(la, BETWEENNESS_COL));
        assertTrue(col(lb, BETWEENNESS_COL) >= col(la, BETWEENNESS_COL));
        assertEquals(0.0, col(lc, BETWEENNESS_COL));

        // harmonic closeness
        assertEquals(1.5, col(la, CLOSENESS_COL), 1e-6);
        assertEquals(1.0, col(lb, CLOSENESS_COL), 1e-6);
        assertEquals(0.0, col(lc, CLOSENESS_COL), 1e-6);

        // hub score ranking
        assertTrue(col(lb, HUB_COL) > col(la, HUB_COL));
        assertTrue(col(lb, HUB_COL) > col(lc, HUB_COL));
    }

    @Test
    @DisplayName("Grafo estrela: nó central é o hub global")
    void starGraph_centerIsGlobalHub() {
        Locale.setDefault(Locale.US);

        RailGraph g = new RailGraph();
        Station c = new Station("C","C",0,0);
        Station a = new Station("A","A",0,0);
        Station b = new Station("B","B",0,0);
        Station d = new Station("D","D",0,0);

        g.addStation(c);
        g.addStation(a);
        g.addStation(b);
        g.addStation(d);

        g.addEdge(c,a,1);
        g.addEdge(c,b,1);
        g.addEdge(c,d,1);

        CentralityService s = new CentralityService(g);
        String out = runAndCapture(s::computeAndPrint);

        double hubC = col(lineFor(out,"C"), HUB_COL);
        double hubA = col(lineFor(out,"A"), HUB_COL);
        double hubB = col(lineFor(out,"B"), HUB_COL);
        double hubD = col(lineFor(out,"D"), HUB_COL);

        assertTrue(hubC > hubA);
        assertTrue(hubC > hubB);
        assertTrue(hubC > hubD);

        // folhas não têm betweenness
        assertEquals(0.0, col(lineFor(out,"A"), BETWEENNESS_COL));
        assertEquals(0.0, col(lineFor(out,"B"), BETWEENNESS_COL));
        assertEquals(0.0, col(lineFor(out,"D"), BETWEENNESS_COL));
    }

    @Test
    @DisplayName("Grafo desconectado ainda produz resultados")
    void disconnectedGraph_stillProducesScores() {
        Locale.setDefault(Locale.US);

        RailGraph g = new RailGraph();
        g.addStation(new Station("A","A",0,0));
        g.addStation(new Station("B","B",0,0));

        CentralityService s = new CentralityService(g);
        String out = runAndCapture(s::computeAndPrint);

        assertTrue(out.contains("A"));
        assertTrue(out.contains("B"));
    }

    @Test
    @DisplayName("Grafo com um único nó gera hub score válido")
    void singleNodeGraph_scoresAreValid() {
        Locale.setDefault(Locale.US);

        RailGraph g = new RailGraph();
        g.addStation(new Station("A","A",0,0));

        CentralityService s = new CentralityService(g);
        String out = runAndCapture(s::computeAndPrint);

        double hub = col(lineFor(out,"A"), HUB_COL);
        assertTrue(hub >= 0.0 && hub <= 1.0);
    }

    @Test
    @DisplayName("Todos os hub scores estão normalizados entre 0 e 1")
    void allHubScoresAreNormalizedBetweenZeroAndOne() {
        Locale.setDefault(Locale.US);

        RailGraph g = new RailGraph();
        Station a = new Station("A","A",0,0);
        Station b = new Station("B","B",0,0);
        Station c = new Station("C","C",0,0);

        g.addStation(a);
        g.addStation(b);
        g.addStation(c);

        g.addEdge(a,b,1);
        g.addEdge(b,c,2);

        CentralityService s = new CentralityService(g);
        String out = runAndCapture(s::computeAndPrint);

        out.lines()
                .filter(l -> l.startsWith("A") || l.startsWith("B") || l.startsWith("C"))
                .forEach(l -> {
                    double hub = col(l, HUB_COL);
                    assertTrue(hub >= 0.0 && hub <= 1.0);
                });
    }
}