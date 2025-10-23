SELECT COUNT(*) as "number of segments" , Operator.VATNUMBER, LineSegmentsType.DESCRIPTION
FROM 
    LineSegment
INNER JOIN 
    LineSegmentsType
ON 
    LineSegment.lineSegmentsTypeid = LineSegmentsType.id
INNER JOIN
    Line 
ON
    LineSegment.lineId = Line.id
INNER JOIN
    Operator
ON
    Line.ownerId = Operator.vatNumber
    where Operator.VATNUMBER = 'PT503933813'
    and LineSegmentsType.DESCRIPTION = 'double track'
group by Operator.VATNUMBER, LineSegmentsType.DESCRIPTION;
