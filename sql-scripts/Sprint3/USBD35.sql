-- Função USBD35 --

CREATE OR REPLACE FUNCTION registerNewStation (
    p_idStation      Station.idStation%TYPE,
    p_nameStation    Station.nameStation%TYPE,
    p_stationTypeId  Station.stationTypeId%TYPE,
    p_areaId1        AreaType.id%TYPE,
    p_areaId2        AreaType.id%TYPE,
    p_areaId3        AreaType.id%TYPE
)
RETURN SYS_REFCURSOR
AS
    refcursor SYS_REFCURSOR;
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM Station
    WHERE idStation = p_idStation;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20351,
            'Station with this ID already exists.');
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM Station
    WHERE UPPER(nameStation) = UPPER(p_nameStation);

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20352,
            'Station with this name already exists.');
    END IF;

    INSERT INTO Station (idStation, nameStation, stationTypeId)
    VALUES (p_idStation, p_nameStation, p_stationTypeId);

    IF p_areaId1 = 1 THEN
        INSERT INTO AreaTypeStation (areaId, stationId)
        VALUES (1, p_idStation);
    END IF;

    IF p_areaId2 = 2 THEN
        INSERT INTO AreaTypeStation (areaId, stationId)
        VALUES (2, p_idStation);
    END IF;

    IF p_areaId3 = 3 THEN
        INSERT INTO AreaTypeStation (areaId, stationId)
        VALUES (3, p_idStation);
    END IF;

    OPEN refcursor FOR
        SELECT Station.idStation,
               Station.nameStation,
               Station.stationTypeId,
               AreaTypeStation.areaId
        FROM Station
        LEFT JOIN AreaTypeStation
           ON AreaTypeStation.stationId = Station.idStation
        WHERE Station.idStation = p_idStation;

    RETURN refcursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20353, 'Error while register new Station: ' || SQLERRM);
END;


-- Bloco Anónimo USBD35 1--

DECLARE
    v_cursor        SYS_REFCURSOR;
    v_idStation     Station.idStation%TYPE     := 100;
    v_nameStation   Station.nameStation%TYPE   := 'Viseu';
    v_stationTypeId Station.stationTypeId%TYPE := 1;

    v_areaId1 AreaType.id%TYPE := 1;
    v_areaId2 AreaType.id%TYPE := 0;
    v_areaId3 AreaType.id%TYPE := 3;

    v_areaId AreaType.id%TYPE;
BEGIN
    v_cursor := registerNewStation(v_idStation, v_nameStation, v_stationTypeId, v_areaId1,  v_areaId2, v_areaId3);

    LOOP
        FETCH v_cursor INTO v_idStation, v_nameStation, v_stationTypeId, v_areaId;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            'Station ID: ' || v_idStation ||
            ', Name: ' || v_nameStation ||
            ', Type ID: ' || v_stationTypeId ||
            ', Area ID: ' || v_areaId
        );
    END LOOP;

    CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;

-- Bloco Anónimo USBD35 2--

DECLARE
    v_cursor        SYS_REFCURSOR;
    v_idStation     Station.idStation%TYPE     := 50;
    v_nameStation   Station.nameStation%TYPE   := 'Braga';
    v_stationTypeId Station.stationTypeId%TYPE := 1;

    v_areaId1 AreaType.id%TYPE := 0;
    v_areaId2 AreaType.id%TYPE := 2;
    v_areaId3 AreaType.id%TYPE := 0;

    v_areaId AreaType.id%TYPE;
BEGIN
    v_cursor := registerNewStation(v_idStation, v_nameStation, v_stationTypeId, v_areaId1,  v_areaId2, v_areaId3);

    LOOP
        FETCH v_cursor INTO v_idStation, v_nameStation, v_stationTypeId, v_areaId;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            'Station ID: ' || v_idStation ||
            ', Name: ' || v_nameStation ||
            ', Type ID: ' || v_stationTypeId ||
            ', Area ID: ' || v_areaId
        );
    END LOOP;

    CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;

-- Bloco Anónimo USBD35 3--


DECLARE
    v_cursor        SYS_REFCURSOR;
    v_idStation     Station.idStation%TYPE     := 102;
    v_nameStation   Station.nameStation%TYPE   := 'Leixões';
    v_stationTypeId Station.stationTypeId%TYPE := 1;

    v_areaId1 AreaType.id%TYPE := 0;
    v_areaId2 AreaType.id%TYPE := 2;
    v_areaId3 AreaType.id%TYPE := 0;

    v_areaId AreaType.id%TYPE;
BEGIN
    v_cursor := registerNewStation(v_idStation, v_nameStation, v_stationTypeId, v_areaId1,  v_areaId2, v_areaId3);

    LOOP
        FETCH v_cursor INTO v_idStation, v_nameStation, v_stationTypeId, v_areaId;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            'Station ID: ' || v_idStation ||
            ', Name: ' || v_nameStation ||
            ', Type ID: ' || v_stationTypeId ||
            ', Area ID: ' || v_areaId
        );
    END LOOP;

    CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END;
