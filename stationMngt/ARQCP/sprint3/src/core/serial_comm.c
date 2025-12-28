#include "serial_comm.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <errno.h>

// ========================================
// Abrir porta serial
// ========================================

int serial_open(const char* port, int baudrate) {
    // Abrir porta
    int fd = open(port, O_RDWR | O_NOCTTY | O_NDELAY);

    if (fd == -1) {
        perror("Erro ao abrir porta serial");
        return -1;
    }

    // Configurar termios
    struct termios options;
    tcgetattr(fd, &options);

    // Configurar baudrate
    speed_t speed;
    switch(baudrate) {
        case 9600:
            speed = B9600;
            break;
        case 115200:
            speed = B115200;
            break;
        default:
            speed = B9600;
    }

    cfsetispeed(&options, speed);
    cfsetospeed(&options, speed);

    // 8N1: 8 bits, sem paridade, 1 stop bit
    options.c_cflag &= ~PARENB;        // Sem paridade
    options.c_cflag &= ~CSTOPB;        // 1 stop bit
    options.c_cflag &= ~CSIZE;         // Limpar tamanho
    options.c_cflag |= CS8;            // 8 bits

    // Habilitar leitura e ignorar status lines
    options.c_cflag |= (CLOCAL | CREAD);

    // Raw mode (sem processamento)
    options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
    options.c_oflag &= ~OPOST;
    options.c_iflag &= ~(IXON | IXOFF | IXANY);

    // Timeout: 1 decissegundo (0.1s)
    options.c_cc[VMIN]  = 0;
    options.c_cc[VTIME] = 1;

    // Aplicar configurações
    tcsetattr(fd, TCSANOW, &options);

    // Limpar buffers
    tcflush(fd, TCIOFLUSH);

    printf("✓ Porta serial aberta: %s @ %d bps\n", port, baudrate);

    return fd;
}

// ========================================
// Fechar porta serial
// ========================================

void serial_close(int fd) {
    if (fd >= 0) {
        close(fd);
        printf("✓ Porta serial fechada\n");
    }
}

// ========================================
// Enviar comando
// ========================================

int serial_send(int fd, const char* command) {
    if (fd < 0) {
        fprintf(stderr, "ERRO: Porta serial não está aberta\n");
        return 0;
    }

    // Adicionar \n ao comando
    char buffer[128];
    snprintf(buffer, sizeof(buffer), "%s\n", command);

    int len = strlen(buffer);
    int written = write(fd, buffer, len);

    if (written != len) {
        perror("Erro ao enviar comando");
        return 0;
    }

    // Garantir que foi enviado (flush)
    tcdrain(fd);

    printf("→ Enviado: %s", buffer);

    return 1;
}

// ========================================
// Receber dados (para USAC13)
// ========================================

int serial_receive(int fd, char* buffer, int max_len) {
    if (fd < 0) {
        fprintf(stderr, "ERRO: Porta serial não está aberta\n");
        return -1;
    }

    int bytes_read = read(fd, buffer, max_len - 1);

    if (bytes_read > 0) {
        buffer[bytes_read] = '\0';
        return bytes_read;
    }

    return 0;
}