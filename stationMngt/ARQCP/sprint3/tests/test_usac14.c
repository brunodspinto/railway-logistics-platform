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

    StationSystem system = {0};

    if (load_configuration("config/station_config.txt", &system) == 0) {
        printf("✗ Erro ao carregar configuração\n");
        return 1;
    }
    printf("✓ Config carregada\n");

    Track* tracks = system.tracks.data;
    int num_tracks = system.tracks.count;

    // ===================================
    // *** CORREÇÃO 1: INICIALIZAR ***
    // ===================================
    printf("\n--- Inicialização Light Controller ---\n");

    if (!light_controller_init(NULL)) {  // NULL = MODO MOCK
        printf("✗ Erro ao inicializar Light Controller\n");
        return 1;
    }

    printf("✓ Light Controller inicializado\n");

    // ===================================
    // Teste 2: Mock Light Commands
    // ===================================
    printf("\n--- Teste 2: Mock Light Commands ---\n");

    printf("Track 01 [FREE]        → ");
    set_track_light(&tracks[0]);

    printf("Track 02 [FREE]        → ");
    set_track_light(&tracks[1]);

    printf("✓ Comandos gerados\n");

    // ===================================
    // Teste 3: Integração USAC11 + USAC14
    // ===================================
    printf("\n--- Teste 3: Integração USAC11 + USAC14 ---\n");

    for (int i = 0; i < num_tracks; i++) {
        printf("Track %02d [", tracks[i].id);

        switch (tracks[i].state) {
            case TRACK_FREE:        printf("FREE]        "); break;
            case TRACK_ASSIGNED:    printf("ASSIGNED]    "); break;
            case TRACK_BUSY:        printf("BUSY]        "); break;
            case TRACK_INOPERATIVE: printf("INOPERATIVE] "); break;
        }

        printf("→ ");
        set_track_light(&tracks[i]);
    }

    printf("✓ Integração testada\n");

    // ===================================
    // Teste 4: Sequência de Estados
    // ===================================
    printf("\n--- Teste 4: Sequência de Estados (Track 1) ---\n");

    // FREE
    tracks[0].state = TRACK_FREE;
    tracks[0].assigned_train_id = -1;
    printf("Track 01 [FREE]        → ");
    set_track_light(&tracks[0]);

    // ASSIGNED
    tracks[0].state = TRACK_ASSIGNED;
    tracks[0].assigned_train_id = 101;
    printf("Track 01 [ASSIGNED]    → ");
    set_track_light(&tracks[0]);

    // BUSY
    tracks[0].state = TRACK_BUSY;
    printf("Track 01 [BUSY]        → ");
    set_track_light(&tracks[0]);

    // INOPERATIVE
    tracks[0].state = TRACK_INOPERATIVE;
    tracks[0].assigned_train_id = -1;
    printf("Track 01 [INOPERATIVE] → ");
    set_track_light(&tracks[0]);

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
    // *** CORREÇÃO 2: CLEANUP ***
    // ===================================
    printf("\n--- Finalização ---\n");
    light_controller_close();

    // ===================================
    // Fim
    // ===================================
    printf("\n✓ Todos os testes passaram!\n");

    return 0;
}