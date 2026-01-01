#include "ui.c"
#include "stdlib.h"

int main(){
    int main(){
        StationSystem sys = {0}; // Inicializa a estrutura a zeros

        // Configurações iniciais (Exemplo)
        sys.tracks.count = 3;
        sys.tracks.data = calloc(3, sizeof(Track));
        sys.tracks.data[0].id = 1; // Via 1
        sys.tracks.data[1].id = 2; // Via 2
        sys.tracks.data[2].id = 3; // Via 3

        // Iniciar controladores
        light_controller_init(NULL); // NULL para modo Mock, ou "/dev/ttyACM0" para Arduino

        // Passar o endereço de sys para o menu
        menu(&sys);

        // Limpeza final
        free(sys.tracks.data);
        return 0;
    }
}