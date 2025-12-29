# Setup Hardware - USAC10 + USAC14

## Hardware Necessário

- Raspberry Pi Pico W
- Sensor DHT11 (GP16)
- 6 LEDs (3 por track)
- Resistências 220Ω
- Breadboard + cabos

## Ligações

### Track 1 (Esquerda)
- LED Verde:   GP11
- LED Amarelo: GP12
- LED Vermelho: GP13

### Track 2 (Direita)
- LED Vermelho: GP18
- LED Amarelo:  GP19
- LED Verde:    GP20

### Sensor DHT11
- Data: GP16
- VCC:  3.3V
- GND:  GND

## Testar Arduino

1. Upload `station_controller.ino`
2. Abrir Serial Monitor (9600 baud)
3. Enviar comandos:
    - `GE,01` → LED verde T1 acende
    - `GTH`   → Retorna temp/humidade

## Integração com USAC14

1. Ligar Arduino ao PC via USB
2. Identificar porta: `ls /dev/ttyACM*`
3. Executar USAC14 com porta correta