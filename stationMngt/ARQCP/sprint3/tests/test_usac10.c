#include <stdio.h>
#include <string.h>
#include <stdlib.h>

// Protótipos das funções Assembly necessárias para o protocolo
// Se tiveres um header "asm.h" ou "assembly.h" em include/, podes usar #include "asm.h"
extern int extract_data(char* str, char* token, char* unit, int* value);
extern int format_command(char* op, int n, char *cmd);

// Macros auxiliares para validação rápida (estilo clean)
#define CHECK_INT(desc, expected, actual) \
    printf("  %-40s: ", desc); \
    if (expected == actual) printf("OK (%d)\n", actual); \
    else { printf("FAIL (Esp: %d, Rec: %d)\n", expected, actual); failures++; }

#define CHECK_STR(desc, expected, actual) \
    printf("  %-40s: ", desc); \
    if (strcmp(expected, actual) == 0) printf("OK ('%s')\n", actual); \
    else { printf("FAIL (Esp: '%s', Rec: '%s')\n", expected, actual); failures++; }

int main() {
    int failures = 0;

    printf("=== TESTE USAC10 (Protocolo Hardware) ===\n");

    // ==========================================
    // PARTE 1: Protocolo de Sensores (extract_data)
    // Simula a string que vem do Arduino:
    // "TEMP&unit:celsius&value:XX#HUM&unit:percentage&value:XX"
    // ==========================================
    printf("\n--- Teste 1: Leitura de Sensores (USAC03) ---\n");

    char sensor_packet[] = "TEMP&unit:celsius&value:25#HUM&unit:percentage&value:60";
    char unit_buffer[20] = {0};
    int value = 0;
    int res = 0;

    // Testar Temperatura
    res = extract_data(sensor_packet, "TEMP", unit_buffer, &value);
    CHECK_INT("Extrair TEMP (retorno 1)", 1, res);
    CHECK_INT("Valor Temperatura", 25, value);
    CHECK_STR("Unidade Temperatura", "celsius", unit_buffer);

    // Testar Humidade (Limpar buffer antes)
    memset(unit_buffer, 0, 20);
    res = extract_data(sensor_packet, "HUM", unit_buffer, &value);
    CHECK_INT("Extrair HUM (retorno 1)", 1, res);
    CHECK_INT("Valor Humidade", 60, value);
    CHECK_STR("Unidade Humidade", "percentage", unit_buffer);

    // ==========================================
    // PARTE 2: Protocolo de Luzes (format_command)
    // O Arduino espera "CMD, x" (ex: "RE, 01")
    // ==========================================
    printf("\n--- Teste 2: Comandos LightSigns (USAC04) ---\n");

    char cmd_buffer[50] = {0};

    // Teste: Ligar Vermelho Pista 1
    res = format_command("RE", 1, cmd_buffer);
    CHECK_INT("Formatar RE Track 1 (retorno 1)", 1, res);
    CHECK_STR("Output Comando RE", "RE, 01", cmd_buffer);

    // Teste: Ligar Verde Pista 12
    res = format_command("GE", 12, cmd_buffer);
    CHECK_INT("Formatar GE Track 12 (retorno 1)", 1, res);
    CHECK_STR("Output Comando GE", "GE, 12", cmd_buffer);

    // Teste: Piscar (Blink) Pista 5
    res = format_command("RB", 5, cmd_buffer);
    CHECK_INT("Formatar RB Track 5 (retorno 1)", 1, res);
    CHECK_STR("Output Comando RB", "RB, 05", cmd_buffer);

    // ==========================================
    // RESULTADO FINAL
    // ==========================================
    printf("\n---------------------------------------------\n");
    if (failures == 0) {
        printf("✓ Teste USAC10 concluído com SUCESSO.\n");
        return 0;
    } else {
        printf("X Teste USAC10 FALHOU com %d erros.\n", failures);
        return 1;
    }
}