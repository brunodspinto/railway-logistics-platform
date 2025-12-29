#include "config_loader.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINE 256
#define INITIAL_CAPACITY 10

// ========== FUNÇÕES AUXILIARES ==========

static void init_user_list(UserList* list) {
    list->data = malloc(INITIAL_CAPACITY * sizeof(User));
    list->count = 0;
    list->capacity = INITIAL_CAPACITY;
}

static void init_track_list(TrackList* list) {
    list->data = malloc(INITIAL_CAPACITY * sizeof(Track));
    list->count = 0;
    list->capacity = INITIAL_CAPACITY;
}

static void init_train_list(TrainList* list) {
    list->data = malloc(INITIAL_CAPACITY * sizeof(Train));
    list->count = 0;
    list->capacity = INITIAL_CAPACITY;
}

static void init_log_list(LogList* list) {
    list->data = malloc(INITIAL_CAPACITY * sizeof(LogEntry));
    list->count = 0;
    list->capacity = INITIAL_CAPACITY;
}

static void init_sensor_config(SensorConfig* config, int buf_size, int window) {
    config->buffer = calloc(buf_size, sizeof(int));
    config->buffer_length = buf_size;
    config->median_window = window;
    config->nelem = 0;
    config->tail = 0;
    config->head = 0;
}

// ========== ADD COM EXPANSÃO DINÂMICA ==========

static void add_user(UserList* list, User* user) {
    if (list->count >= list->capacity) {
        list->capacity *= 2;
        list->data = realloc(list->data, list->capacity * sizeof(User));
    }
    list->data[list->count++] = *user;
}

static void add_track(TrackList* list, Track* track) {
    if (list->count >= list->capacity) {
        list->capacity *= 2;
        list->data = realloc(list->data, list->capacity * sizeof(Track));
    }
    list->data[list->count++] = *track;
}

static void add_train(TrainList* list, Train* train) {
    if (list->count >= list->capacity) {
        list->capacity *= 2;
        list->data = realloc(list->data, list->capacity * sizeof(Train));
    }
    list->data[list->count++] = *train;
}

// ========== PARSING ==========

static int parse_user(const char* line, User* user) {
    // USER|nome|username|password|key
    char buf[MAX_LINE];
    strcpy(buf, line);

    char* token = strtok(buf, "|");
    if (!token || strcmp(token, "USER") != 0) return 0;

    token = strtok(NULL, "|");
    if (!token) return 0;
    strncpy(user->name, token, 49);
    user->name[49] = '\0';

    token = strtok(NULL, "|");
    if (!token) return 0;
    strncpy(user->username, token, 29);
    user->username[29] = '\0';

    token = strtok(NULL, "|");
    if (!token) return 0;
    strncpy(user->encrypted_password, token, 29);
    user->encrypted_password[29] = '\0';

    token = strtok(NULL, "|");
    if (!token) return 0;
    user->caesar_key = atoi(token);

    if (user->caesar_key < 1 || user->caesar_key > 26) return 0;

    return 1;
}

static int parse_sensor(const char* line, SensorData* sensors) {
    // SENSOR|tipo|buffer_size|window_size
    char buf[MAX_LINE];
    strcpy(buf, line);

    char* token = strtok(buf, "|");
    if (!token || strcmp(token, "SENSOR") != 0) return 0;

    token = strtok(NULL, "|");
    if (!token) return 0;
    char type[20];
    strcpy(type, token);

    token = strtok(NULL, "|");
    if (!token) return 0;
    int buf_size = atoi(token);

    token = strtok(NULL, "|");
    if (!token) return 0;
    int window = atoi(token);

    if (buf_size <= 0 || window <= 0 || window > buf_size) return 0;

    if (strcmp(type, "TEMPERATURE") == 0) {
        init_sensor_config(&sensors->temperature, buf_size, window);
    } else if (strcmp(type, "HUMIDITY") == 0) {
        init_sensor_config(&sensors->humidity, buf_size, window);
    } else {
        return 0;
    }

    return 1;
}

