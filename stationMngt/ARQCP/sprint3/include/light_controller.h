#ifndef LIGHT_CONTROLLER_H
#define LIGHT_CONTROLLER_H

#include "structures.h"

// Inicializar controlador de luzes
int light_controller_init(const char* serial_port);

// Fechar controlador
void light_controller_close();

// USAC14 - Controlar luz de uma via
int set_track_light(Track* track);

// Funções auxiliares específicas
int set_track_green(int track_id);    // Via livre
int set_track_yellow(int track_id);   // Via atribuída
int set_track_red(int track_id);      // Via ocupada
int set_track_blink(int track_id);    // Via inoperativa
int light_controller_get_fd();

#endif