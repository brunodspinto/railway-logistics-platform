package org.example.usei03;

import org.example.domain.*;
import org.example.results.PickingResult;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PickingResultTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Redireciona System.out para capturar a saída
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restaura System.out
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Deve imprimir título e número de trolleys corretamente")
    void testPrintSummaryBasicOutput() {
        // Cria 2 trolleys de teste com um item cada
        Trolley.setCapacity(638.96);
        Trolley t1 = new Trolley(1);
        Trolley t2 = new Trolley(2);

        t1.addItem(new PickingItem("O1", 1, "SKU1", "B1", 1, 1, 2, 50.0)); // 100 kg
        t2.addItem(new PickingItem("O2", 1, "SKU2", "B2", 1, 2, 3, 30.0)); // 90 kg

        List<Trolley> trolleys = List.of(t1, t2);

        // Executa método
        PickingResult.printSummary("FIRST FIT", trolleys);

        // Verifica o conteúdo impresso
        String output = outputStream.toString();

        assertTrue(output.contains("=====FIRST FIT====="), "Deve conter o título formatado");
        assertTrue(output.contains("Total trolleys used: 2"), "Deve indicar o número correto de trolleys");
        assertTrue(output.contains("Trolley #1"), "Deve imprimir detalhes do primeiro trolley");
        assertTrue(output.contains("Trolley #2"), "Deve imprimir detalhes do segundo trolley");
    }

    @Test
    @DisplayName("Deve lidar corretamente com lista vazia de trolleys")
    void testPrintSummaryWithEmptyList() {
        PickingResult.printSummary("EMPTY TEST", List.of());
        String output = outputStream.toString();

        assertTrue(output.contains("Total trolleys used: 0"), "Deve indicar 0 trolleys");
        assertFalse(output.contains("Trolley #"), "Não deve listar nenhum trolley");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException se lista for nula")
    void testPrintSummaryWithNullList() {
        assertThrows(NullPointerException.class, () ->
                PickingResult.printSummary("NULL TEST", null)
        );
    }
}
