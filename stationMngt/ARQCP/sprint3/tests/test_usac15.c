#include <stdio.h>
#include <string.h>
#include "../include/board.h"

int main() {

    printf("\n===== TESTE USAC15 - SYNOPTIC BOARD =====\n");

    // ---------- Cenário de teste A ----------
    BoardTrack tracksA[3] = {
        {1, TRACK_FREE,      -1},
        {2, TRACK_ASSIGNED,  1203},
        {3, TRACK_BUSY,      9999}
    };

    BoardData dA;
    dA.num_tracks = 3;
    dA.tracks = tracksA;

    dA.temperature = 23;
    strcpy(dA.temp_unit, "C");
    dA.humidity = 48;
    strcpy(dA.hum_unit, "%");

    printf("\n--- Cenário A ---\n");
    board_show(&dA);


    // ---------- Cenário de teste B ----------
    BoardTrack tracksB[2] = {
        {4, TRACK_INOPERATIVE, -1},
        {5, TRACK_FREE,        -1}
    };

    BoardData dB;
    dB.num_tracks = 2;
    dB.tracks = tracksB;

    dB.temperature = 18;
    strcpy(dB.temp_unit, "C");
    dB.humidity = 60;
    strcpy(dB.hum_unit, "%");

    printf("\n--- Cenário B ---\n");
    board_show(&dB);

    printf("===== FIM TESTE USAC15 =====\n\n");
    return 0;
}