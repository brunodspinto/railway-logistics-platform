#ifndef SENSORS_MANAGER_H
#define SENSORS_MANAGER_H

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

// ========= ESTRUTURA GERAL =========
typedef struct {
    SensorBuffer temperature; // dados de temperatura
    SensorBuffer humidity;    // dados de humidade
} SensorData;

// ========= FUNÇÃO PRINCIPAL =========
int manager_get_sensors_data(SensorData *out);

#endif