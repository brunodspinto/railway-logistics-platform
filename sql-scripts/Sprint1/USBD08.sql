SELECT l.numberLocomotive      AS "Nº Locomotiva",
       l.name                  AS "Nome",
       l.yearOfEntry           AS "Ano Entrada",
       l.operationalSpeed      AS "Velocidade Operacional",
       lm.make                 AS "Fabricante",
       lm.modelName            AS "Modelo",
       lf.description          AS "Tipo Combustível"
FROM Locomotive l
INNER JOIN LocomotiveModel lm
     ON l.model = lm.id
INNER JOIN LocomotiveFuelType lf
     ON lm.locomotiveFuelTypeId = lf.id
WHERE lf.description = 'Diesel/Electric';