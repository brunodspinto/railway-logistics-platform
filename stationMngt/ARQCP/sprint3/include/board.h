#ifndef BOARD_H
#define BOARD_H

#include "structures.h"
#include "sensors_manager.h"

// ========= ESTRUTURA DE CADA TRACK =========
typedef struct {
    int id;                // identificador da via
    TrackState state;      // estado atual (livre/ocupada/etc)
    int train_id;          // id do comboio presente (-1 se nenhum)
} BoardTrack;

// ========= DADOS GLOBAIS DO BOARD =========
typedef struct {
    int num_tracks;        // nº total de vias
    BoardTrack *tracks;    // array de vias
    int temperature;       // valor de temperatura
    int humidity;          // valor de humidade
    char temp_unit[20];    // unidade da temperatura
    char hum_unit[20];     // unidade da humidade
} BoardData;

// ========= FUNÇÃO DE APRESENTAÇÃO =========
void board_show(const BoardData *data);

#endif