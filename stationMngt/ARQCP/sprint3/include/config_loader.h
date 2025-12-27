#ifndef CONFIG_LOADER_H
#define CONFIG_LOADER_H

#include "structures.h"

// USAC11 - Carregar configuração
int load_configuration(const char* filename, StationSystem* system);

// Libertar memória
void free_station_system(StationSystem* system);

#endif