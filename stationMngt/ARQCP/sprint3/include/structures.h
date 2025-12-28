#ifndef STRUCTURES_H
#define STRUCTURES_H

#include "types.h"
#include <time.h>

// ============ USER ============
typedef struct {
    char name[50];
    char username[30];
    char encrypted_password[30];
    int caesar_key;
} User;

typedef struct {
    User* data;
    int count;
    int capacity;
} UserList;

// ============ TRACK ============
typedef struct {
    int id;
    TrackState state;
    int assigned_train_id;
} Track;

typedef struct {
    Track* data;
    int count;
    int capacity;
} TrackList;

// ============ TRAIN ============
typedef struct {
    int id;
} Train;

typedef struct {
    Train* data;
    int count;
    int capacity;
} TrainList;

// ============ SENSOR ============
typedef struct {
    int* buffer;
    int buffer_length;
    int median_window;
    int nelem;
    int tail;
    int head;
} SensorConfig;

typedef struct {
    SensorConfig temperature;
    SensorConfig humidity;
} SensorData;

// ============ LOG ============
typedef struct {
    int id;
    char username[30];
    char action[200];
    char timestamp[30];
} LogEntry;

typedef struct {
    LogEntry* data;
    int count;
    int capacity;
} LogList;

// ============ SYSTEM ============
typedef struct {
    UserList users;
    TrackList tracks;
    TrainList trains;
    SensorData sensors;
    LogList logs;
} StationSystem;

#endif