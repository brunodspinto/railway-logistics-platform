#include <string.h>
#include <stdio.h>
#include "extract_data.h"
#include "sensors_manager.h"

int send_cmd_to_sensors(const char *cmd);              // função externa (hw)
int wait_for_data_from_sensors(char *buffer, int max); // função externa (hw)

// ========= BUFFER: INSERIR VALOR =========
static void add_value(SensorBuffer *sb, int v){
    sb->values[sb->head] = v;               // inserir valor
    sb->head = (sb->head + 1) % 10;         // avanço circular

    if(sb->nelem < sb->win) sb->nelem++;    // buffer ainda não cheio
    else sb->tail = (sb->tail + 1) % 10;    // descartar mais antigo
}

// ========= MEDIANA (FILTRAGEM) =========
static int calc_median(SensorBuffer *sb){
    int tmp[10], i;
    for(i=0;i<sb->nelem;i++)
        tmp[i] = sb->values[(sb->tail+i) % 10]; // copiar valores válidos

    for(int i=0;i<sb->nelem-1;i++)
        for(int j=i+1;j<sb->nelem;j++)
            if(tmp[j] < tmp[i]){
                int t=tmp[i]; tmp[i]=tmp[j]; tmp[j]=t; // ordenação
            }

    return tmp[ sb->nelem/2 ];              // valor mediano
}

// ========= OBTENÇÃO E PROCESSAMENTO DE SENSORES =========
int manager_get_sensors_data(SensorData *out){
    char cmd[]="GTH\n";                     // comando sensores
    char buffer[128];
    char unit[20];
    int value,n;

    if(!send_cmd_to_sensors(cmd)) return 0; // erro no envio
    n=wait_for_data_from_sensors(buffer,sizeof(buffer)-1);
    if(n<=0) return 0;                      // erro na receção

    buffer[n]='\0';                         // terminar string

    // ---- TEMP ----
    if(!extract_data(buffer,"TEMP",unit,&value)) return 0; // extrair temperatura
    if(out->temperature.win==0) out->temperature.win=5;    // janela default
    add_value(&out->temperature,value);
    strncpy(out->temperature.unit,unit,19);
    out->temperature.last=calc_median(&out->temperature);  // filtragem

    // ---- HUM ----
    if(!extract_data(buffer,"HUM",unit,&value)) return 0;  // extrair humidade
    if(out->humidity.win==0) out->humidity.win=5;
    add_value(&out->humidity,value);
    strncpy(out->humidity.unit,unit,19);
    out->humidity.last=calc_median(&out->humidity);        // filtragem

    printf("Temperatura filtrada: %d %s\n",out->temperature.last,out->temperature.unit); // debug
    printf("Humidade filtrada   : %d %s\n",out->humidity.last,out->humidity.unit);       // debug

    return 1;                              // sucesso
}