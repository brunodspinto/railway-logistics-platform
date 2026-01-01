#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "light_controller.h"
#include "structures.h"

// =========================================================
// MOCKS
// =========================================================

// Mock para Sensores (Hardware)
int send_cmd_to_sensors(const char *cmd) { (void)cmd; return 1; }
int wait_for_data_from_sensors(char *buffer, int max) { (void)buffer; (void)max; return 0; }

// Mock para Assembly USAC03 (Extract Data)
int extract_data(char* str, char* token, char* unit, int* value) {
    (void)str; (void)token; (void)unit; (void)value;
    return 0;
}

// REMOVIDO: manager_send_data_to_board (Agora usamos a real do manager_board.c)

// =========================================================
// TESTE USAC14 (Luzes com Mock Serial)
// =========================================================

int main() {
    printf("========================================\n");
    printf("  TESTE USAC14 - LIGHT CONTROLLER (MOCK)\n");
    printf("========================================\n");

    // 1. Inicializar com porta virtual
    if (light_controller_init(NULL) == 0) { // NULL ativa modo mock interno se implementado
        printf("⚠️  Aviso: Falha ao abrir porta (normal se for teste sem hardware).\n");
    } else {
        printf("✓ Controlador inicializado.\n");
    }

    // 2. Criar uma Track de teste
    Track t1;
    t1.id = 1;
    t1.state = TRACK_FREE;

    // 3. Testar sequência de luzes
    printf("\n--- Teste de Sequência de Cores ---\n");

    // Verde
    t1.state = TRACK_FREE;
    set_track_light(&t1);
    printf("Estado FREE (Verde) -> Enviado comando.\n");
    sleep(1);

    // Amarelo
    t1.state = TRACK_ASSIGNED;
    set_track_light(&t1);
    printf("Estado ASSIGNED (Amarelo) -> Enviado comando.\n");
    sleep(1);

    // Vermelho
    t1.state = TRACK_BUSY;
    set_track_light(&t1);
    printf("Estado BUSY (Vermelho) -> Enviado comando.\n");
    sleep(1);

    // Vermelho a piscar
    t1.state = TRACK_INOPERATIVE;
    set_track_light(&t1);
    printf("Estado INOPERATIVE (Piscar) -> Enviado comando.\n");
    sleep(1);

    // 4. Fechar
    light_controller_close();
    printf("\n✓ Teste USAC14 concluído.\n");

    return 0;
}