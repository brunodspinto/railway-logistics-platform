#ifndef ASM_FUNCTIONS_H
#define ASM_FUNCTIONS_H

#include "structures.h"

// USAC01
int encrypt_data(char* in, int key, char* out);

// USAC02
int decrypt_data(char* in, int key, char* out);

// USAC03
int extract_data(char* str, char* token, char* unit, int* value);

// USAC04
int format_command(char* op, int n, char* cmd);

// USAC05
int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value);

// USAC06
int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value);

// USAC07
int move_n_to_array(int* buffer, int length, int* nelem, int* tail, int* head, int n, int* array);

// USAC08
int sort_array(int* vec, int length, char order);

// USAC09
int median(int* vec, int length, int* me);

// SPRINT3
// USAC14
int generate_command_from_track(Track* track, char* cmd);

#endif