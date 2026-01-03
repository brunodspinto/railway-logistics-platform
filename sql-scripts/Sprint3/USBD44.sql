-- Função USBD44 --

CREATE OR REPLACE FUNCTION add_line_segment (
    p_lineId        IN NUMBER,
    p_length        IN NUMBER,
    p_maxWeight     IN NUMBER,
    p_segmentTypeId IN NUMBER,
    p_isElectrified IN NUMBER DEFAULT 0,
    p_segmentOrder  IN NUMBER DEFAULT 1,
    p_hasSiding     IN NUMBER DEFAULT 0,
    p_siding_pos    IN NUMBER DEFAULT NULL,
    p_siding_len    IN NUMBER DEFAULT NULL
) RETURN SYS_REFCURSOR
AS
    v_cursor     SYS_REFCURSOR;
    v_segmentId  NUMBER;
    v_sidingId   NUMBER;
    v_exists     NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_exists FROM Line WHERE id = p_lineId;
    IF v_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Linha não existe.');
    END IF;

    SELECT NVL(MAX(id),0)+1 INTO v_segmentId FROM LineSegment;

    INSERT INTO LineSegment(id, maximumweigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId)
    VALUES(v_segmentId, p_maxWeight, p_length, p_isElectrified, p_segmentOrder, p_segmentTypeId, p_lineId);

    IF p_hasSiding = 1 THEN

        IF p_siding_pos IS NULL OR p_siding_len IS NULL THEN
            RAISE_APPLICATION_ERROR(-20002,'Para siding é preciso posição e comprimento.');
        END IF;

        SELECT NVL(MAX(id),0)+1 INTO v_sidingId FROM Siding;

        INSERT INTO Siding(id, position, lenght, lineSegmentId)
        VALUES(v_sidingId, p_siding_pos, p_siding_len, v_segmentId);
    END IF;

    OPEN v_cursor FOR
        SELECT ls.id, ls.maximumweigh, ls.lenght, ls.isElectrified,ls.segmentOrder, ls.lineSegmentsTypeid, ls.lineId,
               s.id AS sidingId, s.position AS sidingPos, s.lenght AS sidingLen
        FROM LineSegment ls
        LEFT JOIN Siding s ON s.lineSegmentId = ls.id
        WHERE ls.id = v_segmentId;

    RETURN v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20099,'Erro inesperado na USBD44: '||SQLERRM);
END;
/


-- Bloco Anónimo USBD44 1--

DECLARE
    c SYS_REFCURSOR;
    v_id NUMBER;
    v_line NUMBER;
    v_maxweig NUMBER; v_lenght NUMBER; v_isEle NUMBER; v_order NUMBER; v_type NUMBER; v_sidingId NUMBER; v_sidingPos NUMBER; v_sidingLen NUMBER;
BEGIN
    c := add_line_segment(3, 5000, 12000, 1);

    LOOP
        FETCH c INTO v_id, v_line, v_maxweig, v_lenght, v_isEle, v_order, v_type, v_sidingId, v_sidingPos, v_sidingLen;
        EXIT WHEN c%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Segmento ID: '||v_id||'; Line: '||v_line);
    END LOOP;

    CLOSE c;
END;
/


-- Bloco Anónimo USBD44 2--

DECLARE
    c SYS_REFCURSOR;
    v_id NUMBER;
    v_sidingId NUMBER;
    v_maxweig NUMBER; v_lenght NUMBER; v_isEle NUMBER; v_order NUMBER; v_type NUMBER; v_line NUMBER; v_sidingPos NUMBER; v_sidingLen NUMBER;
BEGIN
    c := add_line_segment(5,8000,20000,2,1,3,1,2500,900);

    LOOP
        FETCH c INTO v_id,v_sidingId,v_maxweig,v_lenght,v_isEle,v_order,v_type,v_line,v_sidingPos,v_sidingLen;
        EXIT WHEN c%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Segmento: '||v_id||'; Siding: '||v_sidingId);
    END LOOP;
    CLOSE c;
END;
/


-- Bloco Anónimo USBD44 3--

DECLARE
    c SYS_REFCURSOR;
    v_id NUMBER;
    v_maxweig NUMBER; v_lenght NUMBER; v_isEle NUMBER; v_order NUMBER; v_type NUMBER; v_line NUMBER; v_sidingId NUMBER; v_sidingPos NUMBER; v_sidingLen NUMBER;
BEGIN
    c := add_line_segment(100,5000,12000,1);

    LOOP
        FETCH c INTO v_id,v_maxweig,v_lenght,v_isEle,v_order,v_type,v_line, v_sidingId,v_sidingPos,v_sidingLen;
        EXIT WHEN c%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Segmento criado: '||v_id);
    END LOOP;
    CLOSE c;
END;
/