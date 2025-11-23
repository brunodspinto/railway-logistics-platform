-- Função USBD26 --

CREATE OR REPLACE FUNCTION get_unused_wagons (p_start_date IN DATE, p_end_date IN DATE)
RETURN SYS_REFCURSOR
IS
    result_cursor SYS_REFCURSOR;
BEGIN

OPEN result_cursor FOR
SELECT
    w.numberWagon  AS wagon_number,
    o.shortName    AS operator_name
FROM
    Wagon w
        LEFT JOIN OperatorWagon ow
                  ON w.numberWagon = ow.wagonNumber
        LEFT JOIN Operator o
                  ON ow.operatorVatNumber = o.vatNumber
WHERE
    NOT EXISTS (
        SELECT 1
        FROM WagonFreights wf
            INNER JOIN Freights f
                ON wf.freightsId = f.id
        WHERE wf.wagonNumber = w.numberWagon
            AND TRUNC(f.dateFreights)
                BETWEEN TRUNC(p_start_date)
            AND TRUNC(p_end_date)
    )
ORDER BY
    w.numberWagon;

RETURN result_cursor;

EXCEPTION
    WHEN OTHERS THEN
        OPEN result_cursor FOR
            SELECT 'Error: It was not possible to obtain the locomotive percentage.' FROM dual;
        RETURN result_cursor;
END;



-- Bloco Anónimo USBD26 1--

DECLARE
    v_start_date DATE := DATE '2025-09-01';
    v_end_date   DATE := DATE '2025-09-30';
    c SYS_REFCURSOR;
    v_wagon_number  Wagon.numberWagon%TYPE;
    v_operator_name Operator.shortName%TYPE;
    invalid_interval EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Teste 1: '||TO_CHAR(v_start_date, 'YYYY-MM-DD')|| ' a ' ||TO_CHAR(v_end_date, 'YYYY-MM-DD'));

    IF v_start_date > v_end_date THEN
        RAISE invalid_interval;
    END IF;

    c := get_unused_wagons(v_start_date, v_end_date);
    LOOP
        FETCH c INTO v_wagon_number, v_operator_name;
        EXIT WHEN c%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Wagon '||v_wagon_number||' | Operator: '||NVL(v_operator_name,'<none>'));
    END LOOP;
CLOSE c;

EXCEPTION
    WHEN invalid_interval THEN
        DBMS_OUTPUT.PUT_LINE('Error: the initial date ('||TO_CHAR(v_start_date,'YYYY-MM-DD')||') can not be later than the end date ('||TO_CHAR(v_end_date,'YYYY-MM-DD')||').');

WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error when obtaining wagon data: ' || SQLERRM);
END;


-- Bloco Anónimo USBD26 2--

DECLARE
    v_start_date DATE := DATE '2025-10-03';
    v_end_date   DATE := DATE '2025-10-03';
    c SYS_REFCURSOR;
    v_wagon_number  Wagon.numberWagon%TYPE;
    v_operator_name Operator.shortName%TYPE;
    invalid_interval EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Teste 2: '||TO_CHAR(v_start_date, 'YYYY-MM-DD')|| ' a ' ||TO_CHAR(v_end_date, 'YYYY-MM-DD'));

    IF v_start_date > v_end_date THEN
        RAISE invalid_interval;
    END IF;

    c := get_unused_wagons(v_start_date, v_end_date);
    LOOP
        FETCH c INTO v_wagon_number, v_operator_name;
        EXIT WHEN c%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Wagon '||v_wagon_number||' | Operator: '||NVL(v_operator_name,'<none>'));
    END LOOP;
CLOSE c;

EXCEPTION
    WHEN invalid_interval THEN
        DBMS_OUTPUT.PUT_LINE('Error: the initial date ('||TO_CHAR(v_start_date,'YYYY-MM-DD')||') can not be later than the end date ('||TO_CHAR(v_end_date,'YYYY-MM-DD')||').');

    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error when obtaining wagon data: ' || SQLERRM);
