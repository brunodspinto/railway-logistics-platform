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
 * Testes unitários para USEI07Menu.
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

    static class StationIndexesStub extends StationIndexes {
        @Override
        public int getTotalStations() {
            return 999;
        }
    }

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

    // --- TESTES ---

    @Test
    void testServiceNotReady() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(false);

        // Input vazio, pois ele sai logo
        provideInput("");

        USEI07Menu menu = new USEI07Menu(serviceStub);
        menu.start();

        String erroOutput = errContent.toString();
        // Verifica se a mensagem de erro está correta (System.err)
        assertTrue(erroOutput.contains("Service not ready"),
                "Deve imprimir mensagem de erro se o serviço não estiver pronto.");
    }

    @Test
    void testMenuExitsCorrectly() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        // Simula escolher "0" para sair
        provideInput("0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);
        menu.start();

        String output = outContent.toString();

        assertTrue(output.contains("[MENU]"),
                "O menu deve conter o cabeçalho [MENU]");

        assertTrue(output.contains("Stations indexed: 999"),
                "Deve mostrar o número de estações indexadas.");
    }

    @Test
    void testShowBuildStats() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        // Sequência:
        // "1" - Escolher ver estatísticas
        // "\n" - Enter para o "Press ENTER to continue..." (o pause())
        // "0" - Sair
        provideInput("1\n\n0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);
        menu.start();

        String output = outContent.toString();

        assertTrue(output.contains("RELATORIO DE TESTE: 2D-Tree construída com sucesso."),
                "Deve imprimir o relatório retornado pelo serviço.");
    }

    @Test
    void testInvalidInputType() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        // "abc" -> Gera erro, "0" -> Sai
        provideInput("abc\n0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);
        menu.start();

        String output = outContent.toString();

        // Verifica a mensagem do catch(InputMismatchException)
        assertTrue(output.contains("Invalid input. Please enter a number."),
                "Deve tratar entradas não numéricas.");
    }

    @Test
    void testInvalidOptionNumber() {
        StationServiceStub serviceStub = new StationServiceStub();
        serviceStub.setReady(true);

        // "5" -> Opção inválida, "0" -> Sai
        provideInput("5\n0\n");

        USEI07Menu menu = new USEI07Menu(serviceStub);
        menu.start();

        String output = outContent.toString();

        // Verifica a mensagem do default no switch
        assertTrue(output.contains("Invalid option. Please try again."),
                "Deve avisar quando a opção numérica não existe.");
    }

    /**
     * Método auxiliar para simular o input do utilizador.
     */
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
}