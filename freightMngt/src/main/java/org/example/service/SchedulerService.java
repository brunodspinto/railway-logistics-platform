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
                System.err.printf("⚠️ Error calculating schedule for train %d: %s%n",
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
        // (comboios muito pesados para a potência disponível)
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
     *
     * Regras:
     * - Sempre para na origem e destino
     * - Para nas estações onde há freights para carregar/descarregar
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

        // Caso contrário, apenas passa
        return false;
    }

    /**
     * Calcula tempo de operação numa estação (carga/descarga)
     */
    private long calculateOperationTime(Train train, Station station) {
        // Tempo base de paragem
        long baseTime = 5; // 5 minutos (paragem técnica)

        // Tempo adicional por freight a carregar/descarregar
        int freightOps = 0;

        for (Freight freight : train.getFreights()) {
            if (freight.getOriginId() == station.getId()) {
                freightOps++; // Carregar
            }
            if (freight.getDestinationId() == station.getId()) {
                freightOps++; // Descarregar
            }
        }

        // 20 minutos por operação de freight
        return baseTime + (freightOps * 20L);
    }


    /**
     * Calcula schedules COM detecção e resolução de conflitos
     */
    public ScheduleResult calculateSchedulesWithConflicts(Collection<Train> trains) {
        // 1. Calcular schedules iniciais (sem considerar conflitos)
        List<TrainSchedule> schedules = new ArrayList<>();
        Map<Train, TrainSchedule> scheduleMap = new HashMap<>();

        for (Train train : trains) {
            try {
                TrainSchedule schedule = calculateSchedule(train);
                schedules.add(schedule);
                scheduleMap.put(train, schedule);
            } catch (Exception e) {
                System.err.printf("⚠️ Error calculating schedule for train %d: %s%n",
                        train.getId(), e.getMessage());
            }
        }

        // 2. Detectar conflitos
        List<Conflict> conflicts = detectConflicts(schedules);

        System.out.printf("\n🔍 Detected %d potential conflicts\n", conflicts.size());

        // 3. Resolver conflitos
        List<CrossingOperation> crossings = new ArrayList<>();

        for (Conflict conflict : conflicts) {
            if (conflict.hasTemporalOverlap()) {
                CrossingOperation crossing = resolveCrossing(conflict, scheduleMap);
                if (crossing != null) {
                    crossings.add(crossing);
                }
            }
        }

        System.out.printf("✅ Resolved %d crossings\n\n", crossings.size());

        return new ScheduleResult(schedules, crossings);
    }

    /**
     * Detecta conflitos entre trains
     */
    private List<Conflict> detectConflicts(List<TrainSchedule> schedules) {
        List<Conflict> conflicts = new ArrayList<>();

        // Comparar cada par de trains
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

        // Obter path de cada train
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

                // Verificar se é a mesma linha (em sentidos opostos)
                boolean sameLine = (line1.getId() == line2.getId()) ||
                        (from1.equals(to2) && to1.equals(from2));

                if (sameLine) {
                    // Verificar se algum segmento é single track
                    boolean hasSingleTrack = line1.getSegments().stream()
                            .anyMatch(seg -> seg.getNumberTracks() == 1);

                    if (hasSingleTrack) {
                        // Calcular tempos de entrada/saída
                        LocalDateTime t1Entry = schedule1.getDepartureTimeAt(from1);
                        LocalDateTime t1Exit = schedule1.getArrivalTimeAt(to1);
                        LocalDateTime t2Entry = schedule2.getDepartureTimeAt(from2);
                        LocalDateTime t2Exit = schedule2.getArrivalTimeAt(to2);

                        if (t1Entry != null && t1Exit != null &&
                                t2Entry != null && t2Exit != null) {

                            // Usar primeiro segmento single track
                            LineSegment singleTrackSeg = line1.getSegments().stream()
                                    .filter(seg -> seg.getNumberTracks() == 1)
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

        return conflicts;
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

        // Encontrar estação onde o train que espera deve parar
        Station waitingStation = findWaitingStation(waitingTrain, conflict.getSegment());

        if (waitingStation == null) {
            System.err.printf("⚠️ No waiting station found for train %d before segment %d\n",
                    waitingTrain.getId(), conflict.getSegment().getId());
            return null;
        }

        // Calcular quanto tempo deve esperar
        LocalDateTime waitingArrival = waitingSchedule.getArrivalTimeAt(waitingStation);
        LocalDateTime passingClearTime = getSegmentClearTime(passingTrain, passingSchedule,
                conflict.getSegment());

        if (waitingArrival == null || passingClearTime == null) {
            return null;
        }

        // Adicionar margem de segurança (5 minutos)
        LocalDateTime safeDeparture = passingClearTime.plusMinutes(5);

        // Calcular delay necessário
        long delayMinutes = java.time.Duration.between(waitingArrival, safeDeparture).toMinutes();

        if (delayMinutes > 0) {
            // Aplicar delay ao schedule
            waitingSchedule.addDelay(waitingStation, delayMinutes);

            // Criar operação de cruzamento
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
     * Encontra a estação onde o train deve esperar (antes do segmento de conflito)
     */
    private Station findWaitingStation(Train train, LineSegment conflictSegment) {
        List<Station> path = train.getPathStations();

        // Encontrar segmento de conflito no path
        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);

            Line line = repository.findDirectLine(from.getId(), to.getId());
            if (line != null && line.getSegments().contains(conflictSegment)) {
                // Retornar estação ANTES do segmento
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

        // Encontrar estação de saída do segmento
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

