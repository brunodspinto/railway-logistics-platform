package org.example.trees;

import org.example.domain.Station;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Representa um nó da 2D-Tree.
 * Suporta múltiplas estações nas mesmas coordenadas (bucket).
 */
public class Node2D {

    // O "bucket" de estações neste ponto exato.
    // Requisito: Ordenado por nome
    private final List<Station> stations;

    // Coordenada e eixo usados para a divisão neste nó
    private final double splitCoordinate;
    private final int axis; // 0 para latitude, 1 para longitude

    private Node2D left;
    private Node2D right;

    /**
     * Construtor do nó.
     * @param station A primeira estação a adicionar (a estação mediana).
     * @param axis O eixo de divisão (0=lat, 1=lon).
     */
    public Node2D(Station station, int axis) {
        this.stations = new ArrayList<>();
        this.stations.add(station); // Adiciona a primeira
        this.axis = axis;
        this.splitCoordinate = getCoordinate(station, axis);
        this.left = null;
        this.right = null;
    }

    /**
     * Adiciona uma estação a este nó (ao "bucket").
     * Mantém a lista ordenada por nome , usando a ordenação
     * natural da classe Station (que já está por nome).
     */
    public void addStation(Station station) {
        // Usa pesquisa binária para encontrar o ponto de inserção
        int pos = Collections.binarySearch(stations, station);
        if (pos < 0) {
            // Insere na posição correta para manter a ordem
            stations.add(-pos - 1, station);
        }
        // Se pos >= 0, a estação exata (mesmo nome) já existe, ignoramos.
    }

    // --- Getters ---

    public List<Station> getStations() {
        return stations;
    }

    public int getBucketSize() {
        return stations.size();
    }

    public double getSplitCoordinate() {
        return splitCoordinate;
    }

    public int getAxis() {
        return axis;
    }

    public Node2D getLeft() {
        return left;
    }

    public void setLeft(Node2D left) {
        this.left = left;
    }

    public Node2D getRight() {
        return right;
    }

    public void setRight(Node2D right) {
        this.right = right;
    }

    /**
     * Helper para obter a coordenada (lat ou lon) de uma estação.
     */
    public static double getCoordinate(Station s, int axis) {
        return (axis == 0) ? s.getLatitude() : s.getLongitude();
    }
}