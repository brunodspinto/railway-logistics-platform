-- Função USBD34 --

CREATE OR REPLACE FUNCTION associate_freight_train(
    p_freight_id IN freights.id%TYPE,
    p_train_id   IN train.id%TYPE
) RETURN NUMBER
IS
    v_exists NUMBER;
    v_train_max NUMBER;
    v_current NUMBER;
    v_new NUMBER;
BEGIN
    -- verificar freight
    SELECT COUNT(*) INTO v_exists
    FROM freights
    WHERE id = p_freight_id;

    IF v_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20341,'Freight inexistente.');
    END IF;

    -- verificar train
    SELECT COUNT(*) INTO v_exists
    FROM train
    WHERE id = p_train_id;

    IF v_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20342,'Train inexistente.');
    END IF;

    -- obter maxLenght
    SELECT maxLenght INTO v_train_max
    FROM train
    WHERE id = p_train_id;

    -- wagons já associados ao train via freights
    SELECT COUNT(*) INTO v_current
    FROM wagonfreights wf
    JOIN freights f ON f.id = wf.freightsid
    WHERE f.trainid = p_train_id;

    -- wagons do freight atual
    SELECT COUNT(*) INTO v_new
    FROM wagonfreights
    WHERE freightsid = p_freight_id;

    -- validar capacidade
    IF (v_current + v_new) > v_train_max THEN
        RAISE_APPLICATION_ERROR(-20343,'Capacidade máxima do comboio excedida.');
    END IF;

    -- associar freight ao train
    UPDATE freights
    SET trainid = p_train_id
    WHERE id = p_freight_id;

    RETURN v_new;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20344,'Erro inesperado na USBD34: '||SQLERRM);
END;
/


-- Bloco Anónimo USBD34 1--

DECLARE
    v_wagons NUMBER;
BEGIN
    v_wagons := associate_freight_train(2002, 5435);
    DBMS_OUTPUT.PUT_LINE('Wagons associados ao train: ' || v_wagons);
END;
/


-- Bloco Anónimo USBD34 2--

DECLARE
    v_wagons NUMBER;
BEGIN
    v_wagons := associate_freight_train(2006, 5437);
    DBMS_OUTPUT.PUT_LINE('Wagons associados ao train: ' || v_wagons);
END;
/


-- Bloco Anónimo USBD34 3--

DECLARE
    v_wagons NUMBER;
BEGIN
    v_wagons := associate_freight_train(2010, 5439);
    DBMS_OUTPUT.PUT_LINE('Wagons associados ao train: ' || v_wagons);
END;
/