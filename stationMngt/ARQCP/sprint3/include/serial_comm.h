#ifndef SERIAL_COMM_H
#define SERIAL_COMM_H

// Abrir porta serial
int serial_open(const char* port, int baudrate);

// Fechar porta serial
void serial_close(int fd);

// Enviar comando
int serial_send(int fd, const char* command);

// Receber dados (para USAC13)
int serial_receive(int fd, char* buffer, int max_len);

#endif