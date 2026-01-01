#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "light_controller.h"
#include "config_loader.h"
#include "structures.h"

// =========================================================
// MOCKS - A "Vacina" para os erros de Linker
// Estas funções são necessárias porque o Makefile inclui
// sensors_manager.o e track_manager.o, que pedem estas funções.
// =========================================================

// Mock para Sensores
int send_cmd_to_sensors(const char *cmd) { (void)cmd; return 1; }
int wait_for_data_from_sensors(char *buffer, int max) { (void)buffer; (void)max; return 0; }

// Mock para Assembly USAC03 (Extract Data)
// Definimos aqui em C para não precisares de alterar o Makefile
int extract_data(char* str, char* token, char* unit, int* value) {
    (void)str; (void)token; (void)unit; (void)value;
    return 0;
}

// Mock para Track Manager / Board
void manager_send_data_to_board(int track_id, int train_id, int state) {
    (void)track_id; (void)train_id; (void)state;
}

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
    // Lembra-te: na tua lógica, 1 é sucesso.
    if (light_controller_init(port) == 0) {
        printf("❌ Erro ao abrir %s\n", port);
        // Em teste real de hardware, se falhar a abrir, convém sair.
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
        // Vamos usar a primeira track da lista para testar
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
        sleep(4); // Mais tempo para ver piscar

        printf("\n✓ Testes concluídos\n");
    } else {
        printf("⚠️  Aviso: Nenhuma track encontrada na configuração para testar.\n");
    }

    // 4. Limpeza e fecho
    light_controller_close();
    // Limpar memória alocada pelo config_loader (se necessário)
    if(system.tracks.data) free(system.tracks.data);
    if(system.trains.data) free(system.trains.data);

    return 0;
}