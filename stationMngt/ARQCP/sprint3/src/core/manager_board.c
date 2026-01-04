#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "structures.h"
#include "sensors_manager.h"
#include "board.h"

// ================== USAC15 ==================
void manager_send_data_to_board(StationSystem *sys){
    BoardData bd;

    bd.num_tracks = sys->tracks.count;                         // nº de vias
    bd.tracks = malloc(sizeof(BoardTrack)*bd.num_tracks);      // alocar vias

    if (bd.tracks) {
        for(int i=0;i<bd.num_tracks;i++){
            bd.tracks[i].id = sys->tracks.data[i].id;          // id da via
            bd.tracks[i].state = sys->tracks.data[i].state;   // estado da via
            bd.tracks[i].train_id = sys->tracks.data[i].assigned_train_id; // comboio
        }

        bd.temperature = sys->sensors.temperature.last;        // temperatura filtrada
        bd.humidity    = sys->sensors.humidity.last;           // humidade filtrada
        strcpy(bd.temp_unit, sys->sensors.temperature.unit);   // unidade temperatura
        strcpy(bd.hum_unit,  sys->sensors.humidity.unit);      // unidade humidade

        board_show(&bd);                                       // envio para board

        free(bd.tracks);                                      // libertar memória
    }
}

// ======================= MAIN DO MANAGER =======================
/* COMENTADO PARA NÃO DAR CONFLITO COM O MAIN DA APP FINAL
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
*/