-- Função USBD43 --

CREATE OR REPLACE FUNCTION registerNewElectricLocoModel (
    p_id             LocomotiveModel.id%TYPE,
    p_make           LocomotiveModel.make%TYPE,
    p_modelName      LocomotiveModel.modelName%TYPE,
    p_power          LocomotiveModel.power%TYPE,
    p_maxSpeed       LocomotiveModel.maxSpeed%TYPE,
    p_weight         LocomotiveModel.weight%TYPE,
    p_length         LocomotiveModel.length%TYPE,
    p_width          LocomotiveModel.width%TYPE,
    p_height         LocomotiveModel.height%TYPE,
    p_traction       LocomotiveModel.traction%TYPE,
    p_bogiesId       LocomotiveModel.bogiesId%TYPE,
    p_voltage        Electric.voltage%TYPE,
    p_frequency      Electric.frequency%TYPE,
    p_gaugeMeasure   Gauge.measure%TYPE
)
RETURN SYS_REFCURSOR
AS
    refcursor SYS_REFCURSOR;
    v_count NUMBER;
    v_electricId NUMBER;
BEGIN
    -- 1. Validar se o ID do Modelo já existe
SELECT COUNT(*)
INTO v_count
FROM LocomotiveModel
WHERE id = p_id;

IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20431, 'Locomotive Model with this ID already exists.');
END IF;

    -- 2. Validar se o Nome do Modelo já existe
SELECT COUNT(*)
INTO v_count
FROM LocomotiveModel
WHERE UPPER(modelName) = UPPER(p_modelName);

IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20432, 'Locomotive Model with this name already exists.');
END IF;

    -- 3. Gerir a tabela Electric (Verificar se existe a combinação ou criar nova)
BEGIN
SELECT id
INTO v_electricId
FROM Electric
WHERE voltage = p_voltage
  AND frequency = p_frequency
  AND ROWNUM = 1;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            -- Se não existe, gerar novo ID e inserir
SELECT NVL(MAX(id), 0) + 1 INTO v_electricId FROM Electric;

INSERT INTO Electric (id, voltage, frequency)
VALUES (v_electricId, p_voltage, p_frequency);
END;

    -- 4. Inserir na tabela principal LocomotiveModel
    -- Nota: 'dieselId' fica NULL pois é uma locomotiva elétrica
INSERT INTO LocomotiveModel (
    id, make, modelName, power, maxSpeed, weight,
    length, width, height, traction, bogiesId, electricId, dieselId
) VALUES (
             p_id, p_make, p_modelName, p_power, p_maxSpeed, p_weight,
             p_length, p_width, p_height, p_traction, p_bogiesId, v_electricId, NULL
         );

-- 5. Inserir o gálibo na tabela GaugeLocomotiveModel
INSERT INTO GaugeLocomotiveModel (locomotiveModelId, gaugeMeasure)
VALUES (p_id, p_gaugeMeasure);

-- 6. Retornar cursor com os dados inseridos e a informação elétrica
OPEN refcursor FOR
SELECT lm.id,
       lm.modelName,
       lm.make,
       e.voltage,
       e.frequency,
       glm.gaugeMeasure
FROM LocomotiveModel lm
         JOIN Electric e ON lm.electricId = e.id
         LEFT JOIN GaugeLocomotiveModel glm ON glm.locomotiveModelId = lm.id
WHERE lm.id = p_id;

RETURN refcursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20433, 'Error while registering new Electric Loco Model: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD43 1--

DECLARE
v_cursor      SYS_REFCURSOR;

    -- Variáveis de saída
    v_out_id      LocomotiveModel.id%TYPE;
    v_out_name    LocomotiveModel.modelName%TYPE;
    v_out_make    LocomotiveModel.make%TYPE;
    v_out_volt    Electric.voltage%TYPE;
    v_out_freq    Electric.frequency%TYPE;
    v_out_gauge   Gauge.measure%TYPE;

    -- Dados de Teste
    v_test_id     NUMBER := 1235;
    v_test_bogie  NUMBER := 1;
    v_test_gauge  NUMBER := 1668; -- Ibérico

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- A Iniciar Teste da USBD43 ---');

    -- 3. CHAMADA DA FUNÇÃO
    v_cursor := registerNewElectricLocoModel(
		v_test_id,
        'Siemens',
        'Teste 1', -- nome
        6400,
        230,
        90000,
        19500,
        3000,
        4200,
        300,
        v_test_bogie,
        30000, -- voltagem
        75, -- frequência
        v_test_gauge
    );

    -- 4. VERIFICAÇÃO
    LOOP
