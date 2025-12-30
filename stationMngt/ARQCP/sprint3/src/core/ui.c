#include <stdio.h>
#include "sensors_manager.h"
#include "track_manager.h"

// ========= OPÇÃO DE MENU ("LER SENSORES") =========

void handle_get_sensors_option(){
    static SensorData data={0};   // guarda histórico das leituras

    if(manager_get_sensors_data(&data)){
        printf("\n--- Dados Atuais (Filtrados) ---\n");
        printf("TEMP (mediana) = %d %s\n",data.temperature.last,data.temperature.unit);
        printf("HUM  (mediana) = %d %s\n",data.humidity.last,data.humidity.unit);
    }
    else printf("ERRO ao obter sensores.\n");
}

// ========= OPÇÃO DE MENU ("GESTÃO DE CARRIS") =========

void menu_usac16(StationSystem* sys) {
    int choice = -1;
    int input_id = 0;

    while (choice != 0) {
        printf("\n=== USAC16: Gestão de Carris ===\n");
        printf("1. Registar Chegada de Comboio (Auto Assign)\n");
        printf("2. Registar Partida de Comboio (Libertar Via)\n");
        printf("3. Colocar Via em Manutenção (Inoperacional)\n");
        printf("0. Voltar\n");
        printf("Opção: ");
        scanf("%d", &choice);

        switch (choice) {
            case 1:
                printf(">> Insira ID do Comboio: ");
                scanf("%d", &input_id);
                process_train_arrival(sys, input_id);
                break;

            case 2:
                printf(">> Insira ID da Via a libertar: ");
                scanf("%d", &input_id);
                process_train_departure(sys, input_id);
                break;

            case 3:
                printf(">> Insira ID da Via a bloquear: ");
                scanf("%d", &input_id);
                set_track_unavailable(sys, input_id);
                break;

            case 0:
                break;
            default:
                printf("Opção inválida.\n");
        }
    }
}


// ========= MENU PRINCIPAL =========

void menu(StationSystem* sys){
    int op;
    while(1){
        printf("\n=== STATION CONTROLLER ===\n");
        printf("1 - Ler Sensores\n");
        printf("2 - Gestão de Carris (USAC16)\n");
        printf("0 - Sair\n> ");
        scanf("%d",&op);

        if(op==1) handle_get_sensors_option();
        else if(op==2) menu_usac16(sys);
        else if(op==0) return;
        else printf("Opcao invalida\n");
    }
}