#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "structures.h"
#include "sensors_manager.h"
#include "board.h"

// ================== USAC15 ==================
void manager_send_data_to_board(StationSystem *sys){
    BoardData bd;

    bd.num_tracks = sys->tracks.count;
    bd.tracks = malloc(sizeof(BoardTrack)*bd.num_tracks);

    for(int i=0;i<bd.num_tracks;i++){
        bd.tracks[i].id = sys->tracks.data[i].id;
        bd.tracks[i].state = sys->tracks.data[i].state;
        bd.tracks[i].train_id = sys->tracks.data[i].assigned_train_id;
    }

    bd.temperature = sys->sensors.temperature.last;
    bd.humidity    = sys->sensors.humidity.last;
    strcpy(bd.temp_unit, sys->sensors.temperature.unit);
    strcpy(bd.hum_unit,  sys->sensors.humidity.unit);

    board_show(&bd);

    free(bd.tracks);
}

// ======================= MAIN DO MANAGER =======================
int main(){

    StationSystem sys = {0};
    sys.sensors.temperature.win = 5;
    sys.sensors.humidity.win = 5;

    while(1){
        manager_get_sensors_data(&sys.sensors); // lê sensores
        manager_send_data_to_board(&sys);       // USAC15
    }

    return 0;
}