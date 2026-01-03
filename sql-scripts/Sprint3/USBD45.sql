-- Função USBD45 --

CREATE OR REPLACE FUNCTION add_new_line(
    p_line_name IN VARCHAR2,
    p_owner_vat IN VARCHAR2,
    p_start_station_id IN NUMBER,
    p_end_station_id IN NUMBER,
    p_gauge_measure IN NUMBER,
    -- Dados do primeiro segmento (obrigatório)
    p_segment_max_weight IN NUMBER,
    p_segment_length IN NUMBER,
    p_is_electrified IN NUMBER,
    p_segment_type_id IN NUMBER,
    -- Siding (opcional)
    p_has_siding IN NUMBER DEFAULT 0,
    p_siding_position IN NUMBER DEFAULT NULL,
    p_siding_length IN NUMBER DEFAULT NULL
) RETURN SYS_REFCURSOR IS
    refcursor SYS_REFCURSOR;
    v_line_id NUMBER;
    v_segment_id NUMBER;
    v_siding_id NUMBER;
    v_count NUMBER;
BEGIN
    -- 1. Validar Operator
SELECT COUNT(*) INTO v_count FROM Operator WHERE vatNumber = p_owner_vat;
IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20451, 'Operator with VAT ' || p_owner_vat || ' does not exist.');
END IF;

    -- 2. Validar Start Station
SELECT COUNT(*) INTO v_count FROM Station WHERE idStation = p_start_station_id;
IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20452, 'Start station with ID ' || p_start_station_id || ' does not exist.');
END IF;

    -- 3. Validar End Station
SELECT COUNT(*) INTO v_count FROM Station WHERE idStation = p_end_station_id;
IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20453, 'End station with ID ' || p_end_station_id || ' does not exist.');
END IF;

    -- 4. Validar que as estações são diferentes
    IF p_start_station_id = p_end_station_id THEN
        RAISE_APPLICATION_ERROR(-20454, 'Start and end stations must be different.');
END IF;

    -- 5. Validar Gauge
SELECT COUNT(*) INTO v_count FROM Gauge WHERE measure = p_gauge_measure;
IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20455, 'Gauge with measure ' || p_gauge_measure || ' does not exist.');
END IF;

    -- 6. Validar LineSegmentType
SELECT COUNT(*) INTO v_count FROM LineSegmentType WHERE id = p_segment_type_id;
IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20456, 'LineSegmentType with ID ' || p_segment_type_id || ' does not exist.');
END IF;

    -- 7. Validar dados do segmento
    IF p_segment_length <= 0 OR p_segment_max_weight <= 0 OR p_is_electrified NOT IN (0, 1) THEN
        RAISE_APPLICATION_ERROR(-20457, 'Invalid segment data. Check length, weight and electrified values.');
END IF;

    -- 8. Validar dados do siding (se existir)
    IF p_has_siding = 1 THEN
        IF p_siding_position IS NULL OR p_siding_length IS NULL OR
           p_siding_position <= 0 OR p_siding_length <= 0 OR
           p_siding_position > p_segment_length THEN
            RAISE_APPLICATION_ERROR(-20458, 'Invalid siding data. Check position and length values.');
END IF;
END IF;

    -- 9. Gerar IDs
SELECT NVL(MAX(id), 0) + 1 INTO v_line_id FROM Line;
SELECT NVL(MAX(id), 0) + 1 INTO v_segment_id FROM LineSegment;

-- 10. Inserir Line
INSERT INTO Line (id, nameLine, startStation, endStation, ownerLine, gaugeMeasure)
VALUES (v_line_id, p_line_name, p_start_station_id, p_end_station_id, p_owner_vat, p_gauge_measure);

-- 11. Inserir LineSegment
INSERT INTO LineSegment (
    id, maximumWeigh, lenght, isElectrified,
    segmentOrder, lineSegmentsTypeid, lineId
) VALUES (
             v_segment_id, p_segment_max_weight, p_segment_length, p_is_electrified,
             1, p_segment_type_id, v_line_id
         );

-- 12. Inserir Siding (se necessário)
IF p_has_siding = 1 THEN
SELECT NVL(MAX(id), 0) + 1 INTO v_siding_id FROM Siding;
INSERT INTO Siding (id, position, lenght, lineSegmentId)
VALUES (v_siding_id, p_siding_position, p_siding_length, v_segment_id);
END IF;

    -- 13. Retornar cursor com resultado
OPEN refcursor FOR
SELECT
    l.id AS lineId,
    l.nameLine,
    l.startStation,
    l.endStation,
    l.ownerLine,
    l.gaugeMeasure,
    ls.id AS segmentId,
    ls.lenght,
    ls.maximumWeigh,
    ls.isElectrified,
    si.id AS sidingId
FROM Line l
         INNER JOIN LineSegment ls ON ls.lineId = l.id
         LEFT JOIN Siding si ON si.lineSegmentId = ls.id
WHERE l.id = v_line_id;

RETURN refcursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20459, 'Error creating line: ' || SQLERRM);
END add_new_line;
/


SET SERVEROUTPUT ON;

-- Bloco Anónimo USBD45 1--
DECLARE
v_cursor SYS_REFCURSOR;
    v_line_id NUMBER;
    v_line_name VARCHAR2(100);
    v_start_station NUMBER;
    v_end_station NUMBER;
    v_owner_vat VARCHAR2(20);
    v_gauge NUMBER;
    v_segment_id NUMBER;
    v_length NUMBER;
    v_max_weight NUMBER;
    v_electrified NUMBER;
    v_siding_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 1: Create simple line without siding');

    v_cursor := add_new_line(
        p_line_name => 'Ramal Teste Porto-Braga',
        p_owner_vat => 'PT503933813',
        p_start_station_id => 5,
        p_end_station_id => 30,
        p_gauge_measure => 1668,
        p_segment_max_weight => 8000,
        p_segment_length => 55000,
        p_is_electrified => 1,
        p_segment_type_id => 2
    );

    LOOP
