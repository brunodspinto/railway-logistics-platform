#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "structures.h"
#include "track_manager.h"
#include "light_controller.h"

// =========================================================
// MOCKS
// =========================================================

// Mock para Sensores (Hardware)
int send_cmd_to_sensors(const char *cmd) { (void)cmd; return 1; }
int wait_for_data_from_sensors(char *buffer, int max) { (void)buffer; (void)max; return 0; }

// Mock para Assembly USAC03 (Extract Data)
// O log_manager e sensors_manager precisam disto, mas como
// vamos adicionar o Assembly real no Makefile, podemos remover este mock
// OU mantê-lo APENAS se não linkarmos o usac03.
// PARA EVITAR ERROS DE DUPLA DEFINIÇÃO COM O ASSEMBLY, VAMOS REMOVER ESTE TAMBÉM:
/* int extract_data(char* str, char* token, char* unit, int* value) {
    (void)str; (void)token; (void)unit; (void)value;
    return 0;
}
*/
// (Se der erro "undefined reference to extract_data", descomenta acima,
// mas o plano é adicionar o ASM no Makefile)

// REMOVIDO: manager_send_data_to_board (Agora usamos a real do manager_board.c)

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

    // Setup: Criar estação pequena com APENAS 2 VIAS
    setup_system(&sys, 2);

    printf(">>> INÍCIO DOS TESTES USAC16 (Lógica de Vias) <<<\n");

    // -----------------------------------------------------
    // TESTE 1: Chegada de Comboio (Sucesso)
    // -----------------------------------------------------
    print_test_header("Chegada Normal (Comboio 101)");
    int track_id = process_train_arrival(&sys, 101);

    if (track_id == 1 && sys.tracks.data[0].state == TRACK_ASSIGNED) {
        printf("PASS: Comboio 101 atribuído à Via %d corretamente.\n", track_id);
    } else {
        printf("FAIL: Esperado Via 1, obtido Via %d.\n", track_id);
    }

    // -----------------------------------------------------
    // TESTE 2: Ocupar a segunda via
    // -----------------------------------------------------
    print_test_header("Ocupar Restante (Comboio 102)");
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
    track_id = process_train_arrival(&sys, 103);

    if (track_id == -1) {
        printf("PASS: Emergency Stop acionado corretamente.\n");
    } else {
        printf("FAIL: Devia ter dado erro (-1), mas atribuiu via %d.\n", track_id);
    }

    // -----------------------------------------------------
    // TESTE 4: Libertar Via
    // -----------------------------------------------------
    print_test_header("Partida de Comboio (Via 1)");
    process_train_departure(&sys, 1);

    if (sys.tracks.data[0].state == TRACK_FREE) {
        printf("PASS: Via 1 está LIVRE novamente.\n");
    } else {
        printf("FAIL: Via 1 não ficou livre.\n");
    }

    // -----------------------------------------------------
    // TESTE 5: Manutenção
    // -----------------------------------------------------
    print_test_header("Colocar em Manutenção (Via 1)");
    set_track_unavailable(&sys, 1);

    if (sys.tracks.data[0].state == TRACK_INOPERATIVE) {
        printf("PASS: Via 1 está INOPERACIONAL.\n");
    } else {
        printf("FAIL: Estado incorreto na Via 1.\n");
    }

    // -----------------------------------------------------
    // TESTE 6: Tentar usar via em manutenção
    // -----------------------------------------------------
    print_test_header("Chegada com Via em Manutenção");
    track_id = process_train_arrival(&sys, 104);

    if (track_id == -1) {
        printf("PASS: Não atribuiu via (Correto).\n");
    } else {
        printf("FAIL: Atribuiu via %d indevidamente.\n", track_id);
    }

    teardown_system(&sys);
    printf("\n>>> FIM DOS TESTES <<<\n");
    return 0;
}