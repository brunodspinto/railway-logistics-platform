#ifndef SENSORS_MANAGER_H
#define SENSORS_MANAGER_H

#include "structures.h"

// ========= BUFFER DE SENSOR =========
typedef struct {
    int values[10];        // armazena valores recebidos
    int nelem;             // nº valores guardados
    int head;              // posição de inserção
    int tail;              // posição do mais antigo
    int win;               // tamanho da janela (para mediana)
    int last;              // último valor calculado (filtrado)
    char unit[20];         // unidade do sensor
} SensorBuffer;

// ========= ESTRUTURA GERAL ==========
//  !!! Esta estrutura tem de ser removida pq já existe em structures.h !!!
//  typedef struct {
//      SensorBuffer temperature;
//      SensorBuffer humidity;
//  } SensorData;

// ========= FUNÇÃO PRINCIPAL =========
int manager_get_sensors_data(SensorData *out);

#endif