-- Função USBD41 --

CREATE OR REPLACE FUNCTION removeFreightFromTrain (p_freightId Freights.id%TYPE)
RETURN SYS_REFCURSOR
AS
    refcursor SYS_REFCURSOR;
    v_count   NUMBER;
    v_trainId Train.id%TYPE;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM Freights
    WHERE id = p_freightId;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20411, 'Freight does not exist.');
    END IF;

    SELECT trainId
    INTO v_trainId
    FROM Freights
    WHERE id = p_freightId;

    SELECT COUNT(*)
    INTO v_count
    FROM Train
    WHERE id = v_trainId;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20412, 'Associated train does not exist.');
    END IF;

    DELETE FROM WagonFreights
    WHERE freightsId = p_freightId;

    DELETE FROM Freights
    WHERE id = p_freightId;

    OPEN refcursor FOR
        SELECT f.id AS freightId,
               f.dateFreights,
               f.trainId
        FROM Freights f
        WHERE f.trainId = v_trainId;

    RETURN refcursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20413,
            'Error while removing freight: ' || SQLERRM);
END;


-- Bloco Anónimo USBD41 1--

DECLARE
    v_cursor SYS_REFCURSOR;
    v_freightId Freights.id%TYPE := 1001;
    v_id Freights.id%TYPE;
    v_dateFreights Freights.dateFreights%TYPE;
    v_trainId Freights.trainId%TYPE;
BEGIN
    v_cursor := removeFreightFromTrain(v_freightId);
        DBMS_OUTPUT.PUT_LINE('Remaining freight for train:');
    LOOP
        FETCH v_cursor INTO v_id, v_dateFreights, v_trainId;
        EXIT WHEN v_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Freight ID: ' || v_id || ', Date: ' || v_dateFreights || ', Train ID: ' || v_trainId);
    END LOOP;

    CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro: ' || SQLERRM);
END;


-- Bloco Anónimo USBD41 2--

DECLARE
    v_cursor SYS_REFCURSOR;
    v_freightId Freights.id%TYPE := 1001;
    v_id Freights.id%TYPE;
    v_dateFreights Freights.dateFreights%TYPE;
    v_trainId Freights.trainId%TYPE;
BEGIN
    v_cursor := removeFreightFromTrain(v_freightId);
        DBMS_OUTPUT.PUT_LINE('Remaining freight for train:');
    LOOP
        FETCH v_cursor INTO v_id, v_dateFreights, v_trainId;
        EXIT WHEN v_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Freight ID: ' || v_id || ', Date: ' || v_dateFreights || ', Train ID: ' || v_trainId);
    END LOOP;

    CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro: ' || SQLERRM);
END;



-- INSERT de um Freight ao TRAIN 5421 para testar--
INSERT INTO Freights (id, dateFreights, trainId) VALUES (1001, SYSDATE, 5421);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563077, 1001);
