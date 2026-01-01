#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "structures.h"
#include "track_manager.h"
#include "light_controller.h"

// =========================================================
// MOCKS - Funções Falsas para enganar o Linker
// Necessárias porque o sensors_manager.o e track_manager.o
// estão no Makefile mas não temos o hardware real aqui.
// =========================================================

int send_cmd_to_sensors(const char *cmd) {
    (void)cmd; // Ignorar warning de "não usado"
    return 1;  // Fingir sucesso
}

int wait_for_data_from_sensors(char *buffer, int max) {
    (void)buffer;
    (void)max;
    // Retornar 0 bytes lidos para simular que não há dados novos
    return 0;
}

// CORREÇÃO CRÍTICA: Adicionado este Mock para resolver o erro do Linker
void manager_send_data_to_board(int track_id, int train_id, int state) {
    // Simplesmente ignoramos os parâmetros para o teste compilar
    (void)track_id;
    (void)train_id;
    (void)state;
}

// =========================================================
// SETUP DO SISTEMA
// =========================================================

void setup_system(StationSystem *sys, int num_tracks) {
    sys->tracks.count = num_tracks;
    sys->tracks.data = (Track*) calloc(num_tracks, sizeof(Track));

    if (!sys->tracks.data) {
        fprintf(stderr, "Erro fatal de memória no setup.\n");
        exit(1);
    }

    for(int i=0; i<num_tracks; i++) {
        sys->tracks.data[i].id = i + 1; // Vias 1, 2, 3...
        sys->tracks.data[i].state = TRACK_FREE;
        sys->tracks.data[i].assigned_train_id = -1;
    }

    // Inicializar controlador de luzes com porta falsa
    // Vai falhar a abrir a porta, mas permite que o código corra sem crashar
    // As funções set_track_light vão apenas imprimir erro no stderr, o que é OK para teste lógico.
    light_controller_init("/dev/null");
}

void teardown_system(StationSystem *sys) {
    if (sys->tracks.data) {
        free(sys->tracks.data);
    }
    light_controller_close();
}

void print_test_header(const char* title) {
    printf("\n=== TEST: %s ===\n", title);
}

// =========================================================
// MAIN
// =========================================================

int main() {
    StationSystem sys = {0};

    // Setup: Criar estação pequena com APENAS 2 VIAS para facilitar teste de cheio
    setup_system(&sys, 2);

    printf(">>> INÍCIO DOS TESTES USAC16 (Lógica de Vias) <<<\n");

    // -----------------------------------------------------
    // TESTE 1: Chegada de Comboio (Sucesso)
    // -----------------------------------------------------
    print_test_header("Chegada Normal (Comboio 101)");
    // Esperamos que vá para a via 1
    int track_id = process_train_arrival(&sys, 101);

    if (track_id == 1 && sys.tracks.data[0].state == TRACK_ASSIGNED) {
        printf("PASS: Comboio 101 atribuído à Via %d corretamente.\n", track_id);
    } else {
        printf("FAIL: Esperado Via 1, obtido Via %d (Estado: %d).\n", track_id, sys.tracks.data[0].state);
    }

    // -----------------------------------------------------
    // TESTE 2: Ocupar a segunda via
    // -----------------------------------------------------
    print_test_header("Ocupar Restante (Comboio 102)");
    // Esperamos que vá para a via 2
    track_id = process_train_arrival(&sys, 102);

    if (track_id == 2) {
        printf("PASS: Comboio 102 atribuído à Via %d.\n", track_id);
    } else {
        printf("FAIL: Esperado Via 2, obtido Via %d.\n", track_id);
    }

    // -----------------------------------------------------
    // TESTE 3: Estação Cheia (Emergency Stop)
    // -----------------------------------------------------
    print_test_header("Estação Cheia (Emergency Stop)");
    track_id = process_train_arrival(&sys, 103); // Comboio 103 (Não cabe!)

    if (track_id == -1) {
        printf("PASS: Emergency Stop acionado corretamente (retornou -1).\n");
    } else {
        printf("FAIL: Devia ter dado erro (-1), mas atribuiu via %d.\n", track_id);
    }

    // -----------------------------------------------------
    // TESTE 4: Libertar Via
    // -----------------------------------------------------
    print_test_header("Partida de Comboio (Via 1)");
    process_train_departure(&sys, 1); // Libertar Via 1

    if (sys.tracks.data[0].state == TRACK_FREE) {
        printf("PASS: Via 1 está LIVRE novamente.\n");
    } else {
        printf("FAIL: Via 1 não ficou livre (Estado atual: %d).\n", sys.tracks.data[0].state);
    }

    // -----------------------------------------------------
    // TESTE 5: Manutenção
    // -----------------------------------------------------
    print_test_header("Colocar em Manutenção (Via 1)");
    set_track_unavailable(&sys, 1); // Bloquear Via 1

    if (sys.tracks.data[0].state == TRACK_INOPERATIVE) {
        printf("PASS: Via 1 está INOPERACIONAL.\n");
    } else {
        printf("FAIL: Estado incorreto na Via 1.\n");
    }

    // -----------------------------------------------------
    // TESTE 6: Tentar usar via em manutenção
    // -----------------------------------------------------
    // Cenário: Via 1 Inoperacional. Via 2 Ocupada (pelo comboio 102).
    // Resultado esperado: Emergency Stop (-1).
    print_test_header("Chegada com Via em Manutenção");
    track_id = process_train_arrival(&sys, 104);

    if (track_id == -1) {
        printf("PASS: Não atribuiu via (Correto: Vias ocupadas ou inoperacionais).\n");
    } else {
        printf("FAIL: Atribuiu via %d indevidamente (Via 1 devia estar fechada).\n", track_id);
    }

    teardown_system(&sys);
    printf("\n>>> FIM DOS TESTES <<<\n");
    return 0;
}