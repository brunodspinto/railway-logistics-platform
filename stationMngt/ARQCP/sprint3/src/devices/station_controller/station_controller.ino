#include "DHT.h"

// --- SENSOR ---
#define DHTPIN 16
#define DHTTYPE DHT11

// --- TRACK 1 (Lado Esquerdo) ---
#define T1_GREEN  11  // GP11
#define T1_YELLOW 12  // GP12
#define T1_RED    13  // GP13

// --- TRACK 2 (Lado Direito) ---
#define T2_RED    18  // GP18
#define T2_YELLOW 19  // GP19
#define T2_GREEN  20  // GP20

DHT dht(DHTPIN, DHTTYPE);

// ==========================================
// VARIÁVEIS GLOBAIS
// ==========================================

// --- Filtro Mediana (Sensores) ---
const int WINDOW_SIZE = 5;
float tempHistory[WINDOW_SIZE];
float humHistory[WINDOW_SIZE];
int readIndex = 0;

// --- Controlo de Piscar (Blink) ---
const long blinkInterval = 500; // 500ms ligado, 500ms desligado
unsigned long previousMillis = 0;
int blinkState = LOW;

// Estado individual de cada linha
bool t1_Blinking = false;
bool t2_Blinking = false;

// ==========================================
// SETUP
// ==========================================
void setup() {
  Serial.begin(9600);
  dht.begin();

  // Configurar LEDs Track 1
  pinMode(T1_RED, OUTPUT);
  pinMode(T1_YELLOW, OUTPUT);
  pinMode(T1_GREEN, OUTPUT);

  // Configurar LEDs Track 2
  pinMode(T2_RED, OUTPUT);
  pinMode(T2_YELLOW, OUTPUT);
  pinMode(T2_GREEN, OUTPUT);

  // Limpar arrays do filtro
  for (int i = 0; i < WINDOW_SIZE; i++) {
    tempHistory[i] = 0.0;
    humHistory[i] = 0.0;
  }
}

// ==========================================
// LOOP PRINCIPAL
// ==========================================
void loop() {
  // 1. LER SENSORES (Sem bloquear)
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  if (!isnan(h) && !isnan(t)) {
    tempHistory[readIndex] = t;
    humHistory[readIndex] = h;
    readIndex = (readIndex + 1) % WINDOW_SIZE;
  }

  // 2. ATUALIZAR PISCAS (Non-blocking)
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= blinkInterval) {
    previousMillis = currentMillis;
    blinkState = (blinkState == LOW) ? HIGH : LOW;

    // Se Track 1 está em modo Blink, atualiza o LED Vermelho
    if (t1_Blinking) digitalWrite(T1_RED, blinkState);

    // Se Track 2 está em modo Blink, atualiza o LED Vermelho
    if (t2_Blinking) digitalWrite(T2_RED, blinkState);
  }

  // 3. LER COMANDOS DA PORTA SÉRIE
  if (Serial.available() > 0) {
    String command = Serial.readStringUntil('\n');
    command.trim(); // Limpar espaços/enters

    // --- COMANDO: SENSORES ---
    if (command == "GTH") {
      float medianTemp = getMedian(tempHistory, WINDOW_SIZE);
      float medianHum = getMedian(humHistory, WINDOW_SIZE);

      // Formato: TEMP&unit:celsius&value:XX#HUM&unit:percentage&value:XX
      Serial.print("TEMP&unit:celsius&value:");
      Serial.print((int)medianTemp);
      Serial.print("#HUM&unit:percentage&value:");
      Serial.println((int)medianHum);
    }

    // --- COMANDO: LUZES (Formato "CMD, x") ---
    else if (command.indexOf(',') > 0) {
      int commaIndex = command.indexOf(',');
      String code = command.substring(0, commaIndex); // Ex: "GE"
      String trackStr = command.substring(commaIndex + 1); // Ex: " 1"
      int track = trackStr.toInt();

      setTrackLight(code, track);
    }
  }

  delay(50); // Estabilidade
}

// ==========================================
// FUNÇÕES AUXILIARES
// ==========================================

// Algoritmo de Mediana (Bubble Sort)
float getMedian(float *array, int size) {
  float sorted[size];
  for (int i = 0; i < size; i++) sorted[i] = array[i];

  for (int i = 0; i < size - 1; i++) {
    for (int j = 0; j < size - i - 1; j++) {
      if (sorted[j] > sorted[j + 1]) {
        float temp = sorted[j];
        sorted[j] = sorted[j + 1];
        sorted[j + 1] = temp;
      }
    }
  }
  return sorted[size / 2];
}

// Lógica dos Semáforos
void setTrackLight(String code, int track) {
  if (track == 1) {
    // 1. Reset Track 1 (Apagar tudo)
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
    // 1. Reset Track 2
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