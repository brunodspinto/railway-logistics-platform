-- Função USBD38 --

CREATE OR REPLACE FUNCTION func_add_gauge(
    p_measure IN Gauge.measure%TYPE,
    p_name    IN Gauge.name%TYPE
) RETURN VARCHAR2 IS

BEGIN
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

SET SERVEROUTPUT ON;

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Teste 1: Adicionar Bitola Métrica ---');

    v_result := func_add_gauge(1000, 'Bitola Métrica');

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;

-- Bloco Anónimo USBD38 2 --

SET SERVEROUTPUT ON;

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Teste 2: Tentar duplicar Bitola Ibérica ---');

    v_result := func_add_gauge(1668, 'Bitola Ibérica Duplicada');

    DBMS_OUTPUT.PUT_LINE(v_result);

    ROLLBACK;
END;
