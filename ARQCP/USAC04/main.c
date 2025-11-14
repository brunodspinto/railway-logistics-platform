#include <stdio.h>
#include <string.h>
#include "format_command.h"

int main() {
    char cmd[20];
    int res;

    printf("\n=================================\n");
    printf("  Format Command Demo\n");
    printf("=================================\n\n");

    // Example 1
    printf("Example 1: format_command(\" rB \", 5, cmd)\n");
    res = format_command(" rB ", 5, cmd);
    printf("  Result: %d: %s\n\n", res, cmd);

    // Example 2
    printf("Example 2: format_command(\" Ye \", 25, cmd)\n");
    res = format_command(" Ye ", 25, cmd);
    printf("  Result: %d: %s\n\n", res, cmd);

    // Example 3
    printf("Example 3: format_command(\" Ye \", 125, cmd)\n");
    res = format_command(" Ye ", 125, cmd);
    printf("  Result: %d: %s\n\n", res, cmd);

    // Example 4
    printf("Example 4: format_command(\" aaa \", 25, cmd)\n");
    res = format_command(" aaa ", 25, cmd);
    printf("  Result: %d: %s\n\n", res, cmd);

    // Example 5
    printf("Example 5: format_command(\" gTh \", 25, cmd)\n");
    res = format_command(" gTh ", 25, cmd);
    printf("  Result: %d: %s\n\n", res, cmd);

    return 0;
}