#include <stdio.h>
#include "encrypt_data.h"

int main(void) {
        char input[100], output[100];
        int key;

        printf("\nIntroduza o texto a cifrar (apenas maiúsculas): ");
                scanf("%s", input);

        printf("Introduza a chave (1-26): ");
                scanf("%d", &key);

        if (encrypt_data(input, key, output))
                printf("\nTexto cifrado: %s\n", output);
        else
                printf("Erro: dados inválidos.\n");

        return 0;
}