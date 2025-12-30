#ifndef TRACK_MANAGER_H
#define TRACK_MANAGER_H

#include "structures.h"

// Inicializa ou configura o gestor (se necessário)
void track_manager_init(StationSystem* sys);

// Tenta atribuir uma via a um comboio que chega
// Retorna: ID da via atribuída ou -1 se Emergency Stop (Nenhuma via livre)
int process_train_arrival(StationSystem* sys, int train_id);

// Liberta uma via (Comboio parte)
void process_train_departure(StationSystem* sys, int track_id);

// Coloca uma via como Inoperacional
void set_track_unavailable(StationSystem* sys, int track_id);

// Coloca uma via como Operacional (Livre) se estava inoperacional
void set_track_available(StationSystem* sys, int track_id);

#endif