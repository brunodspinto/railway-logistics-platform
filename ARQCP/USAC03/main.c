#include <stdio.h>
#include "asm.h"

int main() {
    char str[] = "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
    char unit[100];
    int value;
    int result;

    printf("=== USAC03 - extract_data ===\n\n");

    // Test 1: TEMP
    result = extract_data(str, "TEMP", unit, &value);
    printf("Token: TEMP\n");
    printf("Result: %d, Unit: %s, Value: %d\n\n", result, unit, value);

    // Test 2: HUM
    result = extract_data(str, "HUM", unit, &value);
    printf("Token: HUM\n");
    printf("Result: %d, Unit: %s, Value: %d\n\n", result, unit, value);

    // Test 3: Not found
    result = extract_data(str, "AAA", unit, &value);
    printf("Token: AAA (not found)\n");
    printf("Result: %d, Unit: %s, Value: %d\n\n", result, unit, value);

    return 0;
}