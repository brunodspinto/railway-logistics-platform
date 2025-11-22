
-- Função USBD26 --

CREATE OR REPLACE FUNCTION get_unused_wagons(
    p_start_date IN DATE,
    p_end_date IN DATE
)
RETURN SYS_REFCURSOR
AS
    v_result_cursor SYS_REFCURSOR;
    BEGIN
OPEN v_result_cursor FOR
SELECT
    w.numberWagon AS wagon_number,
    wm.nameModel AS model_name,
    wm.maker AS manufacturer,
    wt.description AS wagon_type,
    w.weight AS tare_weight,
    wm.payload AS payload_capacity,
    o.shortName AS operator_name,
    w.yearOfEntry AS year_of_entry
FROM
    Wagon w
        INNER JOIN WagonModel wm ON w.wagonModelId = wm.id
        INNER JOIN WagonsType wt ON wm.wagonsTypeId = wt.id
        INNER JOIN OperatorWagon ow ON w.numberWagon = ow.wagonNumber
        INNER JOIN Operator o ON ow.operatorVatNumber = o.vatNumber
WHERE
    NOT EXISTS (
        SELECT 1
        FROM WagonFreights wf
                 INNER JOIN Freights f ON wf.freightsId = f.id
        WHERE wf.wagonNumber = w.numberWagon
          AND f.dateFreights BETWEEN p_start_date AND p_end_date
    )
ORDER BY
    wt.description ASC,
    w.numberWagon ASC;

RETURN v_result_cursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END get_unused_wagons;
/