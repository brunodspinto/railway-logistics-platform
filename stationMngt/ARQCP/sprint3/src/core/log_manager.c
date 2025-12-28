/*
 * USAC12 - Create a text file (.txt file)
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "structures.h"
#include "log_manager.h"

extern int encrypt_data(char* in, int key, char *out);

/**
 * Função interna para autenticar o Administrador.
 * Usa a função Assembly encrypt_data para cifrar a password recebida
 * e comparar com a armazenada na estrutura do sistema.
 */
int _authenticate_admin(StationSystem *system, const char *username, const char *password) {
    for (int i = 0; i < system->users.count; i++) {
        // Procura o utilizador pelo username
        if (strcmp(system->users.data[i].username, username) == 0) {

            // Prepara buffers
            char encrypted_input[100]; // Buffer para receber a cifra
            int key = system->users.data[i].caesar_key;

            char temp_pass[100];
            strncpy(temp_pass, password, 99);
            temp_pass[99] = '\0';

            int res = encrypt_data(temp_pass, key, encrypted_input);

            if (res == 1) {
                // Se a cifra assembly correu bem, compara com a password guardada
                if (strcmp(encrypted_input, system->users.data[i].encrypted_password) == 0) {
                    return 1; // Sucesso: Autenticado
                }
            }
            // Se falhou cifra ou comparação
            return 0;
        }
    }
    return 0;
}

/**
 * Exportar ações de um utilizador para ficheiro .txt
 */
int export_user_logs(StationSystem *system, const char *admin_user, const char *admin_pass, const char *target_username, const char *filename) {

    // 1. SEGURANÇA: Validar administrador
    printf("[LOG] A autenticar administrador '%s' via Assembly...\n", admin_user);
    if (!_authenticate_admin(system, admin_user, admin_pass)) {
        printf("[ERRO] Autenticação falhou.\n");
        return 0;
    }

    // 2. Verificar se o utilizador alvo existe (procurando pelo username)
    int user_found = 0;
    for (int i = 0; i < system->users.count; i++) {
        // CORREÇÃO: Comparar strings (username) em vez de ID
        if (strcmp(system->users.data[i].username, target_username) == 0) {
            user_found = 1;
            break;
        }
    }

    if (!user_found) {
        printf("[ERRO] Utilizador alvo '%s' não encontrado.\n", target_username);
        return 0;
    }

    // 3. Abrir ficheiro
    FILE *file = fopen(filename, "w");
    if (file == NULL) {
        printf("[ERRO] Não foi possível criar o ficheiro '%s'.\n", filename);
        return 0;
    }

    // 4. Escrever Cabeçalho
    fprintf(file, "==================================================\n");
    fprintf(file, " RELATÓRIO DE AÇÕES (Admin: %s)\n", admin_user);
    fprintf(file, " ALVO: %s\n", target_username);
    fprintf(file, "==================================================\n");
    fprintf(file, "%-20s | %s\n", "TIMESTAMP", "AÇÃO");
    fprintf(file, "--------------------------------------------------\n");

    int count = 0;
    for (int i = 0; i < system->logs.count; i++) {
        // CORREÇÃO: Comparar username do log com o alvo
        if (strcmp(system->logs.data[i].username, target_username) == 0) {
            fprintf(file, "%-20s | %s\n",
                    system->logs.data[i].timestamp, // Agora é seguro porque mudámos para char[]
                    system->logs.data[i].action);
            count++;
        }
    }

    if (count == 0) {
        fprintf(file, "   (Nenhuma ação registada)\n");
    }

    fprintf(file, "==================================================\n");
    fprintf(file, "Total: %d registos.\n", count);

    fclose(file);
    printf("[SUCESSO] Logs exportados para '%s'.\n", filename);
    return 1;
}