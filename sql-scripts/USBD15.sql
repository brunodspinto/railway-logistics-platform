SELECT COUNT(l.id) AS "Number of Lines Depart"
FROM Line l
         JOIN Station s
              ON l.startStation = s.idStation
WHERE s.nameStation = '&name_station';
