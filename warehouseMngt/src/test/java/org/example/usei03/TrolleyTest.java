package org.example.usei03;

import org.example.domain.PickingItem;
import org.example.domain.Trolley;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TrolleyTest {

    private Trolley trolley;
    private PickingItem lightItem;
    private PickingItem heavyItem;

    @BeforeEach
    void setUp() {
        // Capacidade global conforme USEI03
        Trolley.setCapacity(638.96);

        trolley = new Trolley(1);

        // Itens simulados (apenas com valores relevantes)
        lightItem = new PickingItem("O1", 1, "SKU1", "B1", 1, 1, 2, 50.0);  // 100 kg
        heavyItem = new PickingItem("O1", 2, "SKU2", "B2", 1, 2, 10, 80.0); // 800 kg (não cabe)
    }

    @Test
    @DisplayName("Trolley vazio deve ter peso e utilização zero")
    void testEmptyTrolleyInitialState() {
        assertEquals(0.0, trolley.getUsedWeight(), 0.001);
        assertEquals(0.0, trolley.getUtilization(), 0.001);
        assertTrue(trolley.getItems().isEmpty());
    }

    @Test
    @DisplayName("canFit deve retornar verdadeiro se item couber")
    void testCanFitTrue() {
        assertTrue(trolley.canFit(lightItem), "Item leve devia caber no trolley");
    }

    @Test
    @DisplayName("canFit deve retornar falso se item não couber")
    void testCanFitFalse() {
        assertFalse(trolley.canFit(heavyItem), "Item pesado não devia caber no trolley");
    }

    @Test
    @DisplayName("addItem deve aumentar o peso e lista de itens")
    void testAddItemUpdatesWeight() {
        trolley.addItem(lightItem);

        assertEquals(lightItem.getTotalWeight(), trolley.getUsedWeight(), 0.001);
        assertEquals(1, trolley.getItems().size());
        assertTrue(trolley.getItems().contains(lightItem));
    }

    @Test
    @DisplayName("addItem deve lançar exceção se item não couber")
    void testAddItemThrowsIfOverCapacity() {
        assertThrows(IllegalArgumentException.class, () -> trolley.addItem(heavyItem),
                "Esperada exceção ao tentar adicionar item que ultrapassa capacidade");
    }

    @Test
    @DisplayName("getUtilization deve calcular percentagem corretamente")
    void testGetUtilization() {
        trolley.addItem(lightItem); // 100 kg de 638.96 kg
        double expected = (100.0 / 638.96) * 100.0;
        assertEquals(expected, trolley.getUtilization(), 0.001);
    }

    @Test
    @DisplayName("setCapacity deve lançar exceção se valor for inválido")
    void testSetCapacityInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Trolley.setCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> Trolley.setCapacity(-10));
    }

    @Test
    @DisplayName("toString deve conter id e percentagem de utilização")
    void testToStringContainsInfo() {
        trolley.addItem(lightItem);
        String text = trolley.toString();
        assertTrue(text.contains("Trolley #1"));
        assertTrue(text.contains("%"));
    }
}