FETCH v_cursor INTO v_out_id, v_out_name, v_out_make, v_out_volt, v_out_freq, v_out_gauge;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('--------------------------------');
        DBMS_OUTPUT.PUT_LINE('SUCESSO! Locomotiva Criada:');
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_out_id);
        DBMS_OUTPUT.PUT_LINE('Modelo: ' || v_out_name || ' (' || v_out_make || ')');
        DBMS_OUTPUT.PUT_LINE('Elétrica: ' || v_out_volt || 'V / ' || v_out_freq || 'Hz');
		DBMS_OUTPUT.PUT_LINE('Gauge: ' || v_out_gauge);
        DBMS_OUTPUT.PUT_LINE('--------------------------------');
END LOOP;

CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERRO NO TESTE: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD43 2--

DECLARE
v_cursor      SYS_REFCURSOR;

    -- Variáveis de saída
    v_out_id      LocomotiveModel.id%TYPE;
    v_out_name    LocomotiveModel.modelName%TYPE;
    v_out_make    LocomotiveModel.make%TYPE;
    v_out_volt    Electric.voltage%TYPE;
    v_out_freq    Electric.frequency%TYPE;
    v_out_gauge   Gauge.measure%TYPE;

    -- Dados de Teste
    v_test_id     NUMBER := 1;
    v_test_bogie  NUMBER := 1;
    v_test_gauge  NUMBER := 1668; -- Ibérico

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- A Iniciar Teste da USBD43 ---');

    -- 3. CHAMADA DA FUNÇÃO
    v_cursor := registerNewElectricLocoModel(
		v_test_id,
        'Siemens',
        'Teste 2', -- nome
        6400,
        230,
        90000,
        19500,
        3000,
        4200,
        300,
        v_test_bogie,
        30000, -- voltagem
        75, -- frequência
        v_test_gauge
    );

    -- 4. VERIFICAÇÃO
    LOOP
FETCH v_cursor INTO v_out_id, v_out_name, v_out_make, v_out_volt, v_out_freq, v_out_gauge;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('--------------------------------');
        DBMS_OUTPUT.PUT_LINE('SUCESSO! Locomotiva Criada:');
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_out_id);
        DBMS_OUTPUT.PUT_LINE('Modelo: ' || v_out_name || ' (' || v_out_make || ')');
        DBMS_OUTPUT.PUT_LINE('Elétrica: ' || v_out_volt || 'V / ' || v_out_freq || 'Hz');
		DBMS_OUTPUT.PUT_LINE('Gauge: ' || v_out_gauge);
        DBMS_OUTPUT.PUT_LINE('--------------------------------');
END LOOP;

CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERRO NO TESTE: ' || SQLERRM);
END;
/


-- Bloco Anónimo USBD43 3--

DECLARE
v_cursor      SYS_REFCURSOR;

    -- Variáveis de saída
    v_out_id      LocomotiveModel.id%TYPE;
    v_out_name    LocomotiveModel.modelName%TYPE;
    v_out_make    LocomotiveModel.make%TYPE;
    v_out_volt    Electric.voltage%TYPE;
    v_out_freq    Electric.frequency%TYPE;
    v_out_gauge   Gauge.measure%TYPE;

    -- Dados de Teste
    v_test_id     NUMBER := 7777;
    v_test_bogie  NUMBER := 1;
    v_test_gauge  NUMBER := 1668; -- Ibérico

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- A Iniciar Teste da USBD43 ---');

    -- 3. CHAMADA DA FUNÇÃO
    v_cursor := registerNewElectricLocoModel(
		v_test_id,
        'Siemens',
        'Eurosprinter', -- nome
        6400,
        230,
        90000,
        19500,
        3000,
        4200,
        300,
        v_test_bogie,
        30000, -- voltagem
        75, -- frequência
        v_test_gauge
    );

    -- 4. VERIFICAÇÃO
    LOOP
FETCH v_cursor INTO v_out_id, v_out_name, v_out_make, v_out_volt, v_out_freq, v_out_gauge;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('--------------------------------');
        DBMS_OUTPUT.PUT_LINE('SUCESSO! Locomotiva Criada:');
        DBMS_OUTPUT.PUT_LINE('ID: ' || v_out_id);
        DBMS_OUTPUT.PUT_LINE('Modelo: ' || v_out_name || ' (' || v_out_make || ')');
        DBMS_OUTPUT.PUT_LINE('Elétrica: ' || v_out_volt || 'V / ' || v_out_freq || 'Hz');
		DBMS_OUTPUT.PUT_LINE('Gauge: ' || v_out_gauge);
        DBMS_OUTPUT.PUT_LINE('--------------------------------');
END LOOP;

CLOSE v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERRO NO TESTE: ' || SQLERRM);
END;
/