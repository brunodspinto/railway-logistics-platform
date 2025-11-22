
-- Função USBD30 --

CREATE OR REPLACE FUNCTION getStationsWhithGrainSiloAndNoWarehouse
RETURN SYS_REFCURSOR
IS
    result_cursor SYS_REFCURSOR;
BEGIN
    OPEN result_cursor FOR
        SELECT Station.nameStation
        FROM Station
        JOIN AreaTypeStation ON Station.idStation = AreaTypeStation.stationId
        JOIN AreaType ON AreaTypeStation.areaId = AreaType.id
        WHERE AreaType.name = 'grain silo'
        MINUS
        SELECT Station.nameStation
        FROM Station
        JOIN AreaTypeStation ON Station.idStation = AreaTypeStation.stationId
        JOIN AreaType ON AreaTypeStation.areaId = AreaType.id
        WHERE AreaType.name = 'warehouse';
    RETURN result_cursor;
EXCEPTION
    WHEN OTHERS THEN
        OPEN result_cursor FOR
            SELECT 'Error: It was not possible to obtain the stations.' FROM dual;
        RETURN result_cursor;
END;


-- Bloco Anónimo USBD30 --

DECLARE
    v_name Station.nameStation%TYPE;
    refcursor SYS_REFCURSOR;
BEGIN
    refcursor := getStationsWhithGrainSiloAndNoWarehouse;
    DBMS_OUTPUT.PUT_LINE('Facilities that have grain silos, but no warehouses:');
    LOOP
        FETCH refcursor INTO v_name;
        EXIT WHEN refcursor%notfound;
        DBMS_OUTPUT.PUT_LINE(v_name);
    END LOOP;
    CLOSE refcursor;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error when obtain stations: ' || SQLERRM);
END;