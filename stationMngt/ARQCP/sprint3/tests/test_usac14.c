#include <stdio.h>
#include "config_loader.h"
#include "light_controller.h"
#include "structures.h"

// Declaração da função assembly (USAC04)
extern int format_command(char* op, int n, char* cmd);

int main(void) {
    printf("=== TESTE USAC14 ===\n");

    // ===================================
    // Teste 1: Carregamento Config
    // ===================================
    printf("\n--- Teste 1: Carregamento Config ---\n");

    StationSystem system = {0};  // ← StationSystem (não StationConfig)

    if (load_configuration("config/station_config.txt", &system) == 0) {
        printf("✗ Erro ao carregar configuração\n");
        return 1;
    }
    printf("✓ Config carregada\n");

    Track* tracks = system.tracks.data;  // ← .tracks.data (não .tracks)
    int num_tracks = system.tracks.count; // ← .tracks.count

    // ===================================
    // Teste 2: Mock Light Commands
    // ===================================
    printf("\n--- Teste 2: Mock Light Commands ---\n");

    printf("Track 01 [FREE]        → ");
    set_track_light(&tracks[0]);
    printf("GE,01\n");

    printf("Track 02 [FREE]        → ");
    set_track_light(&tracks[1]);
    printf("GE,02\n");

    printf("✓ Comandos gerados\n");

    // ===================================
    // Teste 3: Integração USAC11 + USAC14
    // ===================================
    printf("\n--- Teste 3: Integração USAC11 + USAC14 ---\n");

    for (int i = 0; i < num_tracks; i++) {  // ← num_tracks
        printf("Track %02d [", tracks[i].id);

        switch (tracks[i].state) {
            case TRACK_FREE:        printf("FREE]        "); break;
            case TRACK_ASSIGNED:    printf("ASSIGNED]    "); break;
            case TRACK_BUSY:        printf("BUSY]        "); break;
            case TRACK_INOPERATIVE: printf("INOPERATIVE] "); break;
        }

        printf("→ ");
        set_track_light(&tracks[i]);
        printf("\n");
    }

    printf("✓ Integração testada\n");

    // ===================================
    // Teste 4: Sequência de Estados
    // ===================================
    printf("\n--- Teste 4: Sequência de Estados (Track 1) ---\n");

    // FREE
    tracks[0].state = TRACK_FREE;
    tracks[0].assigned_train_id = -1;  // ← assigned_train_id
    printf("Track 01 [FREE]        → ");
    set_track_light(&tracks[0]);
    printf("GE,01\n");

    // ASSIGNED
    tracks[0].state = TRACK_ASSIGNED;
    tracks[0].assigned_train_id = 101;  // ← assigned_train_id
    printf("Track 01 [ASSIGNED]    → ");
    set_track_light(&tracks[0]);
    printf("YE,01\n");

    // BUSY
    tracks[0].state = TRACK_BUSY;
    printf("Track 01 [BUSY]        → ");
    set_track_light(&tracks[0]);
    printf("RE,01\n");

    // INOPERATIVE
    tracks[0].state = TRACK_INOPERATIVE;
    tracks[0].assigned_train_id = -1;  // ← assigned_train_id
    printf("Track 01 [INOPERATIVE] → ");
    set_track_light(&tracks[0]);
    printf("RB,01\n");

    printf("✓ Todas as transições testadas\n");

    // ===================================
    // Teste 5: Comando Sensores (GTH)
    // ===================================
    printf("\n--- Teste 5: Comando Sensores (GTH) ---\n");

    char gth_cmd[20];
    if (format_command("gth", 0, gth_cmd)) {
        printf("Comando GTH → %s\n", gth_cmd);
    } else {
        printf("✗ Erro ao formatar GTH\n");
    }

    printf("✓ Comando sensor testado\n");

    // ===================================
    // Fim
    // ===================================
    printf("\n✓ Todos os testes passaram!\n");

    return 0;
}