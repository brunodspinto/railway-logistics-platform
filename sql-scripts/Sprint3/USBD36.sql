-- Função USBD36 --

CREATE OR REPLACE FUNCTION add_building_to_facility(
    p_station_id IN NUMBER,
    p_area_type_id IN NUMBER
) RETURN SYS_REFCURSOR IS
    refcursor SYS_REFCURSOR;
    v_station_exists NUMBER;
    v_area_type_exists NUMBER;
    v_already_exists NUMBER;
BEGIN
    -- 1. Validar se a Station existe
SELECT COUNT(*) INTO v_station_exists
FROM Station
WHERE idStation = p_station_id;

IF v_station_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20361, 'Station with ID ' || p_station_id || ' does not exist.');
END IF;

    -- 2. Validar se o AreaType existe
SELECT COUNT(*) INTO v_area_type_exists
FROM AreaType
WHERE id = p_area_type_id;

IF v_area_type_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20362, 'AreaType with ID ' || p_area_type_id || ' does not exist.');
END IF;

    -- 3. Verificar se a associação já existe
SELECT COUNT(*) INTO v_already_exists
FROM AreaTypeStation
WHERE areaId = p_area_type_id
  AND stationId = p_station_id;

IF v_already_exists > 0 THEN
        RAISE_APPLICATION_ERROR(-20363, 'Area is already associated with this station.');
END IF;

    -- 4. Inserir a associação
INSERT INTO AreaTypeStation (areaId, stationId)
VALUES (p_area_type_id, p_station_id);

-- 5. Retornar cursor com o resultado
OPEN refcursor FOR
SELECT
    s.idStation,
    s.nameStation,
    s.stationTypeId,
    ats.areaId
FROM Station s
         INNER JOIN AreaTypeStation ats ON s.idStation = ats.stationId
WHERE s.idStation = p_station_id
  AND ats.areaId = p_area_type_id;

RETURN refcursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20369, 'Error adding building to facility: ' || SQLERRM);
END add_building_to_facility;
/


-- Bloco Anónimo USBD36 1--
DECLARE
v_cursor SYS_REFCURSOR;
    v_idStation Station.idStation%TYPE;
    v_nameStation Station.nameStation%TYPE;
    v_stationTypeId Station.stationTypeId%TYPE;
    v_areaId AreaType.id%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 1: Add warehouse to Ermesinde');

    v_cursor := add_building_to_facility(
        p_station_id => 14,
        p_area_type_id => 1
    );

    LOOP
FETCH v_cursor INTO v_idStation, v_nameStation, v_stationTypeId, v_areaId;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Station ID: ' || v_idStation ||
                           ', Name: ' || v_nameStation ||
                           ', Type ID: ' || v_stationTypeId ||
                           ', Area ID: ' || v_areaId);
END LOOP;

CLOSE v_cursor;
DBMS_OUTPUT.PUT_LINE('');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/

-- Bloco Anónimo USBD36 2--
DECLARE
v_cursor SYS_REFCURSOR;
    v_idStation Station.idStation%TYPE;
    v_nameStation Station.nameStation%TYPE;
    v_stationTypeId Station.stationTypeId%TYPE;
    v_areaId AreaType.id%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 2: Add grain silo to Tamel');

    v_cursor := add_building_to_facility(
        p_station_id => 2,
        p_area_type_id => 3
    );

    LOOP
FETCH v_cursor INTO v_idStation, v_nameStation, v_stationTypeId, v_areaId;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Station ID: ' || v_idStation ||
                           ', Name: ' || v_nameStation ||
                           ', Type ID: ' || v_stationTypeId ||
                           ', Area ID: ' || v_areaId);
END LOOP;

CLOSE v_cursor;
DBMS_OUTPUT.PUT_LINE('');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/

-- Bloco Anónimo USBD36 3--
DECLARE
v_cursor SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 3: Try to add existing area (should fail)');

    v_cursor := add_building_to_facility(
        p_station_id => 1,
        p_area_type_id => 1
    );

CLOSE v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/

-- Bloco Anónimo USBD36 4--
DECLARE
v_cursor SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 4: Try to add area to non-existent station (should fail)');

    v_cursor := add_building_to_facility(
        p_station_id => 9999,
        p_area_type_id => 1
    );

CLOSE v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/