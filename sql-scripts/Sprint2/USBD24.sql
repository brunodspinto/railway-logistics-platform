-- Função USBD24 --

CREATE OR REPLACE FUNCTION listEndPointsRoute(p_train_id IN Train.id%TYPE)
RETURN SYS_REFCURSOR
IS
    refcursor SYS_REFCURSOR;
BEGIN
    OPEN refcursor FOR
        SELECT DISTINCT
               Train.id AS trainId,
               Freights.id AS freightsId,
               Station_inicial.nameStation AS startStationPath,
               Station_final.nameStation AS endStationPath
        FROM Train
             INNER JOIN Freights ON Freights.trainId = Train.id
             INNER JOIN Path ON Path.freightsId = Freights.id
             INNER JOIN Station Station_inicial ON Path.inicialStation = Station_inicial.idStation
             INNER JOIN Station Station_final ON Path.finalStation = Station_final.idStation
        WHERE Train.id = p_train_id
        ORDER BY Station_inicial.nameStation, Station_final.nameStation;

    RETURN refcursor;

EXCEPTION
    WHEN OTHERS THEN
        OPEN refcursor FOR
            SELECT 'Erro: Não foi possível obter os endpoints de uma rota ferroviária planeada.' AS error_message
            FROM dual;
        RETURN refcursor;
END;
/


-- Bloco Anónimo USBD24 1--

DECLARE
    refcursor SYS_REFCURSOR;
    v_trainId Train.id%TYPE;
    v_freightsId Freights.id%TYPE;
    v_startStation Station.nameStation%TYPE;
    v_endStation Station.nameStation%TYPE;
    v_trainIdInput NUMBER := 5421;
BEGIN
    refcursor := listEndPointsRoute(v_trainIdInput);
    LOOP
        FETCH refcursor INTO v_trainId, v_freightsId, v_startStation, v_endStation;
        EXIT WHEN refcursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('TrainID: ' || v_trainId || '; FreightsID: ' || v_freightsId || '; StartStation: ' || v_startStation || '; EndStation: ' || v_endStation);
    END LOOP;

    CLOSE refcursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro ao obter os endpoints finais de uma rota ferroviária planeada: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD24 2--

DECLARE
    refcursor SYS_REFCURSOR;
    v_trainId Train.id%TYPE;
    v_freightsId Freights.id%TYPE;
    v_startStation Station.nameStation%TYPE;
    v_endStation Station.nameStation%TYPE;
     v_trainIdInput NUMBER := 5437;
BEGIN
    refcursor := listEndPointsRoute(v_trainIdInput);

    LOOP
        FETCH refcursor INTO v_trainId, v_freightsId, v_startStation, v_endStation;
        EXIT WHEN refcursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('TrainID: ' || v_trainId || '; FreightsID: ' || v_freightsId || '; StartStation: ' || v_startStation || '; EndStation: ' || v_endStation);
    END LOOP;

    CLOSE refcursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro ao obter os endpoints finais de uma rota ferroviária planeada: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD24 3--

DECLARE
    refcursor SYS_REFCURSOR;
    v_trainId Train.id%TYPE;
    v_freightsId Freights.id%TYPE;
    v_startStation Station.nameStation%TYPE;
    v_endStation Station.nameStation%TYPE;
    v_counter NUMBER := 0;
    v_trainIdInput NUMBER := 9999;
BEGIN
    refcursor := listEndPointsRoute(v_trainIdInput);

    LOOP
        FETCH refcursor INTO v_trainId, v_freightsId, v_startStation, v_endStation;
        EXIT WHEN refcursor%NOTFOUND;
        v_counter := v_counter + 1;

        DBMS_OUTPUT.PUT_LINE('TrainID: ' || v_trainId || '; FreightsID: ' || v_freightsId || '; StartStation: ' || v_startStation || '; EndStation: ' || v_endStation);
    END LOOP;

    IF v_counter = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Erro: TrainID não encontrado ou sem endpoints.');
    END IF;

    CLOSE refcursor;
END;
/