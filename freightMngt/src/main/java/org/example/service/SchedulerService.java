package org.example.service;

import org.example.domain.*;
import org.example.repository.IRouteRepository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Serviço principal de scheduling
 * Calcula horários dos comboios baseado nos seus paths
 */
public class SchedulerService {

    private final IRouteRepository repository;
    private final TravelTimeCalculator travelTimeCalc;

    public SchedulerService(IRouteRepository repository) {
        this.repository = repository;
        this.travelTimeCalc = new TravelTimeCalculator(repository);
    }

    /**
     * Calcula horário de um comboio individual
     */
    public TrainSchedule calculateSchedule(Train train) throws Exception {
        if (train.getPathStations().isEmpty()) {
            throw new IllegalArgumentException("Train must have a path");
        }

        if (train.getLocomotives().isEmpty()) {
            throw new IllegalArgumentException("Train must have at least one locomotive");
        }

        LocalDateTime currentTime = train.getDepartureDateTime();
        TrainSchedule schedule = new TrainSchedule(train, currentTime);

        List<Station> pathStations = train.getPathStations();

        // ❌ REMOVER: Não adicionar origem como entry
        // O TrainSchedule.format() já mostra a origem

        // Para cada segmento do caminho
        for (int i = 0; i < pathStations.size() - 1; i++) {
            Station fromStation = pathStations.get(i);
            Station toStation = pathStations.get(i + 1);

            // Encontrar linha direta entre as duas estações
            Line line = repository.findDirectLine(fromStation.getId(), toStation.getId());

            if (line == null) {
                throw new Exception(String.format(
                        "No direct line found between %s and %s",
                        fromStation.getName(), toStation.getName()));
            }

            // Calcular velocidade efetiva e tempo
            double effectiveSpeed = calculateEffectiveSpeed(line, train);
            double distanceKm = line.getTotalLengthKm();
            long travelMinutes = calculateTravelTimeMinutes(distanceKm, effectiveSpeed);

            // Atualizar tempo atual
            currentTime = currentTime.plusMinutes(travelMinutes);

            // Verificar se para nesta estação
            boolean stops = shouldStopAt(train, toStation);

            LocalDateTime departureTime = currentTime;

            // Se para, adicionar tempo de operação
            if (stops) {
                long operationMinutes = calculateOperationTime(train, toStation);
                departureTime = currentTime.plusMinutes(operationMinutes);
            }

            // Criar entrada no horário
            ScheduleEntry entry = new ScheduleEntry(
                    toStation,
                    currentTime,        // arrival
                    departureTime,      // departure
                    effectiveSpeed,
                    distanceKm,
                    stops
            );

            schedule.addEntry(entry);

            // Atualizar tempo atual para próximo segmento
            currentTime = departureTime;
        }

        return schedule;
    }

    /**
     * Calcula horários de múltiplos comboios
     */
    public List<TrainSchedule> calculateSchedules(Collection<Train> trains) {
        List<TrainSchedule> schedules = new ArrayList<>();

        for (Train train : trains) {
            try {
                TrainSchedule schedule = calculateSchedule(train);
                schedules.add(schedule);
            } catch (Exception e) {
                System.err.printf("Error calculating schedule for train %d: %s%n",
                        train.getId(), e.getMessage());
            }
        }

        return schedules;
    }

    /**
     * Calcula velocidade efetiva considerando:
     * - Limite da linha
     * - Capacidade das locomotivas
     * - Peso do comboio
     */
    private double calculateEffectiveSpeed(Line line, Train train) {
        // Velocidade máxima da linha
        int lineMaxSpeed = line.getMinMaxSpeed();

        // Velocidade máxima das locomotivas
        int trainMaxSpeed = train.getMaxSpeed();

        // Velocidade base = menor das duas
        double baseSpeed = Math.min(lineMaxSpeed, trainMaxSpeed);

        // Penalização por peso (simplificado)
        double weight = train.getTotalWeightTons();
        int power = train.getTotalPowerKw();

        // Ratio peso/potência (tons por kW)
        double weightPowerRatio = weight / power;

        // Se ratio > 0.15, reduzir velocidade
        if (weightPowerRatio > 0.15) {
            double penalty = Math.min(0.3, (weightPowerRatio - 0.15) * 2);
            baseSpeed = baseSpeed * (1 - penalty);
        }

        return baseSpeed;
    }

    /**
     * Calcula tempo de viagem em minutos
     */
    private long calculateTravelTimeMinutes(double distanceKm, double speedKmh) {
        if (speedKmh <= 0) {
            throw new IllegalArgumentException("Speed must be positive");
        }

        double timeHours = distanceKm / speedKmh;
        return Math.round(timeHours * 60);
    }

