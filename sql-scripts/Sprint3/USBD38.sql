-- Função USBD38 --

CREATE OR REPLACE FUNCTION func_add_gauge(
    p_measure IN Gauge.measure%TYPE,
    p_name    IN Gauge.name%TYPE
) RETURN VARCHAR2 IS

BEGIN
    SELECT COUNT(*) INTO v_count
    FROM Gauge
    WHERE UPPER(name) = UPPER(p_name);

    IF v_count > 0 THEN
        RETURN 'Erro: Já existe uma bitola com o nome "' || p_name || '" (ou similar).';
    END IF;

    INSERT INTO Gauge (measure, name)
    VALUES (p_measure, p_name);

    RETURN 'Sucesso: Nova bitola (' || p_measure || ' mm - ' || p_name || ') adicionada.';

EXCEPTION
    -- Erro se a Primary Key (measure) já existir
    WHEN DUP_VAL_ON_INDEX THEN
        RETURN 'Erro: Já existe uma bitola com a medida ' || p_measure || ' mm.';
    WHEN OTHERS THEN
        RETURN 'Erro inesperado: ' || SQLERRM;
END;

-- Bloco Anónimo USBD38 1 --

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Teste 1: Adicionar Bitola Métrica ---');

    v_result := func_add_gauge(1000, 'Bitola Métrica');

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;

-- Bloco Anónimo USBD38 2 --

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Teste 2: Tentar nome duplicado com Case Diferente ---');

    v_result := func_add_gauge(1000, 'Bitola Métrica');

    v_result := func_add_gauge(1001, 'BITOLA MÉTRICA');

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;

-- Bloco Anónimo USBD38 3 --

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Teste 3: Tentar duplicar Bitola Ibérica ---');

    v_result := func_add_gauge(1668, 'Bitola Ibérica Duplicada');

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;
