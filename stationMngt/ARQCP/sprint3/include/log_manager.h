#ifndef LOG_MANAGER_H
#define LOG_MANAGER_H

#include "structures.h"

/**
 * Exporta logs de um utilizador específico para um ficheiro txt.
 * Requer autenticação do administrador (password) para executar.
 */
int export_user_logs(StationSystem *system,
                     const char *admin_user,
                     const char *admin_pass,
                     const char *target_username,
                     const char *filename);

#endif