    /**
     * Determina se o comboio deve parar numa estação
     */
    private boolean shouldStopAt(Train train, Station station) {
        // Para na origem
        if (station.getId() == train.getStartId()) {
            return true;
        }

        // Para no destino final
        if (station.getId() == train.getEndId()) {
            return true;
        }

        // Para se algum freight tem origem ou destino nesta estação
        for (Freight freight : train.getFreights()) {
            if (freight.getOriginId() == station.getId() ||
                    freight.getDestinationId() == station.getId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calcula tempo de operação numa estação (carga/descarga)
     */
    private long calculateOperationTime(Train train, Station station) {
        long baseTime = 10; // 10 minutos (paragem técnica)

        int freightOps = 0;

        for (Freight freight : train.getFreights()) {
            if (freight.getOriginId() == station.getId()) {
                freightOps++; // Carregar
            }
            if (freight.getDestinationId() == station.getId()) {
                freightOps++; // Descarregar
            }
        }

        // 30 minutos por operação de freight
        return baseTime + (freightOps * 30L);
    }

    /**
     * Calcula schedules COM detecção e resolução de conflitos
     */
    public ScheduleResult calculateSchedulesWithConflicts(Collection<Train> trains) {
        // 1. Calcular schedules iniciais
        List<TrainSchedule> schedules = new ArrayList<>();
        Map<Train, TrainSchedule> scheduleMap = new HashMap<>();

        for (Train train : trains) {
            try {
                TrainSchedule schedule = calculateSchedule(train);
                schedules.add(schedule);
                scheduleMap.put(train, schedule);
            } catch (Exception e) {
                System.err.printf("Error calculating schedule for train %d: %s%n",
                        train.getId(), e.getMessage());
            }
        }

        // 2. Detectar conflitos
        List<Conflict> conflicts = detectConflicts(schedules);

        // 3. Mostrar resumo de conflitos
        if (!conflicts.isEmpty()) {
            System.out.println("\n" + "=".repeat(80));
            System.out.printf("CONFLICTS DETECTED: %d\n", conflicts.size());
            System.out.println("=".repeat(80));

            for (Conflict c : conflicts) {
                System.out.printf("\nLine: %s (single track)\n", c.getSegment().getLineId());
                System.out.printf("  Train %d: %s -> %s\n",
                        c.getTrain1().getId(),
                        c.getTrain1EntryTime().toLocalTime(),
                        c.getTrain1ExitTime().toLocalTime());
                System.out.printf("  Train %d: %s -> %s\n",
                        c.getTrain2().getId(),
                        c.getTrain2EntryTime().toLocalTime(),
                        c.getTrain2ExitTime().toLocalTime());
            }

            System.out.println("\n" + "=".repeat(80) + "\n");
        }

        // 4. Resolver conflitos
        List<CrossingOperation> crossings = new ArrayList<>();

        for (Conflict conflict : conflicts) {
            if (conflict.hasTemporalOverlap()) {
                CrossingOperation crossing = resolveCrossing(conflict, scheduleMap);
                if (crossing != null) {
                    crossings.add(crossing);
                }
            }
        }

        if (crossings.size() > 0) {
            System.out.printf("Resolved %d of %d crossings\n\n",
                    crossings.size(), conflicts.size());
        }

        return new ScheduleResult(schedules, crossings);
    }

    /**
     * Detecta conflitos entre trains
     */
    private List<Conflict> detectConflicts(List<TrainSchedule> schedules) {
        List<Conflict> conflicts = new ArrayList<>();

        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                TrainSchedule schedule1 = schedules.get(i);
                TrainSchedule schedule2 = schedules.get(j);

                List<Conflict> pairConflicts = detectConflictsBetween(schedule1, schedule2);
                conflicts.addAll(pairConflicts);
            }
        }

        return conflicts;
    }

    /**
     * Detecta conflitos entre dois trains específicos
     */
    private List<Conflict> detectConflictsBetween(TrainSchedule schedule1,
                                                  TrainSchedule schedule2) {
        List<Conflict> conflicts = new ArrayList<>();
        Train train1 = schedule1.getTrain();
        Train train2 = schedule2.getTrain();

        List<Station> path1 = train1.getPathStations();
        List<Station> path2 = train2.getPathStations();

        // Para cada segmento do train1
        for (int i = 0; i < path1.size() - 1; i++) {
            Station from1 = path1.get(i);
            Station to1 = path1.get(i + 1);

            Line line1 = repository.findDirectLine(from1.getId(), to1.getId());
            if (line1 == null) continue;

            // Para cada segmento do train2
            for (int j = 0; j < path2.size() - 1; j++) {
                Station from2 = path2.get(j);
                Station to2 = path2.get(j + 1);

                Line line2 = repository.findDirectLine(from2.getId(), to2.getId());
                if (line2 == null) continue;

                // Verificar se é a mesma linha
                boolean sameLine = (line1.getId() == line2.getId()) ||
                        (from1.equals(to2) && to1.equals(from2));

                if (sameLine) {
                    // Verificar se algum segmento é single track
                    boolean hasSingleTrack = line1.getSegments().stream()
                            .anyMatch(LineSegment::isSingleTrack);

                    if (hasSingleTrack) {
                        // Calcular tempos de entrada/saída
                        LocalDateTime t1Entry = schedule1.getDepartureTimeAt(from1);
                        LocalDateTime t1Exit = schedule1.getArrivalTimeAt(to1);
                        LocalDateTime t2Entry = schedule2.getDepartureTimeAt(from2);
                        LocalDateTime t2Exit = schedule2.getArrivalTimeAt(to2);

                        if (t1Entry != null && t1Exit != null &&
                                t2Entry != null && t2Exit != null) {

                            // Verificar overlap temporal
                            boolean hasOverlap = checkTimeOverlap(t1Entry, t1Exit, t2Entry, t2Exit);

                            if (hasOverlap) {
                                LineSegment singleTrackSeg = line1.getSegments().stream()
                                        .filter(LineSegment::isSingleTrack)
                                        .findFirst()
                                        .orElse(null);

                                if (singleTrackSeg != null) {
                                    Conflict conflict = new Conflict(
                                            train1, train2, singleTrackSeg,
                                            t1Entry, t1Exit, t2Entry, t2Exit
                                    );
                                    conflicts.add(conflict);
                                }
                            }
                        }
                    }
                }
            }
        }

        return conflicts;
    }

    /**
     * Verifica se dois intervalos de tempo se sobrepõem
     */
    private boolean checkTimeOverlap(LocalDateTime start1, LocalDateTime end1,
                                     LocalDateTime start2, LocalDateTime end2) {
        // Overlap se: start1 < end2 AND start2 < end1
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Resolve um conflito criando uma operação de cruzamento
     */
    private CrossingOperation resolveCrossing(Conflict conflict,
                                              Map<Train, TrainSchedule> scheduleMap) {

        Train waitingTrain = conflict.getWaitingTrain();
        Train passingTrain = conflict.getPassingTrain();

        TrainSchedule waitingSchedule = scheduleMap.get(waitingTrain);
        TrainSchedule passingSchedule = scheduleMap.get(passingTrain);

        if (waitingSchedule == null || passingSchedule == null) {
            return null;
        }

        Station waitingStation = findWaitingStation(waitingTrain, conflict.getSegment());

        if (waitingStation == null) {
            System.err.printf("No waiting station found for train %d before segment %d\n",
                    waitingTrain.getId(), conflict.getSegment().getId());
            return null;
        }

        LocalDateTime waitingArrival = waitingSchedule.getArrivalTimeAt(waitingStation);
        LocalDateTime passingClearTime = getSegmentClearTime(passingTrain, passingSchedule,
                conflict.getSegment());

        if (waitingArrival == null || passingClearTime == null) {
            return null;
        }

        LocalDateTime safeDeparture = passingClearTime.plusMinutes(5);
        long delayMinutes = java.time.Duration.between(waitingArrival, safeDeparture).toMinutes();

        if (delayMinutes > 0) {
            waitingSchedule.addDelay(waitingStation, delayMinutes);

            return new CrossingOperation(
                    waitingTrain,
                    passingTrain,
                    waitingStation,
                    conflict.getSegment(),
                    waitingArrival,
                    safeDeparture
            );
        }

        return null;
    }

    /**
     * Encontra a estação onde o train deve esperar
     */
    private Station findWaitingStation(Train train, LineSegment conflictSegment) {
        List<Station> path = train.getPathStations();

        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);

            Line line = repository.findDirectLine(from.getId(), to.getId());
            if (line != null && line.getSegments().contains(conflictSegment)) {
                return from;
            }
        }

        return null;
    }

    /**
     * Calcula quando o train liberta completamente o segmento
     */
    private LocalDateTime getSegmentClearTime(Train train, TrainSchedule schedule,
                                              LineSegment segment) {
        List<Station> path = train.getPathStations();

        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);

            Line line = repository.findDirectLine(from.getId(), to.getId());
            if (line != null && line.getSegments().contains(segment)) {
                return schedule.getArrivalTimeAt(to);
            }
        }

        return null;
    }
}