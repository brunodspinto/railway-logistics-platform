#include <stdio.h>
#include <string.h>
#include "sensors_manager.h"

// ========= MOCK SENSORS =========
int send_cmd_to_sensors(const char *cmd){ return 1; }

int wait_for_data_from_sensors(char *buffer, int max){
    char *f="TEMP&unit:celsius&value:23#HUM&unit:percentage&value:48";
    snprintf(buffer,max,"%s",f);
    return strlen(f);
}

// ========= TESTE USAC13 =========
int main(){
    SensorData d={0};
    for(int i=0;i<6;i++){
        manager_get_sensors_data(&d);
    }
    return 0;
}