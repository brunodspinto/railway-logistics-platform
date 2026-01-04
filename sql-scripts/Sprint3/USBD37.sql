-- Função USBD37 --

CREATE OR REPLACE FUNCTION registerNewWagonModel (
    p_id             WagonModel.id%TYPE,
    p_nameModel      WagonModel.nameModel%TYPE,
    p_maker          WagonModel.maker%TYPE,
    p_length         WagonModel.length%TYPE,
    p_width          WagonModel.width%TYPE,
    p_height         WagonModel.height%TYPE,
    p_maxSpeed       WagonModel.maxSpeed%TYPE,
    p_payload        WagonModel.payload%TYPE,
    p_volume         WagonModel.volume%TYPE,
    p_wagonsTypeId   WagonModel.wagonsTypeId%TYPE,
    p_bogiesId       WagonModel.bogiesId%TYPE,
    p_gaugeMeasure   Gauge.measure%TYPE
)
RETURN SYS_REFCURSOR
AS
    refcursor SYS_REFCURSOR;
    v_count NUMBER;
BEGIN
    -- 1. Validar se o ID já existe
    SELECT COUNT(*)
    INTO v_count
    FROM WagonModel
    WHERE id = p_id;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20371, 'Wagon Model with this ID already exists.');
    END IF;

    -- 2. Validar se o Nome já existe
    SELECT COUNT(*)
    INTO v_count
    FROM WagonModel
    WHERE UPPER(nameModel) = UPPER(p_nameModel);

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20372, 'Wagon Model with this name already exists.');
    END IF;

    -- 3. Inserir na tabela WagonModel
    INSERT INTO WagonModel (
        id, nameModel, maker, length, width, height, maxSpeed, payload, volume, wagonsTypeId, bogiesId)
    VALUES (p_id, p_nameModel, p_maker, p_length, p_width, p_height, p_maxSpeed, p_payload, p_volume, p_wagonsTypeId, p_bogiesId);

    -- 4. Inserir o gauge único na tabela de associação GaugeWagonModel
    -- Nota: Assume-se que o p_gaugeMeasure existe na tabela Gauge (senão dá erro de FK)
    INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure)
    VALUES (p_id, p_gaugeMeasure);

    -- 5. Retornar cursor com os dados inseridos
    OPEN refcursor FOR
    SELECT wm.id,
        wm.nameModel,
        wm.maker,
        gwm.gaugeMeasure
    FROM WagonModel wm
         JOIN GaugeWagonModel gwm ON gwm.wagonModelId = wm.id
    WHERE wm.id = p_id;

    RETURN refcursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20373, 'Error while registering new Wagon Model: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD37 1--

DECLARE
    -- Variável para receber o cursor retornado pela função
v_cursor SYS_REFCURSOR;

    -- Variáveis para armazenar os dados lidos do cursor
    v_out_id     WagonModel.id%TYPE;
    v_out_name   WagonModel.nameModel%TYPE;
    v_out_maker  WagonModel.maker%TYPE;
    v_out_gauge  Gauge.measure%TYPE;

    -- Dados de teste para input
    v_test_id           NUMBER := 7777; -- Inserir ID
    v_test_wagonType    NUMBER := 1;
    v_test_bogie        NUMBER := 1;
    v_test_gauge        NUMBER := 1435; -- Gauge

BEGIN

DBMS_OUTPUT.PUT_LINE('--- A Iniciar Teste da USBD37 ---');

    -- 3. CHAMADA DA FUNÇÃO
    v_cursor := registerNewWagonModel(
        v_test_id,
        'Nome de Teste 1', -- Nome
        'Fabrica de Testes',
        15000,
        3000,
        4000,
        120,
        55.5,
        80.0,
		v_test_wagonType,
        v_test_bogie,
        v_test_gauge
    );

    -- 4. VERIFICAÇÃO DO RESULTADO
    LOOP