FETCH v_cursor INTO v_line_id, v_line_name, v_start_station, v_end_station,
                           v_owner_vat, v_gauge, v_segment_id, v_length,
                           v_max_weight, v_electrified, v_siding_id;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Line ID: ' || v_line_id);
        DBMS_OUTPUT.PUT_LINE('Line Name: ' || v_line_name);
        DBMS_OUTPUT.PUT_LINE('Start Station: ' || v_start_station);
        DBMS_OUTPUT.PUT_LINE('End Station: ' || v_end_station);
        DBMS_OUTPUT.PUT_LINE('Owner VAT: ' || v_owner_vat);
        DBMS_OUTPUT.PUT_LINE('Gauge: ' || v_gauge || ' mm');
        DBMS_OUTPUT.PUT_LINE('Segment ID: ' || v_segment_id);
        DBMS_OUTPUT.PUT_LINE('Segment Length: ' || v_length || ' m');
        DBMS_OUTPUT.PUT_LINE('Max Weight: ' || v_max_weight || ' kg/m');
        DBMS_OUTPUT.PUT_LINE('Electrified: ' || CASE v_electrified WHEN 1 THEN 'Yes' ELSE 'No' END);
        IF v_siding_id IS NOT NULL THEN
            DBMS_OUTPUT.PUT_LINE('Siding ID: ' || v_siding_id);
ELSE
            DBMS_OUTPUT.PUT_LINE('Siding: No');
END IF;
END LOOP;

CLOSE v_cursor;
DBMS_OUTPUT.PUT_LINE('');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/

-- Bloco Anónimo USBD45 2--
DECLARE
v_cursor SYS_REFCURSOR;
    v_line_id NUMBER;
    v_line_name VARCHAR2(100);
    v_start_station NUMBER;
    v_end_station NUMBER;
    v_owner_vat VARCHAR2(20);
    v_gauge NUMBER;
    v_segment_id NUMBER;
    v_length NUMBER;
    v_max_weight NUMBER;
    v_electrified NUMBER;
    v_siding_id NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 2: Create line with siding');

    v_cursor := add_new_line(
        p_line_name => 'Linha Teste com Siding',
        p_owner_vat => 'PT503933813',
        p_start_station_id => 1,
        p_end_station_id => 2,
        p_gauge_measure => 1668,
        p_segment_max_weight => 7500,
        p_segment_length => 45000,
        p_is_electrified => 0,
        p_segment_type_id => 1,
        p_has_siding => 1,
        p_siding_position => 22500,
        p_siding_length => 800
    );

    LOOP
FETCH v_cursor INTO v_line_id, v_line_name, v_start_station, v_end_station,
                           v_owner_vat, v_gauge, v_segment_id, v_length,
                           v_max_weight, v_electrified, v_siding_id;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Line ID: ' || v_line_id);
        DBMS_OUTPUT.PUT_LINE('Line Name: ' || v_line_name);
        DBMS_OUTPUT.PUT_LINE('Start Station: ' || v_start_station);
        DBMS_OUTPUT.PUT_LINE('End Station: ' || v_end_station);
        DBMS_OUTPUT.PUT_LINE('Owner VAT: ' || v_owner_vat);
        DBMS_OUTPUT.PUT_LINE('Gauge: ' || v_gauge || ' mm');
        DBMS_OUTPUT.PUT_LINE('Segment ID: ' || v_segment_id);
        DBMS_OUTPUT.PUT_LINE('Segment Length: ' || v_length || ' m');
        DBMS_OUTPUT.PUT_LINE('Max Weight: ' || v_max_weight || ' kg/m');
        DBMS_OUTPUT.PUT_LINE('Electrified: ' || CASE v_electrified WHEN 1 THEN 'Yes' ELSE 'No' END);
        IF v_siding_id IS NOT NULL THEN
            DBMS_OUTPUT.PUT_LINE('Siding ID: ' || v_siding_id);
ELSE
            DBMS_OUTPUT.PUT_LINE('Siding: No');
END IF;
END LOOP;

CLOSE v_cursor;
DBMS_OUTPUT.PUT_LINE('');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/

-- Bloco Anónimo USBD45 3--
DECLARE
v_cursor SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 3: Invalid operator (should fail)');

    v_cursor := add_new_line(
        p_line_name => 'Linha Invalida',
        p_owner_vat => 'PT999999999',
        p_start_station_id => 1,
        p_end_station_id => 2,
        p_gauge_measure => 1668,
        p_segment_max_weight => 8000,
        p_segment_length => 50000,
        p_is_electrified => 1,
        p_segment_type_id => 2
    );

CLOSE v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/

-- Bloco Anónimo USBD45 4--
DECLARE
v_cursor SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 4: Same start/end station (should fail)');

    v_cursor := add_new_line(
        p_line_name => 'Linha Circular',
        p_owner_vat => 'PT503933813',
        p_start_station_id => 5,
        p_end_station_id => 5,
        p_gauge_measure => 1668,
        p_segment_max_weight => 8000,
        p_segment_length => 50000,
        p_is_electrified => 1,
        p_segment_type_id => 2
    );

CLOSE v_cursor;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('');
END;
/