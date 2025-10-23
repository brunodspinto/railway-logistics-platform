package org.example.service;

import org.example.domain.*;
import org.example.repository.IRouteRepository;

public class TravelTimeCalculator {

    private final IRouteRepository repository;

    public TravelTimeCalculator(IRouteRepository repository) {
        this.repository = repository;
    }

    /**
     * USLP03: Calcula tempo de viagem entre duas estações com conexão direta
     * @return tempo em horas
     */
    public double calculateTravelTime(int originId, int destinationId, int locomotiveNumber)
            throws Exception {

        // 1. Validar inputs
        Station origin = repository.getStation(originId);
        Station destination = repository.getStation(destinationId);
        Locomotive locomotive = repository.getLocomotive(locomotiveNumber);

        if (origin == null) {
            throw new IllegalArgumentException("Origin station not found: " + originId);
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination station not found: " + destinationId);
        }
        if (locomotive == null) {
            throw new IllegalArgumentException("Locomotive not found: " + locomotiveNumber);
        }

        // 2. Encontrar linha direta
        Line line = repository.findDirectLine(originId, destinationId);
        if (line == null) {
            throw new Exception(String.format(
                    "No direct connection between %s (ID:%d) and %s (ID:%d)",
                    origin.getName(), originId, destination.getName(), destinationId));
        }

        // 3. Validar compatibilidade
        if (!locomotive.isCompatibleWithGauge(line.getGauge())) {
            throw new Exception(String.format(
                    "Locomotive gauge (%d mm) incompatible with line gauge (%d mm)",
                    locomotive.getGauge(), line.getGauge()));
        }

        if (locomotive.isElectric() && !line.isFullyElectrified()) {
            throw new Exception(String.format(
                    "Electric locomotive '%s' cannot run on non-electrified line: %s",
                    locomotive.getName(), line.getName()));
        }

        // 4. Calcular velocidade efetiva (mínimo entre linha e locomotiva)
        int lineMaxSpeed = line.getMinMaxSpeed();
        int effectiveSpeed = Math.min(lineMaxSpeed, locomotive.getMaxSpeed());

        // 5. Calcular tempo (assumindo aceleração instantânea - Sprint 1)
        double distanceKm = line.getTotalLengthKm();
        double timeHours = distanceKm / effectiveSpeed;

        return timeHours;
    }

    /**
     * Versão detalhada que retorna informações do cálculo
     */
    public TravelTimeResult calculateDetailed(int originId, int destinationId,
                                              int locomotiveNumber) throws Exception {

        // 1. Validar inputs
        Station origin = repository.getStation(originId);
        if (origin == null) {
            throw new IllegalArgumentException(
                    String.format("Origin station not found with ID: %d", originId));
        }

        Station destination = repository.getStation(destinationId);
        if (destination == null) {
            throw new IllegalArgumentException(
                    String.format("Destination station not found with ID: %d", destinationId));
        }

        Locomotive locomotive = repository.getLocomotive(locomotiveNumber);
        if (locomotive == null) {
            throw new IllegalArgumentException(
                    String.format("Locomotive not found with number: %d", locomotiveNumber));
        }

        // 2. Encontrar linha direta (ANTES de tentar usar)
        Line line = repository.findDirectLine(originId, destinationId);
        if (line == null) {
            throw new Exception(String.format(
                    "No direct connection found!%n" +
                            "  Origin: %s (ID:%d)%n" +
                            "  Destination: %s (ID:%d)%n%n" +
                            "💡 Tip: Use option 3 (List all available routes) to see valid connections.",
                    origin.getName(), originId,
                    destination.getName(), destinationId));
        }

        // 3. Validar compatibilidade bitola
        if (!locomotive.isCompatibleWithGauge(line.getGauge())) {
            throw new Exception(String.format(
                    "Incompatible gauge!%n" +
                            "  Locomotive '%s' (Number: %d): %d mm%n" +
                            "  Line '%s': %d mm%n%n" +
                            "💡 Tip: Use option 4 (List locomotives) to see compatible options.",
                    locomotive.getName(), locomotive.getNumber(), locomotive.getGauge(),
                    line.getName(), line.getGauge()));
        }

        // 4. Validar eletrificação
        if (locomotive.isElectric() && !line.isFullyElectrified()) {
            throw new Exception(String.format(
                    "Electric locomotive on non-electrified line!%n" +
                            "  Locomotive: '%s' (⚡ Electric)%n" +
                            "  Line: '%s' (❌ Not fully electrified)%n%n" +
                            "💡 Tip: Use a diesel locomotive or choose a different route.",
                    locomotive.getName(), line.getName()));
        }

        // 5. Cálculos
        int lineMaxSpeed = line.getMinMaxSpeed();
        int effectiveSpeed = Math.min(lineMaxSpeed, locomotive.getMaxSpeed());
        double distanceKm = line.getTotalLengthKm();
        double timeHours = distanceKm / effectiveSpeed;

        return new TravelTimeResult(origin, destination, line, locomotive,
                distanceKm, effectiveSpeed, timeHours);
    }
}
