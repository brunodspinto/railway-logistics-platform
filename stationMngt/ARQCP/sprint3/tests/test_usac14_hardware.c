#include <stdio.h>
#include <unistd.h>
#include "light_controller.h"
#include "config_loader.h"

int main() {
    printf("========================================\n");
    printf("  TESTE USAC14 - HARDWARE REAL\n");
    printf("========================================\n");

    // 1. Criar a estrutura principal do sistema
    StationSystem system;

    // Pedir porta ao utilizador
    char port[50];
    printf("\nPorta serial (ex: /dev/ttyACM0): ");
    scanf("%s", port);

    // Inicializar controlador de luzes
    // CORREÇÃO: A função retorna 1 em sucesso. Só é erro se for == 0.
    if (light_controller_init(port) == 0) {
        printf("❌ Erro ao abrir %s\n", port);
        return 1;
    }

    printf("✓ Ligado ao Arduino em %s\n\n", port);

    // 2. Carregar configuração (Usando a tua função correta)
    // A tua função retorna 1 em caso de sucesso, 0 em erro
    if (load_configuration("config/station_config.txt", &system) != 1) {
        printf("❌ Erro ao carregar config\n");
        light_controller_close();
        return 1;
    }

    printf("✓ Configuração carregada\n");
    printf("  Tracks carregadas: %d\n\n", system.tracks.count);

    // 3. Testes interativos
    if (system.tracks.count > 0) {
        // Vamos usar a primeira track da lista para testar
        Track *test_track = &system.tracks.data[0];

        printf("--- Teste Interativo (Track ID: %d) ---\n", test_track->id);

        printf("Estado: FREE (Verde)\n");
        test_track->state = (TrackState)0; // 0 = TRACK_FREE
        set_track_light(test_track);
        sleep(3);

        printf("Estado: ASSIGNED (Amarelo)\n");
        test_track->state = (TrackState)1; // 1 = TRACK_ASSIGNED
        set_track_light(test_track);
        sleep(3);

        printf("Estado: BUSY (Vermelho)\n");
        test_track->state = (TrackState)2; // 2 = TRACK_BUSY
        set_track_light(test_track);
        sleep(3);

        printf("Estado: INOPERATIVE (Vermelho a piscar)\n");
        test_track->state = (TrackState)3; // 3 = TRACK_INOPERATIVE
        set_track_light(test_track);
        sleep(5); // Mais tempo para ver piscar

        printf("\n✓ Testes concluídos\n");
    } else {
        printf("⚠️  Aviso: Nenhuma track encontrada na configuração para testar.\n");
    }

    // 4. Limpeza e fecho (Usando a tua função correta)
    light_controller_close();
    free_station_system(&system);

    return 0;
}