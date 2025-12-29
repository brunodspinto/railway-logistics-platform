#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "extract_data.h"
#include "sensors_manager.h"

// Funções externas (hw ou mock)
int send_cmd_to_sensors(const char *cmd);
int wait_for_data_from_sensors(char *buffer, int max);

// ========= BUFFER: INSERIR VALOR =========
static void add_value(SensorBuffer *sb, int v){
    sb->values[sb->head] = v;
    sb->head = (sb->head + 1) % 10;

    if(sb->nelem < sb->win) sb->nelem++;
    else sb->tail = (sb->tail + 1) % 10;
}

// ========= MEDIANA (FILTRAGEM) =========
static int calc_median(SensorBuffer *sb){
    if (sb->nelem == 0) return 0;

    int tmp[10];
    // Copiar valores
    for(int i=0; i < sb->nelem; i++)
        tmp[i] = sb->values[(sb->tail + i) % 10];

    // Ordenar (Bubble sort)
    for(int i=0; i < sb->nelem - 1; i++)
        for(int j=i+1; j < sb->nelem; j++)
            if(tmp[j] < tmp[i]){
                int t = tmp[i]; tmp[i] = tmp[j]; tmp[j] = t;
            }

    return tmp[sb->nelem / 2];
}

// ========= OBTENÇÃO E PROCESSAMENTO DE SENSORES =========
int manager_get_sensors_data(SensorData *out){
    // O comando para o sensor
    char cmd[] = "GTH\n";
    char buffer[128]; // Buffer para receber a resposta "TEMP&unit..."
    char unit[20];    // Buffer para a unidade
    int value = 0;    // Variável para o valor
    int n;

    // 1. Enviar comando e Receber string
    if(!send_cmd_to_sensors(cmd)) return 0;

    n = wait_for_data_from_sensors(buffer, sizeof(buffer)-1);
    if(n <= 0) return 0;

    buffer[n] = '\0'; // Garantir terminação da string

    // 2. Extrair TEMPERATURA (Usando o teu Assembly)
    // Passamos "TEMP" como token
    if(extract_data(buffer, "TEMP", unit, &value)) {
        if(out->temperature.win == 0) out->temperature.win = 5;

        add_value(&out->temperature, value);
        strncpy(out->temperature.unit, unit, 19);
        out->temperature.last = calc_median(&out->temperature);
    } else {
        printf("Falha ao extrair TEMP do buffer: %s\n", buffer);
    }

    // 3. Extrair HUMIDADE (Usando o teu Assembly)
    // Passamos "HUM" como token
    if(extract_data(buffer, "HUM", unit, &value)) {
        if(out->humidity.win == 0) out->humidity.win = 5;

        add_value(&out->humidity, value);
        strncpy(out->humidity.unit, unit, 19);
        out->humidity.last = calc_median(&out->humidity);
    } else {
         printf("Falha ao extrair HUM do buffer\n");
    }

    // Output para veres acontecer
    printf("   [Leitura] %s\n", buffer);
    printf("   → Temp: %d %s | Hum: %d %s\n",
           out->temperature.last, out->temperature.unit,
           out->humidity.last, out->humidity.unit);

    return 1;
}