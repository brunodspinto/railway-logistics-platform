# USAC04 - format_command

## Descrição
Formata comandos de controlo (RE/YE/GE/RB/GTH) com trim, uppercase e validação de número.

## Ficheiros
- `asm.h` - Protótipo da função
- `asm.s` - Implementação em Assembly RISC-V
- `main.c` - Testes locais (opcional)
- `Makefile` - Compilação local (opcional)

## Função
```c
int format_command(char* op, int n, char *cmd);
```

**Retorna:** 1 se sucesso, 0 caso contrário

## Comandos válidos
- **RE, YE, GE, RB:** Formato `CMD,NN` (n: 0-99)
- **GTH:** Formato `GTH` (ignora n)

## Exemplos
- `" re ", 23` → `"RE,23"`
- `" gTh ", 17` → `"GTH"`
- `"Off", 7` → `""` (inválido)

## Testes
- **Localmente:** `make run`
- **Oficiais:** `cd /media/sf_partilha/utests/sprint2/usac04 && make run REPO=sem3pi`

## Resultado
✅ 12/12 testes passaram