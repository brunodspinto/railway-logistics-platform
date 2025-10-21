-- USBD08 - Lista de todas as locomotivas de um dado tipo
-- Utiliza o valor 'Electric' ou 'Diesel'

SELECT l.numberLocomotive      AS "Nº Locomotiva",
       l.name                  AS "Nome",
       l.yearOfEntry           AS "Ano Entrada",
       l.operationalSpeed      AS "Velocidade Operacional",
       lm.make                 AS "Fabricante",
       lm.modelName            AS "Modelo",
       lf.description          AS "Tipo Combustível"
FROM Locomotive l
JOIN LocomotiveModel lm 
     ON l.model = lm.id
JOIN LocomotiveFuelType lf 
     ON lm.locomotiveFuelTypeId = lf.id
WHERE lf.description = '&tipo';