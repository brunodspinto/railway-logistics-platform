#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "structures.h"
#include "log_manager.h"

// Função auxiliar para preparar dados de teste com dados de segurança
void setup_test_data(StationSystem *sys) {
    // 1. Inicializar arrays
    sys->users.data = malloc(2 * sizeof(User));
    sys->users.count = 2;
    sys->logs.data = malloc(5 * sizeof(LogEntry)); // Usar LogEntry se for esse o nome na tua struct
    sys->logs.count = 5;

    // 2. Criar Utilizadores

    // User 1: Boss (Será o Administrador neste teste)
    sys->users.data[0].id = 1;
    strcpy(sys->users.data[0].name, "Chefe");
    strcpy(sys->users.data[0].username, "boss");

    // Configuração de Segurança para passar no Assembly (USAC01)
    // Password real: "BOSS"
    // Key: 1
    // Cifra esperada (Caesar +1): "CPTT" (B->C, O->P, S->T, S->T)
    sys->users.data[0].caesar_key = 1;
    strcpy(sys->users.data[0].encrypted_password, "CPTT");

    // User 2: Admin (Outro user qualquer)
    sys->users.data[1].id = 2;
    strcpy(sys->users.data[1].name, "Administrador Secundário");
    strcpy(sys->users.data[1].username, "admin");
    sys->users.data[1].caesar_key = 5;
    strcpy(sys->users.data[1].encrypted_password, "FINT"); // Exemplo dummy

    // 3. Criar Logs (Misturados)

    // Log 1 (Boss)
    sys->logs.data[0].id = 101;
    sys->logs.data[0].user_id = 1;
    strcpy(sys->logs.data[0].timestamp, "2025-12-27 10:00");
    strcpy(sys->logs.data[0].action, "Login efetuado");

    // Log 2 (Admin) - NÃO deve aparecer no ficheiro do boss
    sys->logs.data[1].id = 102;
    sys->logs.data[1].user_id = 2;
    strcpy(sys->logs.data[1].timestamp, "2025-12-27 10:05");
    strcpy(sys->logs.data[1].action, "Configuração alterada");

    // Log 3 (Boss)
    sys->logs.data[2].id = 103;
    sys->logs.data[2].user_id = 1;
    strcpy(sys->logs.data[2].timestamp, "2025-12-27 10:10");
    strcpy(sys->logs.data[2].action, "Luz Track 1 alterada para VERDE");
}

void cleanup_test_data(StationSystem *sys) {
    if (sys->users.data) free(sys->users.data);
    if (sys->logs.data) free(sys->logs.data);
}

int main() {
    StationSystem system;
    const char *output_file = "data/logs/test_boss_actions.txt";

    printf("=== TESTE USAC12 (Exportar Logs com Autenticação Assembly) ===\n");

    // Preparar dados
    setup_test_data(&system);

    // --- TESTE 1: Sucesso (Admin: boss, Pass: BOSS, Alvo: boss) ---
    printf("\n[Teste 1] Exportar logs do utilizador 'boss' (autenticado como 'boss')...\n");

    // Assinatura: system, admin_user, admin_pass, target_user, filename
    int res = export_user_logs(&system, "boss", "BOSS", "boss", output_file);

    if (res == 1) {
        printf("✓ Sucesso: Função retornou 1.\n");
        printf("✓ Verifica o ficheiro em: %s\n", output_file);
    } else {
        printf("X Falha: Função retornou 0 (Erro inesperado).\n");
    }

    // --- TESTE 2: Falha na Autenticação (Password Errada) ---
    printf("\n[Teste 2] Tentativa com password errada...\n");
    if (export_user_logs(&system, "boss", "WRONGPASS", "boss", "lixo.txt") == 0) {
         printf("✓ Sucesso: Sistema bloqueou password errada.\n");
    } else {
         printf("X Falha: Sistema aceitou password errada!\n");
    }

    // --- TESTE 3: Utilizador Alvo Inexistente ---
    printf("\n[Teste 3] Exportar utilizador inexistente (autenticação correta)...\n");
    if (export_user_logs(&system, "boss", "BOSS", "fantasma", "lixo.txt") == 0) {
         printf("✓ Sucesso: Detetou utilizador alvo inexistente.\n");
    } else {
         printf("X Falha: Devia ter retornado 0.\n");
    }

    cleanup_test_data(&system);
    return 0;
}