END;


-- Bloco Anónimo USBD26 3--

DECLARE
    v_start_date DATE := DATE '2025-10-06';
    v_end_date   DATE := DATE '2025-10-06';
    c SYS_REFCURSOR;
    v_wagon_number  Wagon.numberWagon%TYPE;
    v_operator_name Operator.shortName%TYPE;
    invalid_interval EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Teste 3: '||TO_CHAR(v_start_date, 'YYYY-MM-DD')|| ' a ' ||TO_CHAR(v_end_date, 'YYYY-MM-DD'));

    IF v_start_date > v_end_date THEN
        RAISE invalid_interval;
    END IF;

    c := get_unused_wagons(v_start_date, v_end_date);
    LOOP
        FETCH c INTO v_wagon_number, v_operator_name;
        EXIT WHEN c%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Wagon '||v_wagon_number||' | Operator: '||NVL(v_operator_name,'<none>'));
    END LOOP;
CLOSE c;

EXCEPTION
    WHEN invalid_interval THEN
        DBMS_OUTPUT.PUT_LINE('Error: the initial date ('||TO_CHAR(v_start_date,'YYYY-MM-DD')||') can not be later than the end date ('||TO_CHAR(v_end_date,'YYYY-MM-DD')||').');

    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error when obtaining wagon data: ' || SQLERRM);
END;


-- Bloco Anónimo USBD26 4--

DECLARE
    v_start_date DATE := DATE '2025-10-03';
    v_end_date   DATE := DATE '2025-10-06';
    c SYS_REFCURSOR;
    v_wagon_number  Wagon.numberWagon%TYPE;
    v_operator_name Operator.shortName%TYPE;
    invalid_interval EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Teste 4: '||TO_CHAR(v_start_date, 'YYYY-MM-DD')|| ' a ' ||TO_CHAR(v_end_date, 'YYYY-MM-DD'));

    IF v_start_date > v_end_date THEN
        RAISE invalid_interval;
END IF;

    c := get_unused_wagons(v_start_date, v_end_date);
    LOOP
        FETCH c INTO v_wagon_number, v_operator_name;
        EXIT WHEN c%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Wagon '||v_wagon_number||' | Operator: '||NVL(v_operator_name,'<none>'));
    END LOOP;
CLOSE c;

EXCEPTION
    WHEN invalid_interval THEN
        DBMS_OUTPUT.PUT_LINE('Error: the initial date ('||TO_CHAR(v_start_date,'YYYY-MM-DD')||') can not be later than the end date ('||TO_CHAR(v_end_date,'YYYY-MM-DD')||').');

    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error when obtaining wagon data: ' || SQLERRM);
END;


-- Bloco Anónimo USBD26 5--

DECLARE
    v_start_date DATE := DATE '2025-11-01';
    v_end_date   DATE := DATE '2025-10-01';
    c SYS_REFCURSOR;
    v_wagon_number  Wagon.numberWagon%TYPE;
    v_operator_name Operator.shortName%TYPE;
    invalid_interval EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Teste 5: '||TO_CHAR(v_start_date, 'YYYY-MM-DD')|| ' a ' ||TO_CHAR(v_end_date, 'YYYY-MM-DD'));

    IF v_start_date > v_end_date THEN
        RAISE invalid_interval;
    END IF;

    c := get_unused_wagons(v_start_date, v_end_date);
    LOOP
        FETCH c INTO v_wagon_number, v_operator_name;
        EXIT WHEN c%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Wagon '||v_wagon_number||' | Operator: '||NVL(v_operator_name,'<none>'));
    END LOOP;
CLOSE c;

EXCEPTION
    WHEN invalid_interval THEN
        DBMS_OUTPUT.PUT_LINE('Error: the initial date ('||TO_CHAR(v_start_date,'YYYY-MM-DD')||') can not be later than the end date ('||TO_CHAR(v_end_date,'YYYY-MM-DD')||').');

    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error when obtaining wagon data: ' || SQLERRM);
END;