package org.example.usei04.executor;

import org.example.domain.PickingItem;
import org.example.domain.Trolley;
import org.example.ui.executors.USEI04Executor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para a classe USEI04Executor.
 */
class USEI04ExecutorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    /**
     * Redireciona System.out e System.err para os nossos streams.
     */
    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /**
     * Restaura os streams originais.
     */
    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testExecuteWithNullPickingPlan() {
        USEI04Executor.execute(null);
        String output = errContent.toString();
        assertTrue(output.contains("Picking plan is empty. Cannot execute USEI04."),
                "Deveria exibir a mensagem de erro completa para plano nulo.");
    }

    @Test
    void testExecuteWithEmptyPickingPlan() {
        USEI04Executor.execute(new ArrayList<>());
        String output = errContent.toString();
        assertTrue(output.contains("Picking plan is empty. Cannot execute USEI04."),
                "Deveria exibir a mensagem de erro completa para plano vazio.");
    }

    @Test
    void testExecuteWithTrolleyWithNoItems() {
        Trolley trolley = new Trolley(1);
        List<Trolley> pickingPlan = Collections.singletonList(trolley);
        USEI04Executor.execute(pickingPlan);
        String output = outContent.toString();
        assertTrue(output.contains("This trolley has no items to pick. Skipping."),
                "Deveria exibir uma mensagem a informar que o trolley não tem itens.");
    }

    @Test
    void testExecuteWithSingleTrolleyWithItems() {
        Trolley trolley = new Trolley(1);
        trolley.addItem(new PickingItem("ORD01", 1, "SKU01", "BOX01", 1, 8, 1, 1.0));
        trolley.addItem(new PickingItem("ORD01", 2, "SKU02", "BOX02", 2, 2, 1, 1.0));
        trolley.addItem(new PickingItem("ORD01", 3, "SKU03", "BOX03", 3, 4, 1, 1.0));
        List<Trolley> pickingPlan = Collections.singletonList(trolley);

        USEI04Executor.execute(pickingPlan);
        String output = outContent.toString();

        assertTrue(output.contains("  -> Total Distance: 33.0"));
        assertTrue(output.contains("  -> Total Distance: 35.0"));
        // Executor prints the delta with %.2f, whose decimal separator depends on the
        // JVM default locale (2,00 in pt-PT, 2.00 in en-US). Assert on the parts of the
        // line that are locale-independent.
        assertTrue(output.contains("Strategy A is shorter by 2"),
                "Should announce Strategy A is shorter by 2 units");
        assertTrue(output.contains("units."));
    }
}