#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "light_controller.h"
#include "config_loader.h"
#include "structures.h"

// =========================================================
// MOCKS
// =========================================================

// Mock para Sensores
int send_cmd_to_sensors(const char *cmd) { (void)cmd; return 1; }
int wait_for_data_from_sensors(char *buffer, int max) { (void)buffer; (void)max; return 0; }

// Mock para Assembly USAC03
int extract_data(char* str, char* token, char* unit, int* value) {
    (void)str; (void)token; (void)unit; (void)value;
    return 0;
}

// REMOVIDO: manager_send_data_to_board (Agora usamos a real)

// =========================================================
// TESTE USAC14 - HARDWARE REAL
// =========================================================

int main() {
    printf("========================================\n");
    printf("  TESTE USAC14 - HARDWARE REAL\n");
    printf("========================================\n");

    // 1. Criar a estrutura principal do sistema
    StationSystem system = {0};

    // Pedir porta ao utilizador
    char port[50];
    printf("\nPorta serial (ex: /dev/ttyACM0): ");
    scanf("%s", port);

    // Inicializar controlador de luzes
    if (light_controller_init(port) == 0) {
        printf("❌ Erro ao abrir %s\n", port);
        return 1;
    }

    printf("✓ Ligado ao Arduino em %s\n\n", port);

    // 2. Carregar configuração
    if (load_configuration("config/station_config.txt", &system) != 1) {
        printf("❌ Erro ao carregar config\n");
        light_controller_close();
        return 1;
    }

    printf("✓ Configuração carregada\n");
    printf("  Tracks carregadas: %d\n\n", system.tracks.count);

    // 3. Testes interativos com Hardware
    if (system.tracks.count > 0) {
        Track *test_track = &system.tracks.data[0];
        printf("--- Teste Interativo (Track ID: %d) ---\n", test_track->id);

        printf("Estado: FREE (Verde)\n");
        test_track->state = TRACK_FREE;
        set_track_light(test_track);
        sleep(2);

        printf("Estado: ASSIGNED (Amarelo)\n");
        test_track->state = TRACK_ASSIGNED;
        set_track_light(test_track);
        sleep(2);

        printf("Estado: BUSY (Vermelho)\n");
        test_track->state = TRACK_BUSY;
        set_track_light(test_track);
        sleep(2);

        printf("Estado: INOPERATIVE (Vermelho a piscar)\n");
        test_track->state = TRACK_INOPERATIVE;
        set_track_light(test_track);
        sleep(4);

        printf("\n✓ Testes concluídos\n");
    } else {
        printf("⚠️  Aviso: Nenhuma track encontrada na configuração para testar.\n");
    }

    // 4. Limpeza
    light_controller_close();
    if(system.tracks.data) free(system.tracks.data);

    return 0;
}