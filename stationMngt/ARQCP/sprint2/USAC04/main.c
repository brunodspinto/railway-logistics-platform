#include <stdio.h>
#include "asm.h"

int main() {
    char cmd[100];
    int result;

    printf("=== USAC04 - format_command ===\n\n");

    // Test 1: RE
    result = format_command(" re ", 23, cmd);
    printf("Input: ' re ', 23\n");
    printf("Result: %d, Output: %s\n\n", result, cmd);

    // Test 2: YE
    result = format_command(" ye ", 91, cmd);
    printf("Input: ' ye ', 91\n");
    printf("Result: %d, Output: %s\n\n", result, cmd);

    // Test 3: GTH
    result = format_command(" gTh ", 17, cmd);
    printf("Input: ' gTh ', 17\n");
    printf("Result: %d, Output: %s\n\n", result, cmd);

    // Test 4: Invalid
    result = format_command("Off", 7, cmd);
    printf("Input: 'Off', 7 (invalid)\n");
    printf("Result: %d, Output: %s\n\n", result, cmd);

    return 0;
}