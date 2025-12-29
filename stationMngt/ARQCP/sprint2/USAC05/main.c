#include <stdio.h>
#include "enqueue_value.h"

// Função auxiliar para imprimir o estado do buffer
void print_state(int* buffer, int length, int nelem, int head, int tail) {
    printf("Buffer: [ ");
    for (int i = 0; i < length; i++) {
        printf("%d ", buffer[i]);
    }
    printf("]\n");
    printf("head=%d, tail=%d, nelem=%d\n\n", head, tail, nelem);
}

int main() {
    printf("=== USAC05 - enqueue_value ===\n\n");

    int length = 5;
    int buffer[5] = {0};
    int nelem = 0;
    int head = 0;
    int tail = 0;

    int result;

    // Test 1: Inserir valor normal
    printf("Test 1: Inserir 10\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 10);
    printf("Result: %d\n", result);
    print_state(buffer, length, nelem, head, tail);

    // Test 2: Inserir mais valores
    printf("Test 2: Inserir 20\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 20);
    printf("Result: %d\n", result);
    print_state(buffer, length, nelem, head, tail);

    printf("Test 3: Inserir 30\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 30);
    printf("Result: %d\n", result);
    print_state(buffer, length, nelem, head, tail);

    printf("Test 4: Inserir 40\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 40);
    printf("Result: %d\n", result);
    print_state(buffer, length, nelem, head, tail);

    printf("Test 5: Inserir 50 (buffer fica cheio)\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 50);
    printf("Result: %d\n", result);   // Deve ser 1 porque fica cheio
    print_state(buffer, length, nelem, head, tail);

    // Test 6: Inserir 60 (remove o mais antigo)
    printf("Test 6: Inserir 60 (buffer cheio → remove mais antigo)\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 60);
    printf("Result: %d\n", result);   // Deve ser 1
    print_state(buffer, length, nelem, head, tail);

    // Test 7: Inserir 70
    printf("Test 7: Inserir 70 (continua a substituir)\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 70);
    printf("Result: %d\n", result);
    print_state(buffer, length, nelem, head, tail);

    return 0;
}
