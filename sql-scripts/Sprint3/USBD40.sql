-- Função USBD40 --

CREATE OR REPLACE FUNCTION func_register_freight(
    p_freightId   IN Freights.id%TYPE,
    p_date        IN Freights.dateFreights%TYPE,
    p_trainId     IN Freights.trainId%TYPE,
    p_wagonTypeId IN WagonsType.id%TYPE,
    p_wagonQty    IN NUMBER
) RETURN VARCHAR2 IS

    v_available_count NUMBER;
    v_wagon_id        Wagon.numberWagon%TYPE;

    CURSOR c_free_wagons IS
        SELECT w.numberWagon
        FROM Wagon w JOIN WagonModel wm
        ON w.wagonModelId = wm.id
        WHERE wm.wagonsTypeId = p_wagonTypeId
        AND w.numberWagon NOT IN (
             SELECT wf.wagonNumber
             FROM WagonFreights wf JOIN Freights f
             ON wf.freightsId = f.id
             WHERE f.dateFreights = p_date
        );

BEGIN
    SELECT COUNT(*) INTO v_available_count
    FROM Wagon w JOIN WagonModel wm
    ON w.wagonModelId = wm.id
    WHERE wm.wagonsTypeId = p_wagonTypeId
    AND w.numberWagon NOT IN (
        SELECT wf.wagonNumber
        FROM WagonFreights wf JOIN Freights f
        ON wf.freightsId = f.id
        WHERE f.dateFreights = p_date
    );

    -- Validar a quantidade
    IF v_available_count < p_wagonQty THEN
        RETURN 'Erro: Insuficiente vagões do tipo ' || p_wagonTypeId ||
               '. Disponíveis: ' || v_available_count ||
               ', Solicitados: ' || p_wagonQty;
    END IF;

    -- Se passar na validação, insere o Freight
    INSERT INTO Freights (id, dateFreights, trainId)
    VALUES (p_freightId, p_date, p_trainId);

    -- Associar os vagões ao Freight (WagonFreights)
    OPEN c_free_wagons;

    FOR i IN 1..p_wagonQty LOOP
        FETCH c_free_wagons INTO v_wagon_id;
        EXIT WHEN c_free_wagons%NOTFOUND;

        INSERT INTO WagonFreights (wagonNumber, freightsId)
        VALUES (v_wagon_id, p_freightId);
    END LOOP;

    CLOSE c_free_wagons;

    RETURN 'Sucesso: Freight ' || p_freightId || ' registado com ' || p_wagonQty || ' vagões associados.';

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        RETURN 'Erro: O ID do Freight ' || p_freightId || ' já existe.';
    WHEN OTHERS THEN
        IF c_free_wagons%ISOPEN THEN CLOSE c_free_wagons; END IF;
        RETURN 'Erro Inesperado: ' || SQLERRM;
END;


-- Bloco Anónimo USBD40 1 --

SET SERVEROUTPUT ON;

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- USBD40: Teste de Sucesso ---');

    -- ID Frete: 9001, Data: 2025-12-01, Comboio: 5437, Tipo Vagão: 1 (Cereal), Qtd: 2
    v_result := func_register_freight(9001, TO_DATE('2025-12-01','YYYY-MM-DD'), 5437, 1, 2);

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;

-- Bloco Anónimo USBD40 2 --

SET SERVEROUTPUT ON;

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- USBD40: Teste de Insuficiência de Recursos ---');

    -- Pede 100 vagões
    v_result := func_register_freight(9002, TO_DATE('2025-12-01','YYYY-MM-DD'), 5437, 1, 100);

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;

-- Bloco Anónimo USBD40 3 --

SET SERVEROUTPUT ON;

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- USBD40: Teste de ID Duplicado (Exception) ---');

    -- Tenta registar o Frete 2001 (que já existe)
    v_result := func_register_freight(2001, TO_DATE('2025-12-01','YYYY-MM-DD'), 5437, 1, 1);

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;
