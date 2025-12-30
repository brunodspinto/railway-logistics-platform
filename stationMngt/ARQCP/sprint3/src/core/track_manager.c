#include <stdio.h>
#include "track_manager.h"
#include "light_controller.h"
#include "board.h" // Se tiveres o header do board disponível
#include "types.h" // Para os enums TRACK_FREE, etc.

// Declaração externa da função do board (caso não esteja num header público)
void manager_send_data_to_board(StationSystem *sys);

// Função auxiliar pura em C para encontrar via livre
static Track* find_first_free_track(StationSystem* sys) {
    if (!sys || sys->tracks.count == 0) return NULL;

    for (int i = 0; i < sys->tracks.count; i++) {
        // Verifica se o estado é TRACK_FREE (0)
        if (sys->tracks.data[i].state == TRACK_FREE) {
            return &sys->tracks.data[i];
        }
    }
    return NULL;
}

// Função auxiliar para encontrar via por ID
static Track* get_track_by_id(StationSystem* sys, int id) {
    for (int i = 0; i < sys->tracks.count; i++) {
        if (sys->tracks.data[i].id == id) {
            return &sys->tracks.data[i];
        }
    }
    return NULL;
}

// =========================================================
// USAC16: Processar Chegada de Comboio
// =========================================================
int process_train_arrival(StationSystem* sys, int train_id) {
    printf("[Manager] A processar chegada do comboio %d...\n", train_id);

    // 1. Procurar via livre
    Track* track = find_first_free_track(sys);

    // 2. Cenário: Paragem de Emergência (Nenhuma via livre)
    if (track == NULL) {
        printf("!!! ALERTA: Nenhuma via disponível! !!!\n");
        printf("!!! ORDEM: EMERGENCY STOP para Comboio %d !!!\n", train_id);
        return -1;
    }

    // 3. Cenário: Via Encontrada -> Atribuir
    track->state = TRACK_ASSIGNED; // Estado 1 (Amarelo/Ocupado)
    track->assigned_train_id = train_id;

    printf("✓ Sucesso: Comboio %d atribuído à Via %d.\n", train_id, track->id);

    // 4. Atualizar Hardware e Painel
    set_track_light(track);       // Atualiza semáforo (USAC14)
    manager_send_data_to_board(sys); // Atualiza dashboard (USAC15)

    return track->id;
}

// =========================================================
// USAC16: Processar Partida (Libertar Via)
// =========================================================
void process_train_departure(StationSystem* sys, int track_id) {
    Track* track = get_track_by_id(sys, track_id);

    if (track == NULL) {
        printf("x Erro: Via %d não existe.\n", track_id);
        return;
    }

    if (track->state == TRACK_FREE) {
        printf("! Aviso: A via %d já está livre.\n", track_id);
        return;
    }

    // Lógica de partida
    int train = track->assigned_train_id;
    track->state = TRACK_FREE;      // Estado 0 (Verde/Livre)
    track->assigned_train_id = -1;  // Limpar ID do comboio

    printf("✓ Comboio %d partiu. Via %d está agora LIVRE.\n", train, track_id);

    // Atualizar Hardware e Painel
    set_track_light(track);
    manager_send_data_to_board(sys);
}

// =========================================================
// USAC16: Bloquear Via (Inoperacional/Manutenção)
// =========================================================
void set_track_unavailable(StationSystem* sys, int track_id) {
    Track* track = get_track_by_id(sys, track_id);

    if (!track) {
        printf("x Erro: Via %d não encontrada.\n", track_id);
        return;
    }

    track->state = TRACK_INOPERATIVE; // Estado 3 (Vermelho/Bloqueado)
    // Nota: Se houver lá um comboio, ele continua lá "preso" ou movemos?
    // Assume-se que bloqueia a via independentemente do comboio.

    printf("✓ Via %d marcada como INOPERACIONAL.\n", track_id);

    set_track_light(track);
    manager_send_data_to_board(sys);
}