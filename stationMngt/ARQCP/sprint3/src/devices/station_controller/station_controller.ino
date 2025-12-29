/*
 * USAC10 - Sensors and LightSigns Device Controller
 */

#include "DHT.h"

// Sensor DHT
#define DHTPIN 16       // GP16
#define DHTTYPE DHT11

// TRACK 1 (Esquerda)
#define T1_GREEN  11    // GP11
#define T1_YELLOW 12    // GP12
#define T1_RED    13    // GP13

// TRACK 2 (Direita)
#define T2_RED    18    // GP18
#define T2_YELLOW 19    // GP19
#define T2_GREEN  20    // GP20

// Inicialização do Sensor
DHT dht(DHTPIN, DHTTYPE);

// --- VARIÁVEIS GLOBAIS ---

// Filtro Mediana (Janela Deslizante)
const int WINDOW_SIZE = 5;
float tempHistory[WINDOW_SIZE];
float humHistory[WINDOW_SIZE];
int readIndex = 0;
bool bufferFull = false; // Para saber se já enchemos o array inicial

// Controlo de Piscar (Blink) - Non-blocking
const long blinkInterval = 500; // ms
unsigned long previousMillis = 0;
int blinkState = LOW;

// Estados de Pisca por Pista
bool t1_Blinking = false;
bool t2_Blinking = false;

// --- PROTÓTIPOS ---
float getMedian(float *array, int size);
void setTrackLight(String code, int track);

void setup() {
  Serial.begin(9600);

  dht.begin();

  // Configurar Saídas (LEDs)
  pinMode(T1_RED, OUTPUT);
  pinMode(T1_YELLOW, OUTPUT);
  pinMode(T1_GREEN, OUTPUT);

  pinMode(T2_RED, OUTPUT);
  pinMode(T2_YELLOW, OUTPUT);
  pinMode(T2_GREEN, OUTPUT);

  // Inicializar Arrays a 0
  for (int i = 0; i < WINDOW_SIZE; i++) {
    tempHistory[i] = 0.0;
    humHistory[i] = 0.0;
  }
}

void loop() {
  // 1. LEITURA DE SENSORES (Non-blocking logic recommended)
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  // Validar leitura (isnan verifica se não é número)
  if (!isnan(h) && !isnan(t)) {
    tempHistory[readIndex] = t;
    humHistory[readIndex] = h;
    readIndex = (readIndex + 1) % WINDOW_SIZE;
    if (readIndex == 0) bufferFull = true;
  }

  // 2. GESTÃO DE PISCAS (Timer por software)
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= blinkInterval) {
    previousMillis = currentMillis;
    blinkState = (blinkState == LOW) ? HIGH : LOW;

    // Atualizar LEDs se estiverem em modo Blink
    if (t1_Blinking) digitalWrite(T1_RED, blinkState);
    if (t2_Blinking) digitalWrite(T2_RED, blinkState);
  }

  // 3. COMUNICAÇÃO SÉRIE (Receber Comandos)
  if (Serial.available() > 0) {
    // Ler comando até ao caracter nova linha
    String command = Serial.readStringUntil('\n');
    command.trim(); // Remove \r e espaços extra

    // --- COMANDO: SENSORES (GTH) ---
    if (command.equalsIgnoreCase("GTH")) {
      // Calcular mediana
      int count = bufferFull ? WINDOW_SIZE : readIndex;
      // Se ainda não tivermos leituras, evitar erro
      if (count == 0) count = 1;

      float medianTemp = getMedian(tempHistory, WINDOW_SIZE); // Usa size total para simplificar
      float medianHum = getMedian(humHistory, WINDOW_SIZE);

      // Enviar resposta no formato estrito da USAC03
      // Formato: TEMP&unit:celsius&value:XX#HUM&unit:percentage&value:XX
      Serial.print("TEMP&unit:celsius&value:");
      Serial.print((int)medianTemp);
      Serial.print("#HUM&unit:percentage&value:");
      Serial.println((int)medianHum);
    }

    // --- COMANDO: LUZES (CMD, x) ---
    // Ex: "GE, 1" ou "RB, 02"
    else if (command.indexOf(',') > 0) {
      int commaIndex = command.indexOf(',');

      String code = command.substring(0, commaIndex);      // "GE"
      String trackStr = command.substring(commaIndex + 1); // " 1"

      code.trim();
      trackStr.trim();

      int track = trackStr.toInt();

      setTrackLight(code, track);
    }
  }

  // Pequeno delay para estabilidade do loop e do DHT
  delay(50);
}

// --- FUNÇÕES AUXILIARES ---

/*
 * setTrackLight: Aplica a lógica exclusiva dos semáforos.
 * code: RE (Red), YE (Yellow), GE (Green), RB (Red Blink)
 * track: Número da linha (1 ou 2)
 */
void setTrackLight(String code, int track) {
  code.toUpperCase(); // Garantir que comparamos maiúsculas

  if (track == 1) {
    // 1. Reset: Desligar tudo e parar blink
    digitalWrite(T1_RED, LOW);
    digitalWrite(T1_YELLOW, LOW);
    digitalWrite(T1_GREEN, LOW);
    t1_Blinking = false;

    // 2. Definir novo estado
    if (code == "RE") digitalWrite(T1_RED, HIGH);
    else if (code == "YE") digitalWrite(T1_YELLOW, HIGH);
    else if (code == "GE") digitalWrite(T1_GREEN, HIGH);
    else if (code == "RB") t1_Blinking = true;
  }
  else if (track == 2) {
    // 1. Reset
    digitalWrite(T2_RED, LOW);
    digitalWrite(T2_YELLOW, LOW);
    digitalWrite(T2_GREEN, LOW);
    t2_Blinking = false;

    // 2. Definir novo estado
    if (code == "RE") digitalWrite(T2_RED, HIGH);
    else if (code == "YE") digitalWrite(T2_YELLOW, HIGH);
    else if (code == "GE") digitalWrite(T2_GREEN, HIGH);
    else if (code == "RB") t2_Blinking = true;
  }
}

/*
 * getMedian: Calcula a mediana usando Bubble Sort num array temporário.
 * Nota: Cria uma cópia local para não desordenar o histórico original.
 */
float getMedian(float *array, int size) {
  float sorted[size];

  // Copiar array
  for (int i = 0; i < size; i++) {
    sorted[i] = array[i];
  }

  // Ordenar (Bubble Sort)
  for (int i = 0; i < size - 1; i++) {
    for (int j = 0; j < size - i - 1; j++) {
      if (sorted[j] > sorted[j + 1]) {
        float temp = sorted[j];
        sorted[j] = sorted[j + 1];
        sorted[j + 1] = temp;
      }
    }
  }

  // Retornar valor central
  return sorted[size / 2];
}