FETCH v_cursor INTO v_out_id, v_out_name, v_out_maker, v_out_gauge;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('--------------------------------');
        DBMS_OUTPUT.PUT_LINE('SUCESSO! Modelo Criado:');
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_out_id);
        DBMS_OUTPUT.PUT_LINE('Nome: ' || v_out_name);
        DBMS_OUTPUT.PUT_LINE('Fabricante: ' || v_out_maker);
        DBMS_OUTPUT.PUT_LINE('Gauge Associado: ' || v_out_gauge);
        DBMS_OUTPUT.PUT_LINE('--------------------------------');
END LOOP;

CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERRO NO TESTE: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD37 2--

DECLARE
    -- Variável para receber o cursor retornado pela função
v_cursor SYS_REFCURSOR;

    -- Variáveis para armazenar os dados lidos do cursor
    v_out_id     WagonModel.id%TYPE;
    v_out_name   WagonModel.nameModel%TYPE;
    v_out_maker  WagonModel.maker%TYPE;
    v_out_gauge  Gauge.measure%TYPE;

    -- Dados de teste para input
    v_test_id           NUMBER := 1245; -- Inserir ID
    v_test_wagonType    NUMBER := 1;
    v_test_bogie        NUMBER := 1;
    v_test_gauge        NUMBER := 1435; -- Gauge

BEGIN

DBMS_OUTPUT.PUT_LINE('--- A Iniciar Teste da USBD37 ---');

    -- 3. CHAMADA DA FUNÇÃO
    v_cursor := registerNewWagonModel(
        v_test_id,
        'Nome de Teste 2', -- Nome
        'Fabrica de Testes',
        15000,
        3000,
        4000,
        120,
        55.5,
        80.0,
		v_test_wagonType,
        v_test_bogie,
        v_test_gauge
    );

    -- 4. VERIFICAÇÃO DO RESULTADO
    LOOP
FETCH v_cursor INTO v_out_id, v_out_name, v_out_maker, v_out_gauge;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('--------------------------------');
        DBMS_OUTPUT.PUT_LINE('SUCESSO! Modelo Criado:');
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_out_id);
        DBMS_OUTPUT.PUT_LINE('Nome: ' || v_out_name);
        DBMS_OUTPUT.PUT_LINE('Fabricante: ' || v_out_maker);
        DBMS_OUTPUT.PUT_LINE('Gauge Associado: ' || v_out_gauge);
        DBMS_OUTPUT.PUT_LINE('--------------------------------');
END LOOP;

CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERRO NO TESTE: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD37 3--

DECLARE
    -- Variável para receber o cursor retornado pela função
v_cursor SYS_REFCURSOR;

    -- Variáveis para armazenar os dados lidos do cursor
    v_out_id     WagonModel.id%TYPE;
    v_out_name   WagonModel.nameModel%TYPE;
    v_out_maker  WagonModel.maker%TYPE;
    v_out_gauge  Gauge.measure%TYPE;

    -- Dados de teste para input
    v_test_id           NUMBER := 6666; -- Inserir ID
    v_test_wagonType    NUMBER := 1;
    v_test_bogie        NUMBER := 1;
    v_test_gauge        NUMBER := 1435; -- Gauge

BEGIN

DBMS_OUTPUT.PUT_LINE('--- A Iniciar Teste da USBD37 ---');

    -- 3. CHAMADA DA FUNÇÃO
    v_cursor := registerNewWagonModel(
        v_test_id,
        'Tadgs 32 94 082 3', -- Nome
        'Fabrica de Testes',
        15000,
        3000,
        4000,
        120,
        55.5,
        80.0,
		v_test_wagonType,
        v_test_bogie,
        v_test_gauge
    );

    -- 4. VERIFICAÇÃO DO RESULTADO
    LOOP
FETCH v_cursor INTO v_out_id, v_out_name, v_out_maker, v_out_gauge;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('--------------------------------');
        DBMS_OUTPUT.PUT_LINE('SUCESSO! Modelo Criado:');
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_out_id);
        DBMS_OUTPUT.PUT_LINE('Nome: ' || v_out_name);
        DBMS_OUTPUT.PUT_LINE('Fabricante: ' || v_out_maker);
        DBMS_OUTPUT.PUT_LINE('Gauge Associado: ' || v_out_gauge);
        DBMS_OUTPUT.PUT_LINE('--------------------------------');
END LOOP;

CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERRO NO TESTE: ' || SQLERRM);
END;
/