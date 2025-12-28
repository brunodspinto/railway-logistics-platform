#include <stdio.h>
#include <unistd.h>
#include "light_controller.h"
#include "config_loader.h"

int main() {
    printf("========================================\n");
    printf("  TESTE USAC14 - HARDWARE REAL\n");
    printf("========================================\n");

    // Pedir porta ao utilizador
    char port[50];
    printf("\nPorta serial (ex: /dev/ttyACM0): ");
    scanf("%s", port);

    // Inicializar
    if (light_controller_init(port, 9600) != 0) {
        printf("❌ Erro ao abrir %s\n", port);
        return 1;
    }

    printf("✓ Ligado ao Arduino em %s\n\n", port);

    // Carregar config
    if (load_station_config("config/station_config.txt") != 0) {
        printf("❌ Erro ao carregar config\n");
        return 1;
    }

    printf("✓ Configuração carregada\n");
    printf("  Tracks: %d\n\n", get_track_count());

    // Testes interativos
    printf("--- Teste Interativo ---\n");
    printf("Track 1: FREE (Verde)\n");
    tracks[0].state = TRACK_FREE;
    set_track_light(&tracks[0]);
    sleep(3);

    printf("Track 1: ASSIGNED (Amarelo)\n");
    tracks[0].state = TRACK_ASSIGNED;
    set_track_light(&tracks[0]);
    sleep(3);

    printf("Track 1: BUSY (Vermelho)\n");
    tracks[0].state = TRACK_BUSY;
    set_track_light(&tracks[0]);
    sleep(3);

    printf("Track 1: INOPERATIVE (Vermelho a piscar)\n");
    tracks[0].state = TRACK_INOPERATIVE;
    set_track_light(&tracks[0]);
    sleep(5);

    printf("\n✓ Testes concluídos\n");

    light_controller_close();
    cleanup_station_data();

    return 0;
}