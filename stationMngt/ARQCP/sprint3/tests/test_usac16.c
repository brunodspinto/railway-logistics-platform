#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "structures.h"
#include "track_manager.h"
#include "light_controller.h"

// Setup simples do sistema para teste
void setup_system(StationSystem *sys, int num_tracks) {
    sys->tracks.count = num_tracks;
    sys->tracks.data = (Track*) calloc(num_tracks, sizeof(Track));

    for(int i=0; i<num_tracks; i++) {
        sys->tracks.data[i].id = i + 1; // Vias 1, 2, 3...
        sys->tracks.data[i].state = TRACK_FREE;
        sys->tracks.data[i].assigned_train_id = -1;
    }

    // Inicializar controlador de luzes em modo MOCK (sem hardware)
    light_controller_init(NULL);
}

void teardown_system(StationSystem *sys) {
    free(sys->tracks.data);
    light_controller_close();
}

void print_test_header(const char* title) {
    printf("\n=== TEST: %s ===\n", title);
}

int main() {
    StationSystem sys = {0};
    setup_system(&sys, 2); // Criar estação pequena com APENAS 2 VIAS para facilitar teste de cheio

    printf(">>> INÍCIO DOS TESTES USAC16 <<<\n");

    // TESTE 1: Chegada de Comboio (Sucesso)
    print_test_header("Chegada Normal");
    int track_id = process_train_arrival(&sys, 101); // Comboio 101
    if (track_id != -1 && sys.tracks.data[0].state == TRACK_ASSIGNED) {
        printf("PASS: Comboio 101 atribuído à Via %d\n", track_id);
    } else {
        printf("FAIL: Erro na atribuição.\n");
    }

    // TESTE 2: Ocupar a segunda via
    print_test_header("Ocupar Restante");
    track_id = process_train_arrival(&sys, 102); // Comboio 102
    printf("PASS: Comboio 102 atribuído à Via %d\n", track_id);

    // TESTE 3: Estação Cheia (Emergency Stop)
    print_test_header("Estação Cheia (Emergency Stop)");
    track_id = process_train_arrival(&sys, 103); // Comboio 103 (Não cabe!)
    if (track_id == -1) {
        printf("PASS: Emergency Stop acionado corretamente (retornou -1).\n");
    } else {
        printf("FAIL: Devia ter dado erro, mas atribuiu via %d.\n", track_id);
    }

    // TESTE 4: Libertar Via
    print_test_header("Partida de Comboio");
    process_train_departure(&sys, 1); // Libertar Via 1
    if (sys.tracks.data[0].state == TRACK_FREE) {
        printf("PASS: Via 1 está LIVRE novamente.\n");
    } else {
        printf("FAIL: Via 1 não ficou livre.\n");
    }

    // TESTE 5: Manutenção
    print_test_header("Colocar em Manutenção");
    set_track_unavailable(&sys, 1); // Bloquear Via 1
    if (sys.tracks.data[0].state == TRACK_INOPERATIVE) {
        printf("PASS: Via 1 está INOPERACIONAL.\n");
    } else {
        printf("FAIL: Estado incorreto.\n");
    }

    // Tentar estacionar na via em manutenção (deve ir para a Via 2 se estiver livre, ou falhar)
    // A Via 2 está ocupada pelo comboio 102. A Via 1 está Inoperacional.
    // Logo, deve dar Emergency Stop de novo.
    print_test_header("Chegada com Via em Manutenção");
    track_id = process_train_arrival(&sys, 104);
    if (track_id == -1) {
        printf("PASS: Não atribuiu via (Correto: Vias ocupadas ou inoperacionais).\n");
    } else {
        printf("FAIL: Atribuiu via %d indevidamente.\n", track_id);
    }

    teardown_system(&sys);
    printf("\n>>> FIM DOS TESTES <<<\n");
    return 0;
}