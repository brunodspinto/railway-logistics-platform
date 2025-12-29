#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "board.h"

// ========= STRING DO ESTADO =========
static const char *state_str(TrackState s){
    switch(s){
        case TRACK_FREE:        return "FREE";
        case TRACK_BUSY:        return "BUSY";
        case TRACK_ASSIGNED:    return "ASSIGNED";
        case TRACK_INOPERATIVE: return "INOPERATIVE";
        default:                return "UNKNOWN";
    }
}

// ========= MOSTRAR BOARD =========
void board_show(const BoardData *d){
    printf("\n========= SYNOPTIC BOARD =========\n");
    printf("Temperature: %d %s\n", d->temperature, d->temp_unit);
    printf("Humidity   : %d %s\n\n", d->humidity, d->hum_unit);

    printf("TRACK STATUS:\n");
    for(int i=0;i<d->num_tracks;i++){
        BoardTrack *t=&d->tracks[i];
        if(t->train_id>=0)
            printf("Track %d   %-12s Train %d\n", t->id, state_str(t->state), t->train_id);
        else
            printf("Track %d   %-12s (no train)\n", t->id, state_str(t->state));
    }

    printf("=================================\n\n");
}