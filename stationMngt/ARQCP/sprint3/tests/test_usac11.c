#include <stdio.h>
#include "config_loader.h"

int main() {
    StationSystem system;

    printf("=== TESTE USAC11 ===\n");

    if (!load_configuration("config/station_config.txt", &system)) {
        return 1;
    }

    // Mostrar dados carregados
    printf("\n--- USERS ---\n");
    for (int i = 0; i < system.users.count; i++) {
        printf("%d. %s (@%s) - Key:%d - Pass:%s\n",
               i+1,
               system.users.data[i].name,
               system.users.data[i].username,
               system.users.data[i].caesar_key,
               system.users.data[i].encrypted_password);
    }

    printf("\n--- TRACKS ---\n");
    const char* states[] = {"FREE", "ASSIGNED", "BUSY", "INOPERATIVE"};
    for (int i = 0; i < system.tracks.count; i++) {
        printf("Track %02d: %s",
               system.tracks.data[i].id,
               states[system.tracks.data[i].state]);
        if (system.tracks.data[i].assigned_train_id != -1) {
            printf(" (Train %d)", system.tracks.data[i].assigned_train_id);
        }
        printf("\n");
    }

    printf("\n--- TRAINS ---\n");
    for (int i = 0; i < system.trains.count; i++) {
        printf("Train %d\n", system.trains.data[i].id);
    }

    free_station_system(&system);
    printf("\n✓ Teste USAC11 concluído\n");

    return 0;
}