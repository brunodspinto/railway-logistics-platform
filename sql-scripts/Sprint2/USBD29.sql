-- Função USBD29 --

CREATE OR REPLACE FUNCTION getPercentageElectricLocomotives(p_vatOperator Operator.vatNumber%TYPE)
RETURN SYS_REFCURSOR
IS
    result_cursor SYS_REFCURSOR;
BEGIN
    OPEN result_cursor FOR
        SELECT ROUND(COUNT(e.id) * 100 / COUNT(l.numberLocomotive), 2) AS "ElectricPercentage"
        FROM Operator o
        INNER JOIN LocomotiveOperator lo ON o.vatNumber = lo.operatorVatNumber
        INNER JOIN Locomotive l ON lo.locomotiveNumber = l.numberLocomotive
        INNER JOIN LocomotiveModel lm ON l.model = lm.id
        LEFT JOIN Electric e ON lm.id = e.locomotiveModelId
        WHERE o.vatNumber = p_vatOperator
        GROUP BY o.vatNumber;

    RETURN result_cursor;
EXCEPTION
    WHEN OTHERS THEN
        OPEN result_cursor FOR
            SELECT 'Error: It was not possible to obtain the locomotive percentage.' FROM dual;
        RETURN result_cursor;
END;

-- Bloco Anónimo USBD29 --

DECLARE
    v_result VARCHAR2(200);
    v_vatOperator Operator.vatNumber%TYPE := 'PT509017800';
    refcursor SYS_REFCURSOR;
BEGIN
    refcursor := getPercentageElectricLocomotives(v_vatOperator);

    DBMS_OUTPUT.PUT_LINE('Percentage of electric locomotives for operator ' || v_vatOperator || ':');

    LOOP
        FETCH refcursor INTO v_result;
        EXIT WHEN refcursor%notfound;

        IF v_result IS NULL THEN
            DBMS_OUTPUT.PUT_LINE('N/A (No locomotives found for this operator)');
        ELSIF v_result LIKE 'Error%' THEN
            DBMS_OUTPUT.PUT_LINE(v_result);
        ELSE
            DBMS_OUTPUT.PUT_LINE(v_result || '%');
        END IF;

    END LOOP;
    CLOSE refcursor;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error when obtaining locomotive data: ' || SQLERRM);
END;
