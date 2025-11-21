package org.example.usei07.ui;

import org.example.service.StationService;
import org.example.trees.StationIndexes;
import org.example.ui.usei07.USEI07Menu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para USEI07Menu usando apenas JUnit e Stubs manuais.
 * Simula a interação com a consola (System.in/out).
 */
class USEI07MenuTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    /**
     * Stub para StationIndexes.
     * Sobrescreve apenas o método que devolve o total de estações.
     */
    static class StationIndexesStub extends StationIndexes {
        @Override
        public int getTotalStations() {
            return 999;
        }
    }

    /**
     * Stub para StationService.
     * Permite controlar o estado 'ready' e o relatório de performance.
     */
    static class StationServiceStub extends StationService {
        private boolean isReadyValue;
        private final StationIndexes indexesStub = new StationIndexesStub();

        public void setReady(boolean ready) {
            this.isReadyValue = ready;
        }

        @Override
        public boolean isReady() {
            return isReadyValue;
        }

        @Override
        public StationIndexes getIndexes() {
            return indexesStub;
        }

        @Override
        public String getPerformanceReport() {
            return "RELATORIO DE TESTE: 2D-Tree construída com sucesso.";
        }
    }

    @Test
    void testServiceNotReady() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(false);

        provideInput("");

        USEI07Menu menu = new USEI07Menu(serviceStub);

        menu.start();

        String erroOutput = errContent.toString();
        assertTrue(erroOutput.contains("Service not ready"),
                "Deve imprimir mensagem de erro se o serviço não estiver pronto.");
    }

    @Test
    void testMenuExitsCorrectly() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        provideInput("0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);

        menu.start();

        String output = outContent.toString();

        assertTrue(output.contains("[SPATIAL QUERIES (2D-Tree) MENU]"));

        assertTrue(output.contains("Stations indexed: 999"));
    }

    @Test
    void testShowBuildStats() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        provideInput("1\n\n0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);

        menu.start();

        String output = outContent.toString();

        assertTrue(output.contains("[USEI07: Index Build Report]"));

        assertTrue(output.contains("RELATORIO DE TESTE: 2D-Tree construída com sucesso."));
    }

    @Test
    void testInvalidInputType() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        provideInput("abc\n0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);

        menu.start();

        String output = outContent.toString();

        assertTrue(output.contains("Invalid input"),
                "Deve tratar entradas não numéricas (InputMismatchException).");
    }

    @Test
    void testInvalidOptionNumber() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        provideInput("5\n0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);

        menu.start();

        String output = outContent.toString();

        assertTrue(output.contains("Invalid option"),
                "Deve avisar quando a opção numérica não existe no menu.");
    }

    /**
     * Método auxiliar para simular o input do utilizador.
     * Deve ser chamado ANTES de instanciar o menu.
     */
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
}