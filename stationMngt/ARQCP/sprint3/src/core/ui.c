#include <stdio.h>
#include "sensors_manager.h"

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

// ========= MENU PRINCIPAL =========
void menu(){
    int op;
    while(1){
        printf("\n1 - Ler Sensores\n0 - Sair\n> ");
        scanf("%d",&op);

        if(op==1) handle_get_sensors_option();
        else if(op==0) return;
        else printf("Opcao invalida\n");
    }
}