static int parse_track(const char* line, Track* track) {
    // TRACK|id|state|train_id
    char buf[MAX_LINE];
    strcpy(buf, line);

    char* token = strtok(buf, "|");
    if (!token || strcmp(token, "TRACK") != 0) return 0;

    token = strtok(NULL, "|");
    if (!token) return 0;
    track->id = atoi(token);
    if (track->id < 1 || track->id > 99) return 0;

    token = strtok(NULL, "|");
    if (!token) return 0;
    int state = atoi(token);
    if (state < 0 || state > 3) return 0;
    track->state = (TrackState)state;

    token = strtok(NULL, "|");
    if (!token) return 0;
    track->assigned_train_id = atoi(token);

    return 1;
}

static int parse_train(const char* line, Train* train) {
    // TRAIN|id
    char buf[MAX_LINE];
    strcpy(buf, line);

    char* token = strtok(buf, "|");
    if (!token || strcmp(token, "TRAIN") != 0) return 0;

    token = strtok(NULL, "|");
    if (!token) return 0;
    train->id = atoi(token);

    if (train->id <= 0) return 0;

    return 1;
}

// ========== USAC11 - FUNÇÃO PRINCIPAL ==========

int load_configuration(const char* filename, StationSystem* system) {
    FILE* file = fopen(filename, "r");
    if (!file) {
        printf("ERRO: Não foi possível abrir '%s'\n", filename);
        return 0;
    }

    // Inicializar listas
    init_user_list(&system->users);
    init_track_list(&system->tracks);
    init_train_list(&system->trains);
    init_log_list(&system->logs);

    char line[MAX_LINE];
    int line_num = 0;
    int errors = 0;

    while (fgets(line, sizeof(line), file)) {
        line_num++;

        // Remover \n
        line[strcspn(line, "\n")] = 0;

        // Ignorar vazias e comentários
        if (line[0] == '\0' || line[0] == '#') continue;

        // Processar
        if (strncmp(line, "USER|", 5) == 0) {
            User user;
            if (parse_user(line, &user)) {
                add_user(&system->users, &user);
            } else {
                printf("ERRO linha %d: %s\n", line_num, line);
                errors++;
            }
        }
        else if (strncmp(line, "SENSOR|", 7) == 0) {
            if (!parse_sensor(line, &system->sensors)) {
                printf("ERRO linha %d: %s\n", line_num, line);
                errors++;
            }
        }
        else if (strncmp(line, "TRACK|", 6) == 0) {
            Track track;
            if (parse_track(line, &track)) {
                add_track(&system->tracks, &track);
            } else {
                printf("ERRO linha %d: %s\n", line_num, line);
                errors++;
            }
        }
        else if (strncmp(line, "TRAIN|", 6) == 0) {
            Train train;
            if (parse_train(line, &train)) {
                add_train(&system->trains, &train);
            } else {
                printf("ERRO linha %d: %s\n", line_num, line);
                errors++;
            }
        }
    }

    fclose(file);

    if (errors > 0) {
        printf("\n❌ Configuração carregada com %d erros\n", errors);
        return 0;
    }

    printf("\n✓ Configuração carregada com sucesso!\n");
    printf("  Users: %d\n", system->users.count);
    printf("  Tracks: %d\n", system->tracks.count);
    printf("  Trains: %d\n", system->trains.count);
    printf("  Temp buffer: %d (window: %d)\n",
           system->sensors.temperature.buffer_length,
           system->sensors.temperature.median_window);
    printf("  Hum buffer: %d (window: %d)\n",
           system->sensors.humidity.buffer_length,
           system->sensors.humidity.median_window);

    return 1;
}

// ========== CLEANUP ==========

void free_station_system(StationSystem* system) {
    free(system->users.data);
    free(system->tracks.data);
    free(system->trains.data);
    free(system->logs.data);
    free(system->sensors.temperature.buffer);
    free(system->sensors.humidity.buffer);
}