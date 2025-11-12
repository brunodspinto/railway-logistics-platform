#include <stdio.h>
#include <string.h>
#include "extract_data.h"

int main() {
    char str[] = "TEMP &unit:celsius &value:20 #HUM &unit:percentage &value:80";
    char unit[50];
    int value;

    printf("\n=================================\n");
    printf("  Extract Sensor Data Demo\n");
    printf("=================================\n\n");

    printf("Input string:\n  %s\n\n", str);

    // Extract TEMP (from specification example)
    printf("Extracting TEMP...\n");
    if (extract_data(str, "TEMP", unit, &value)) {
        printf("  Result: 1:%s,%d\n\n", unit, value);
    } else {
        printf("  Failed (0)\n\n");
    }

    // Extract HUM (from specification example)
    printf("Extracting HUM...\n");
    if (extract_data(str, "HUM", unit, &value)) {
        printf("  Result: 1:%s,%d\n\n", unit, value);
    } else {
        printf("  Failed (0)\n\n");
    }

    // Try invalid token (from specification example)
    printf("Extracting AAA...\n");
    if (extract_data(str, "AAA", unit, &value)) {
        printf("  Result: 1:%s,%d\n\n", unit, value);
    } else {
        printf("  Result: 0:,%d (expected)\n\n", value);
    }

    return 0;
}