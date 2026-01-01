#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "light_controller.h"
#include "structures.h"

// =========================================================
// MOCKS - A "Vacina" para os erros de Linker
// Estas funções enganam o compilador para ele não pedir
// o hardware de sensores nem o assembly USAC03.
// =========================================================

// Mock para Sensores (Hardware)
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
// TESTE USAC14 (Luzes com Mock Serial)
// =========================================================

int main() {
    printf("========================================\n");
    printf("  TESTE USAC14 - LIGHT CONTROLLER (MOCK)\n");
    printf("========================================\n");

    // 1. Inicializar com porta virtual (não precisa de Arduino real aqui)
    // O sistema vai usar o mock interno do light_controller ou falhar graciosamente
    if (light_controller_init("/dev/ttyUSB_MOCK") == 0) {
        printf("⚠️  Aviso: Falha esperada ao abrir porta mock (normal se não houver lógica de mock interna).\n");
        printf("   A continuar teste lógico...\n");
    } else {
        printf("✓ Controlador inicializado.\n");
    }

    // 2. Criar uma Track de teste
    Track t1;
    t1.id = 1;
    t1.state = TRACK_FREE; // Começa Livre (Verde)

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