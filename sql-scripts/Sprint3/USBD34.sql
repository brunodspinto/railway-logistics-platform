-- Função USBD34 --

CREATE OR REPLACE FUNCTION associate_freight_train(
    p_freight_id IN freights.id%TYPE,
    p_train_id   IN train.id%TYPE
) RETURN NUMBER
IS
    v_qtd_wagons NUMBER;
    v_aux       NUMBER;
BEGIN
    -- valida freight
    SELECT COUNT(*) INTO v_aux
    FROM freights
    WHERE id = p_freight_id;

    IF v_aux = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Freight inexistente.');
    END IF;

    -- valida train
    SELECT COUNT(*) INTO v_aux
    FROM train
    WHERE id = p_train_id;

    IF v_aux = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Train inexistente.');
    END IF;

    -- associar
    UPDATE freights
       SET trainId = p_train_id
     WHERE id = p_freight_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Falha ao associar freight ao train.');
    END IF;

    -- contar wagons
    SELECT COUNT(*) INTO v_qtd_wagons
    FROM wagonfreights
    WHERE freightsId = p_freight_id;

    RETURN v_qtd_wagons;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20099, 'Erro inesperado na USBD34: ' || SQLERRM);
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