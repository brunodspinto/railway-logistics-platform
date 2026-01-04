package org.example.ui;

import org.example.domain.Train;
import org.example.domain.CrossingOperation;
import org.example.repository.IRouteRepository;
import org.example.service.SchedulerService;
import org.example.service.ScheduleResult;
import org.example.service.TrainSchedule;
import org.example.service.ScheduleEntry;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Scanner;

/**
 * UI dedicada à USLP10 - Traffic Manager.
 * Foca-se no despacho automático e resolução de conflitos.
 */
public class TrafficSchedulerUI {

    private final IRouteRepository repository;
    private final SchedulerService service;
    private final Scanner scanner;
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public TrafficSchedulerUI(IRouteRepository repository) {
        this.repository = repository;
        this.service = new SchedulerService(repository);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("       TRAFFIC MANAGER: AUTO DISPATCH & CONFLICTS (USLP10)");
        System.out.println("=".repeat(80));
        System.out.println("1. Deteta rotas pendentes e calcula-as automaticamente (Dijkstra).");
        System.out.println("2. Calcula horários com base na física (Peso/Potência).");
        System.out.println("3. Resolve conflitos de cruzamento em linha única.");
        System.out.println("=".repeat(80));

        // 1. CARREGAR DADOS
        Collection<Train> allTrains = repository.getAllTrains();
        if (allTrains.isEmpty()) {
            System.out.println("\n(!) Não há comboios para despachar.");
            return;
        }

        // 2. MOSTRAR LISTA
        System.out.println("\n[DASHBOARD DE TRÁFEGO]");
        System.out.printf("%-5s | %-15s | %-15s | %-10s | %-20s\n",
                "ID", "Origem", "Destino", "Partida", "Tipo de Rota");
        System.out.println("-".repeat(80));

        int manualCount = 0;
        int autoCount = 0;

        for (Train t : allTrains) {
            boolean hasManualRoute = (t.getPathStations() != null && !t.getPathStations().isEmpty());
            String routeType = hasManualRoute ? "MANUAL" : "AUTO (Pendente)";

            if (hasManualRoute) manualCount++; else autoCount++;

            String start = (t.getStartStation() != null) ? t.getStartStation().getName() : "N/A";
            String end = (t.getEndStation() != null) ? t.getEndStation().getName() : "N/A";

            System.out.printf("%-5d | %-15s | %-15s | %-10s | %s\n",
                    t.getId(), truncate(start, 15), truncate(end, 15), t.getTime(), routeType);
        }
        System.out.println("-".repeat(80));
        System.out.printf("Resumo: %d Manuais, %d Automáticas.\n", manualCount, autoCount);

        // 3. EXECUTAR
        System.out.print("\nAutorizar despacho e cálculo de horários? (S/N): ");
        if (!scanner.next().equalsIgnoreCase("S")) return;

        // === NOVO BLOCO: MODO DE TESTE SEM MEXER NA BD ===
        System.out.print("Deseja forçar o MODO AUTOMÁTICO e CONFLITOS para teste (Ignorar BD)? (S/N): ");
        if (scanner.next().equalsIgnoreCase("S")) {
            System.out.println("\n⚠️ A ATIVAR MODO SIMULAÇÃO (Dados em memória alterados)...");

            // Usamos a data do primeiro comboio como referência para colidir os outros
            java.time.LocalDate targetDate = null;
            if (!allTrains.isEmpty()) {
                targetDate = allTrains.iterator().next().getDate();
            }

            for (Train t : allTrains) {
                // 1. Apagar a rota manual (só na memória) para obrigar o Dijkstra a correr
                if (t.getPathStations() != null && !t.getPathStations().isEmpty()) {
                    // Guardamos Start/End, mas limpamos o caminho
                    t.setPathStations(new java.util.ArrayList<>());
                    System.out.println("-> Comboio " + t.getId() + ": Rota manual removida (Forçar Dijkstra).");
                }

                // 2. Sincronizar datas para garantir que há conflitos
                if (targetDate != null) {
                    t.setDate(targetDate);
                }
            }
            System.out.println("-> Todas as datas sincronizadas para " + targetDate + " (Para testar cruzamentos).");
        }
        // =================================================

        try {
            System.out.println("\n>> A otimizar tráfego na rede...");
            ScheduleResult result = service.calculateSchedulesWithConflicts(allTrains);

            if (result.getSchedules().isEmpty()) {
                System.out.println("(!) Nenhum horário gerado.");
                return;
            }

            // 4. RESULTADOS (TIMETABLES)
            System.out.println("\n--- HORÁRIOS GERADOS ---");
            for (TrainSchedule sched : result.getSchedules()) {
                printSchedule(sched);
            }

            // 5. RESULTADOS (CONFLITOS)
            System.out.println("\n--- RESOLUÇÃO DE CONFLITOS (CRUZAMENTOS) ---");
            if (result.getCrossings().isEmpty()) {
                System.out.println("✓ Rede livre de conflitos.");
            } else {
                for (CrossingOperation op : result.getCrossings()) {
                    System.out.println("⚠️  " + op.toString());
                }
            }

        } catch (Exception e) {
            System.out.println("(!) Erro no despacho: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printSchedule(TrainSchedule sched) {
        Train t = sched.getTrain();
        System.out.printf("\nComboio #%d (%s)\n", t.getId(), t.getOperator());
        System.out.println("+-----------------+-------+-------+----------------------+");
        System.out.println("| Estação         | Cheg  | Part  | Obs                  |");
        System.out.println("+-----------------+-------+-------+----------------------+");

        String firstDep = sched.getEntries().isEmpty() ? t.getTime().toString() :
                sched.getEntries().get(0).getArrivalTime().format(timeFmt);

        System.out.printf("| %-15s | --:-- | %s | Origem               |\n",
                truncate(t.getStartStation().getName(), 15), firstDep);

        for (ScheduleEntry e : sched.getEntries()) {
            // LÓGICA NOVA PARA MOSTRAR O TEMPO
            String observacao;
            if (e.stops()) {
                long minutos = e.getStopDurationMinutes();
                observacao = String.format("PARAGEM (%d min)", minutos);
            } else {
                observacao = "Passagem";
            }

            System.out.printf("| %-15s | %s | %s | %-20s |\n",
                    truncate(e.getStation().getName(), 15),
                    e.getArrivalTime().format(timeFmt),
                    e.getDepartureTime().format(timeFmt),
                    observacao);
        }
        System.out.println("+-----------------+-------+-------+----------------------+");
    }

    private String truncate(String s, int len) {
        return (s != null && s.length() > len) ? s.substring(0, len-1) + "." : s;
    }
}