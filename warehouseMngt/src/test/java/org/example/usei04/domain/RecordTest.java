package org.example.usei04.domain;

import org.example.domain.Record;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe de domínio Record.
 */
class RecordTest {

    // ----------------------------------------------------------
    // TESTES DO CONSTRUTOR E GETTERS
    // ----------------------------------------------------------

    @Test
    void testConstructorAndGetters() {
        Record record = new Record(5, 10);

        assertAll("O construtor deve inicializar corretamente as propriedades aisle e bay",
                () -> assertEquals(5, record.getAisle(), "O getAisle() deve retornar o valor correto."),
                () -> assertEquals(10, record.getBay(), "O getBay() deve retornar o valor correto.")
        );
    }

    @Test
    void testEntranceConstant() {
        assertAll("A constante ENTRANCE deve estar na posição (0,0)",
                () -> assertEquals(0, Record.ENTRANCE.getAisle(), "O aisle de ENTRANCE deve ser 0."),
                () -> assertEquals(0, Record.ENTRANCE.getBay(), "O bay de ENTRANCE deve ser 0.")
        );
    }

    // ----------------------------------------------------------
    // TESTES DO MÉTODO equals()
    // ----------------------------------------------------------

    @Test
    void testEqualsContract() {
        Record r1 = new Record(3, 7);
        Record r2 = new Record(3, 7); // Igual a r1
        Record r3 = new Record(4, 7); // Aisle diferente
        Record r4 = new Record(3, 8); // Bay diferente

        // Teste de reflexividade: um objeto deve ser igual a si mesmo.
        assertTrue(r1.equals(r1), "Um objeto deve ser igual a si mesmo.");

        // Teste de simetria: se r1.equals(r2), então r2.equals(r1).
        assertTrue(r1.equals(r2), "Dois registos com o mesmo aisle e bay devem ser iguais.");
        assertTrue(r2.equals(r1), "A igualdade deve ser simétrica.");

        // Teste contra nulo
        assertFalse(r1.equals(null), "Um objeto nunca deve ser igual a null.");

        // Teste contra tipo diferente
        assertFalse(r1.equals("uma string"), "Um objeto Record não deve ser igual a um objeto de outra classe.");

        // Testes de desigualdade
        assertFalse(r1.equals(r3), "Dois registos com aisles diferentes não devem ser iguais.");
        assertFalse(r1.equals(r4), "Dois registos com bays diferentes não devem ser iguais.");
    }


    // ----------------------------------------------------------
    // TESTES DO MÉTODO hashCode()
    // ----------------------------------------------------------

    @Test
    void testHashCodeContract() {
        Record r1 = new Record(10, 20);
        Record r2 = new Record(10, 20); // Igual a r1

        // O contrato de hashCode exige que objetos iguais tenham o mesmo hashCode.
        assertEquals(r1.hashCode(), r2.hashCode(), "Objetos iguais devem ter o mesmo hashCode.");
    }

    // ----------------------------------------------------------
    // TESTES DO MÉTODO toString()
    // ----------------------------------------------------------

    @Test
    void testToStringFormat() {
        Record record = new Record(9, 15);
        String expected = "(9,15)";
        assertEquals(expected, record.toString(), "O método toString() deve retornar a string no formato (aisle,bay).");

        // Testa também com a constante
        assertEquals("(0,0)", Record.ENTRANCE.toString(), "O toString() de ENTRANCE deve ser (0,0).");
    }
}