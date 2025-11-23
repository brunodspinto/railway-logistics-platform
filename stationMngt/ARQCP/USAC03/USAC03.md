# USAC03 - extract_data

## Descrição
Extrai dados de uma string formatada com tokens (TEMP/HUM) e retorna a unidade e valor correspondentes.

## Ficheiros
- `asm.h` - Protótipo da função
- `asm.s` - Implementação em Assembly RISC-V
- `main.c` - Testes locais (opcional)
- `Makefile` - Compilação local (opcional)

## Função
```c
int extract_data(char* str, char* token, char* unit, int* value);
```

**Retorna:** 1 se sucesso, 0 caso contrário

## Formato da string
```
TOKEN&unit:xxxxxxx&value:xx#TOKEN&unit:xxxxxxxx&value:xx
```

## Testes
- **Localmente:** `make run`
- **Oficiais:** `cd /media/sf_partilha/utests/sprint2/usac03 && make run REPO=sem3pi`

## Resultado
✅ 6/6 testes passaram