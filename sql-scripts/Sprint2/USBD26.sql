-- Função USBD26 --

CREATE OR REPLACE FUNCTION get_unused_wagons (
    p_start_date IN DATE,
    p_end_date   IN DATE
) RETURN SYS_REFCURSOR
AS
    v_result_cursor SYS_REFCURSOR;
BEGIN
OPEN v_result_cursor FOR
SELECT
    w.numberWagon        AS wagon_number,
    wm.nameModel         AS model_name,
    wm.maker             AS manufacturer,
    wt.description       AS wagon_type,
    w.weight             AS tare_weight,
    wm.payload           AS payload_capacity,
    o.shortName          AS operator_name,
    w.yearOfEntry        AS year_of_entry
FROM
    Wagon w
        INNER JOIN WagonModel wm
                   ON w.wagonModelId = wm.id
        INNER JOIN WagonsType wt
                   ON wm.wagonsTypeId = wt.id
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
          AND TRUNC(f.dateFreights) BETWEEN TRUNC(p_start_date)
            AND TRUNC(p_end_date)
    )
ORDER BY
    wt.description ASC,
    w.numberWagon  ASC;

RETURN v_result_cursor;
END get_unused_wagons;


-- Bloco Anónimo USBD26 --

DECLARE
c                  SYS_REFCURSOR;
  v_wagon_number     Wagon.numberWagon%TYPE;
  v_model_name       WagonModel.nameModel%TYPE;
  v_manufacturer     WagonModel.maker%TYPE;
  v_wagon_type       WagonsType.description%TYPE;
  v_tare_weight      Wagon.weight%TYPE;
  v_payload_capacity WagonModel.payload%TYPE;
  v_operator_name    Operator.shortName%TYPE;
  v_year_of_entry    Wagon.yearOfEntry%TYPE;
BEGIN
  -- Abre o cursor
  c := get_unused_wagons(
         DATE '2025-01-01',
         DATE '2025-06-30'
       );

  LOOP
FETCH c
      INTO v_wagon_number,
           v_model_name,
           v_manufacturer,
           v_wagon_type,
           v_tare_weight,
           v_payload_capacity,
           v_operator_name,
           v_year_of_entry;
    EXIT WHEN c%NOTFOUND;

    -- Imprime uma linha por cada registo
    DBMS_OUTPUT.PUT_LINE(
      'Wagon ' || v_wagon_number
      || ' | Modelo: ' || v_model_name
      || ' | Fabricante: ' || v_manufacturer
      || ' | Tipo: ' || v_wagon_type
      || ' | Tara: ' || v_tare_weight
      || ' | Carga: ' || v_payload_capacity
      || ' | Operador: ' || NVL(v_operator_name,'<nenhum>')
      || ' | Ano Entrada: ' || v_year_of_entry
    );
END LOOP;

CLOSE c;
END;

-- Bloco Anónimo USBD26 2--

DECLARE
c                  SYS_REFCURSOR;
  v_wagon_number     Wagon.numberWagon%TYPE;
  v_model_name       WagonModel.nameModel%TYPE;
  v_manufacturer     WagonModel.maker%TYPE;
  v_wagon_type       WagonsType.description%TYPE;
  v_tare_weight      Wagon.weight%TYPE;
  v_payload_capacity WagonModel.payload%TYPE;
  v_operator_name    Operator.shortName%TYPE;
  v_year_of_entry    Wagon.yearOfEntry%TYPE;
BEGIN
  -- Abre o cursor
  c := get_unused_wagons(
         DATE '2025-10-03',
         DATE '2025-10-03'
       );

  LOOP
FETCH c
      INTO v_wagon_number,
           v_model_name,
           v_manufacturer,
           v_wagon_type,
           v_tare_weight,
           v_payload_capacity,
           v_operator_name,
           v_year_of_entry;
    EXIT WHEN c%NOTFOUND;

    -- Imprime uma linha por cada registo
    DBMS_OUTPUT.PUT_LINE(
      'Wagon ' || v_wagon_number
      || ' | Modelo: ' || v_model_name
      || ' | Fabricante: ' || v_manufacturer
      || ' | Tipo: ' || v_wagon_type
      || ' | Tara: ' || v_tare_weight
      || ' | Carga: ' || v_payload_capacity
      || ' | Operador: ' || NVL(v_operator_name,'<nenhum>')
      || ' | Ano Entrada: ' || v_year_of_entry
    );
END LOOP;

CLOSE c;
END;