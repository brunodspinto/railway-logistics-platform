#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "structures.h"
#include "light_controller.h"
#include "serial_comm.h"
#include "config_loader.h"
#include "ui.c" // Incluímos o c diretamente como estavas a fazer (embora o ideal fosse .h)

// =========================================================
// GLUE CODE (A "Cola" para o Hardware)
// Implementamos aqui as funções que o sensors_manager pede
// =========================================================

int send_cmd_to_sensors(const char *cmd) {
    // Buscar o file descriptor aberto pelo light_controller
    int fd = light_controller_get_fd();
    return serial_send(fd, cmd);
}

int wait_for_data_from_sensors(char *buffer, int max) {
    int fd = light_controller_get_fd();
    // Pequeno delay para dar tempo ao Arduino de responder
    usleep(100000); // 100ms
    return serial_receive(fd, buffer, max);
}

// =========================================================
// MAIN PRINCIPAL
// =========================================================

int main() {
    StationSystem sys = {0};

    printf(">>> A INICIAR STATION CONTROLLER <<<\n");

    // 1. Carregar Configuração
    // Cria um ficheiro dummy se não existir ou ajusta o caminho
    if (!load_configuration("config/station_config.txt", &sys)) {
        printf("⚠️  Aviso: Configuração falhou ou ficheiro inexistente.\n");
        // Continuamos apenas para teste, num sistema real abortaríamos.
    }

    // 2. Inicializar Hardware (Arduino)
    // Tenta abrir a porta. Se falhar, avisa.
    char port[50];
    printf("Porta Serial (ex: /dev/ttyACM0): ");
    scanf("%s", port);

    if (!light_controller_init(port)) {
        printf("❌ Falha ao abrir porta serial. A rodar em modo limitado.\n");
    }

    // 3. Lançar Menu UI
    // A função menu() está no ui.c que incluímos
    menu(&sys);

    // 4. Limpeza
    light_controller_close();
    if(sys.tracks.data) free(sys.tracks.data);

    printf("Sistema encerrado.\n");
    return 0;
}