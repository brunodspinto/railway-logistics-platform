package org.example.service;

public enum RoutePriority {
    DISTANCE,   // Menor distância (km)
    TIME,       // Menor tempo (baseado na velocidade máxima da linha)
    ENERGY      // Menor consumo (para o futuro)
}