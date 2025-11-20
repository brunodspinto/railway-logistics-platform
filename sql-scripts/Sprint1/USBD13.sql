SELECT
    l.numberLocomotive      AS "Nº Locomotiva",
    l.name                  AS "Nome",
    l.yearOfEntry           AS "Ano de Entrada",
    l.operationalSpeed      AS "Velocidade Operacional",
    lm.make                 AS "Fabricante",
    lm.modelName            AS "Modelo",
    lf.description          AS "Tipo Combustível",
    g.measure               AS "Medida gauge (mm)",
    o.shortName             AS "Operador"
FROM Locomotive l
         INNER JOIN LocomotiveModel lm ON l.model = lm.id
         INNER JOIN LocomotiveFuelType lf ON lm.locomotiveFuelTypeId = lf.id
         INNER JOIN Gauge g ON lm.gaugeId = g.idGauge
         INNER JOIN Operator o ON l.operator = o.vatNumber
WHERE lf.description = 'Electric'
  AND g.measure = 1668
  AND o.shortName = 'Medway'
ORDER BY l.numberLocomotive ASC;