SELECT COUNT(l.id) AS "Number of Lines Depart"
FROM Line l
         INNER JOIN Station s
              ON l.startStation = s.idStation
WHERE s.nameStation = 'Nine';
