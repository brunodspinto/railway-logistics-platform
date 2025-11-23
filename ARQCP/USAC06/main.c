#include <stdio.h>
#include "dequeue_value.h"

extern int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value);

void print_state(int* buffer, int length, int nelem, int head, int tail) {
    printf("Buffer: [ ");
    for (int i = 0; i < length; i++)
        printf("%d ", buffer[i]);
    printf("]\n");
    printf("head=%d, tail=%d, nelem=%d\n\n", head, tail, nelem);
}

int main() {
    printf("=== USAC06 - dequeue_value ===\n\n");

    int length = 5;
    int buffer[5] = {10, 20, 30, 40, 50}; // valores simulados
    int nelem = 5;   // 5 elementos no buffer
    int head = 0;    // não interessa muito para dequeue
    int tail = 0;    // oldest = buffer[0]

    int result, out;

    print_state(buffer, length, nelem, head, tail);

    // Teste 1: remover 10
    printf("Test 1: Dequeue\n");
    result = dequeue_value(buffer, length, &nelem, &tail, &head, &out);
    printf("Result: %d, Value: %d (esperado 10)\n", result, out);
    print_state(buffer, length, nelem, head, tail);

    // Teste 2: remover 20
    printf("Test 2: Dequeue\n");
    result = dequeue_value(buffer, length, &nelem, &tail, &head, &out);
    printf("Result: %d, Value: %d (esperado 20)\n", result, out);
    print_state(buffer, length, nelem, head, tail);

    // Teste 3: esvaziar o resto
    printf("Test 3: Dequeue até esvaziar\n");
    while (dequeue_value(buffer, length, &nelem, &tail, &head, &out)) {
        printf("Dequeued: %d\n", out);
        print_state(buffer, length, nelem, head, tail);
    }

    printf("Buffer agora está vazio.\n");

    // Teste 4: tentar remover de buffer vazio
    printf("\nTest 4: Dequeue em buffer vazio\n");
    result = dequeue_value(buffer, length, &nelem, &tail, &head, &out);
    printf("Result: %d (esperado 0)\n", result);

    return 0;
}