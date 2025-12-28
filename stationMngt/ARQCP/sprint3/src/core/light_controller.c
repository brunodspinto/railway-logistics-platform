#include "light_controller.h"
#include "serial_comm.h"
#include "asm_functions.h"
#include <stdio.h>
#include <string.h>

// ========================================
// Variável global - File descriptor serial
// ========================================

static int serial_fd = -1;

// ========================================
// Inicializar Light Controller
// ========================================

int light_controller_init(const char* serial_port) {
    // Abrir porta serial a 9600 baud
    serial_fd = serial_open(serial_port, 9600);

    if (serial_fd < 0) {
        fprintf(stderr, "ERRO: Não foi possível abrir %s\n", serial_port);
        return 0;
    }

    printf("✓ Light Controller inicializado\n");
    return 1;
}

// ========================================
// Fechar Light Controller
// ========================================

void light_controller_close() {
    if (serial_fd >= 0) {
        serial_close(serial_fd);
        serial_fd = -1;
    }
}

// ========================================
// USAC14 - Função Principal
// Controlar luz baseado no estado da via
// ========================================

int set_track_light(Track* track) {
    if (serial_fd < 0) {
        fprintf(stderr, "ERRO: Serial não inicializada. ");
        fprintf(stderr, "Chame light_controller_init() primeiro.\n");
        return 0;
    }

    if (!track) {
        fprintf(stderr, "ERRO: Track inválida (NULL)\n");
        return 0;
    }

    // Determinar comando baseado no estado
    char op[4];

    switch (track->state) {
        case TRACK_FREE:
            strcpy(op, "GE");  // Green - Via livre
            break;
        case TRACK_ASSIGNED:
            strcpy(op, "YE");  // Yellow - Via atribuída
            break;
        case TRACK_BUSY:
            strcpy(op, "RE");  // Red - Via ocupada
            break;
        case TRACK_INOPERATIVE:
            strcpy(op, "RB");  // Red Blink - Via inoperativa
            break;
        default:
            fprintf(stderr, "ERRO: Estado inválido (%d)\n", track->state);
            return 0;
    }

    // Usar USAC04 (Assembly) para formatar comando
    char cmd[20];
    int result = format_command(op, track->id, cmd);

    if (!result) {
        fprintf(stderr, "ERRO: Falha ao formatar comando\n");
        return 0;
    }

    // Enviar comando pela serial
    if (!serial_send(serial_fd, cmd)) {
        fprintf(stderr, "ERRO: Falha ao enviar comando\n");
        return 0;
    }

    const char* state_names[] = {"FREE", "ASSIGNED", "BUSY", "INOPERATIVE"};
    printf("✓ Via %02d: %s → Comando: %s\n",
           track->id,
           state_names[track->state],
           cmd);

    return 1;
}

// ========================================
// Funções Auxiliares - Controle direto
// ========================================

int set_track_green(int track_id) {
    if (serial_fd < 0) {
        fprintf(stderr, "ERRO: Serial não inicializada\n");
        return 0;
    }

    char cmd[20];

    // Usar USAC04 (Assembly)
    if (!format_command("GE", track_id, cmd)) {
        fprintf(stderr, "ERRO: Falha ao formatar comando GREEN\n");
        return 0;
    }

    return serial_send(serial_fd, cmd);
}

int set_track_yellow(int track_id) {
    if (serial_fd < 0) {
        fprintf(stderr, "ERRO: Serial não inicializada\n");
        return 0;
    }

    char cmd[20];

    // Usar USAC04 (Assembly)
    if (!format_command("YE", track_id, cmd)) {
        fprintf(stderr, "ERRO: Falha ao formatar comando YELLOW\n");
        return 0;
    }

    return serial_send(serial_fd, cmd);
}

int set_track_red(int track_id) {
    if (serial_fd < 0) {
        fprintf(stderr, "ERRO: Serial não inicializada\n");
        return 0;
    }

    char cmd[20];

    //  Usar USAC04 (Assembly)
    if (!format_command("RE", track_id, cmd)) {
        fprintf(stderr, "ERRO: Falha ao formatar comando RED\n");
        return 0;
    }

    return serial_send(serial_fd, cmd);
}

int set_track_blink(int track_id) {
    if (serial_fd < 0) {
        fprintf(stderr, "ERRO: Serial não inicializada\n");
        return 0;
    }

    char cmd[20];

    // Usar USAC04 (Assembly)
    if (!format_command("RB", track_id, cmd)) {
        fprintf(stderr, "ERRO: Falha ao formatar comando BLINK\n");
        return 0;
    }

    return serial_send(serial_fd, cmd);
}