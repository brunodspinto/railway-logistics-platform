#ifndef BOARD_H
#define BOARD_H

#include "structures.h"
#include "sensors_manager.h"

typedef struct {
    int id;
    TrackState state;
    int train_id;
} BoardTrack;

typedef struct {
    int num_tracks;
    BoardTrack *tracks;
    int temperature;
    int humidity;
    char temp_unit[20];
    char hum_unit[20];
} BoardData;

void board_show(const BoardData *data);

#endif