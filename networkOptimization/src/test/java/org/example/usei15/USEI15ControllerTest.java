package org.example.usei15;

import org.example.domain.Connection;
import org.example.domain.Station;
import org.example.graph.Graph;
import org.example.graph.map.MapGraph;
import org.example.usei15.controller.USEI15Controller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class USEI15ControllerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private Graph<Station, Connection> graph;
    private USEI15Controller controller;
    private Station s1, s2, s3;

    @BeforeEach
    void setUp() {
        // Redirecionar System.out para capturar o output
        System.setOut(new PrintStream(outContent));

        // Configurar Grafo de Teste
        graph = new MapGraph<>(true);
        s1 = new Station("1", "S1");
        s2 = new Station("2", "S2");
        s3 = new Station("3", "S3");

        graph.addVertex(s1);
        graph.addVertex(s2);
        graph.addVertex(s3);

        controller = new USEI15Controller(graph);
    }

    @AfterEach
    void tearDown() {
        // Restaurar System.out original
        System.setOut(originalOut);
    }

    @Test
    void testExecuteWithValidPath() {
        // S1 -> S2 (Cust 10)
        graph.addEdge(s1, s2, new Connection(s1, s2, 10.0, 50, 10.0));

        controller.execute(s1, s2);

        String output = outContent.toString();

        // Verificar se imprimiu o caminho e o custo
        assertTrue(output.contains("Risk-Aware Shortest Path"));
        assertTrue(output.contains("S1"));
        assertTrue(output.contains("S2"));
        assertTrue(output.contains("Total cost to target: 10,00") || output.contains("Total cost to target: 10.00"));
    }

    @Test
    void testExecuteWithNegativeCycle() {
        // Criar Ciclo Negativo: S1 -> S2 (1) -> S1 (-5)
        graph.addEdge(s1, s2, new Connection(s1, s2, 10.0, 50, 1.0));
        graph.addEdge(s2, s1, new Connection(s2, s1, 10.0, 50, -5.0));

        controller.execute(s1, s2);

        String output = outContent.toString();

        // Verificar se apanhou a exceção e imprimiu a mensagem de erro formatada
        assertTrue(output.contains("Negative Cycle Detected"), "Deve avisar do ciclo");
        assertTrue(output.contains("Configuration inconsistency"), "Deve avisar da inconsistência");
        // Verificar se os detalhes do ciclo estão lá (Graças à tua nova implementação)
        assertTrue(output.contains("Cost: -5") || output.contains("Cost: -5.0"), "Deve mostrar o custo da aresta problemática");
    }

    @Test
    void testExecuteNoPath() {
        // Sem arestas entre S1 e S3
        controller.execute(s1, s3);

        String output = outContent.toString();

        assertTrue(output.contains("No path exists"), "Deve avisar que não há caminho");
    }
}