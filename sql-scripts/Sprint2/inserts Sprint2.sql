INSERT INTO StationType (id, description) VALUES (1, 'Freight yards');
INSERT INTO StationType (id, description) VALUES (2, 'Stations');
INSERT INTO StationType (id, description) VALUES (3, 'Terminals');

INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (1, 'São Romão',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (2, 'Tamel',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (3, 'Senhora das Dores',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (4, 'Lousado',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (5, 'Porto Campanhã',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (6, 'Leandro',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (7, 'Porto São Bento',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (8, 'Barcelos',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (9, 'Vila Nova da Cerveira',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (10, 'Midões',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (11, 'Valença',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (12, 'Darque',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (13, 'Contumil',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (14, 'Ermesinde',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (15, 'São Frutuoso',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (16, 'São Pedro da Torre',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (17, 'Viana do Castelo',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (18, 'Famalicão',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (19, 'Barroselas',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (20, 'Nine',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (21, 'Caminha',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (22, 'Carvalha',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (23, 'Carreço',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (50, 'Leixões',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (45, 'São Mamede de Infesta',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (48, 'Leça do Balio',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (43, 'São Gemil',1);

INSERT INTO LineSegmentType (id, description) VALUES (1,'single track');
INSERT INTO LineSegmentType (id, description) VALUES (2,'double track');
INSERT INTO LineSegmentType (id, description) VALUES (3,'quadruple track');

INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT509017800', 'Medway - Operador Ferroviário de Mercadorias, S.A','Medway');
INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT507832388', 'Captrain Portugal S.A.','Captrain');
INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT503933813', 'Infraestruturas de Portugal, SA','IP');

INSERT INTO Gauge (idGauge, measure) VALUES (1,'1668');
INSERT INTO Gauge (idGauge, measure) VALUES (2,'1435');

INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (1,'Ramal São Bento - Campanhã','PT503933813',7,5,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (2,'Ramal Camapanhã - Contumil','PT503933813',5,13,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (3,'Ramal Contumil - Nine','PT503933813',13,20,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (4,'Ramal Nine - Barcelos','PT503933813',20,8,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (5,'Ramal Barcelos - Darque','PT503933813',8,12,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (6,'Ramal Darque - Viana','PT503933813',12,17,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (7,'Ramal Viana - Caminha','PT503933813',17,21,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (8,'Ramal Caminha - Torre','PT503933813',21,16,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (9,'Ramal Torre - Valença','PT503933813',16,11,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (21,'Ramal Contumil - São Gemil','PT503933813',13,43,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (22,'Ramal São Gemil - São Mamede de Infesta','PT503933813',43,45,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (23,'Ramal São Mamede de Infesta - Leça do Balio','PT503933813',45,48,1);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeId) VALUES (24,'Ramal Leça do Balio - Leixões','PT503933813',48,50,1);

INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (1,8000,2618,1,1,'','',3,1);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (3,8000,2443,1,1,'','',3,2);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (10,8000,26560,1,1,'','',3,3);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (11,8000,10000,1,2,'','',3,3);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (15,8000,5286,1,1,'','',3,4);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (16,8000,6000,1,2,'','',3,4);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (14,8000,10387,1,1,'','',3,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (12,8000,12000,1,2,'','',3,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (13,8000,3100,1,3,'','',3,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (20,6400,4890,1,1,'','',3,6);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (18,8000,6000,1,1,'','',3,7);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (21,8000,5000,1,2,2000,864,3,7);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (22,8000,12000,1,3,'','',3,7);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (25,8000,20829,1,1,11000,266,3,8);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (26,8000,4264,1,1,'','',3,9);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (30,8000,3883,1,1,'','',3,21);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (31,8400,1174,1,1,'','',3,22);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (32,8000,2534,1,2,'','',3,22);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (33,8000,1566,1,1,'','',3,23);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (34,8000,1453,1,2,'','',3,23);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (35,8100,3597,1,1,'','',3,24);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, sidingPosition, sidingLength, lineSegmentsTypeid, lineId) VALUES (36,8000,4334,1,2,'','',3,24);

INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (1, 'Bo-Bo', 2);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (2, 'Co-Co', 2);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (3, 'Co-Co', 3);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (4, 'Simples', 2);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (5, 'Duplo', 2);
INSERT INTO WagonsType (id, description) VALUES (1,'Cereal wagon');
INSERT INTO WagonsType (id, description) VALUES (2,'Covered wagon with sliding door');
INSERT INTO WagonsType (id, description) VALUES (3,'Container wagon (max 40'' HC)');
INSERT INTO WagonsType (id, description) VALUES (4,'Biodiesel wagaon');
INSERT INTO WagonsType (id, description) VALUES (5,'Wood wagon');


INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1245,'Tadgs 32 94 082 3','Metalsines',17240,3072,4270,120,56,75,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1278,'Tdgs 41 94 074 1','Equimetal',9640,3120,4165.5,100,26.2,38,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1325,'Gabs 81 94 181 1','Sepsa Cometna',21700,3180,4170,100,50.2,110,5,2);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1104,'Regmms 32 94 356 3','Metalsines',14040,3104,2535,120,60.6,76.3,5,3);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (985,'Lgs 22 94 441 6','Metalsines',13860,2850,1060,120,28.1,76.3,4,3);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (987,'Sgnss 12 94 455 2','Emef',18116,2950,1030,120,68.4,76.3,5,3);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (988,'Sgnss 12 94 455 2','Emef',18116,2950,1030,120,68.4,76.3,5,3);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1525,'Zaes 81 94 788','Equimetal',13800,2950,4226,120,57.1,64.6,5,4);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1523,'Zaes 81 94 788','Equimetal',13800,2950,4226,120,57.1,64.6,5,4);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1212,'Kbs 41 94 333','Simmering',14020,2842,3300,100,25.8,62.7,4,5);


INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563077, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563078, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563079, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563080, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563081, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563082, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563083, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563084, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563085, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563086, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563087, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563088, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563089, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563090, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563091, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563092, 21.2,1987,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823045,24 ,1990,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823046,24 ,1990,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823047,24 ,1990,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823048,24 ,1990,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741001,24 ,1977,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741002,24 ,1977,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741003,24 ,1977,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741004,24 ,1977,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741005,24 ,1977,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741006,24 ,1977,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811010,24 ,1977,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811011,24 ,1977,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811012,24 ,1977,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811013,24 ,1977,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811014,24 ,1977,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330001,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330002,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330003,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330004,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330005,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330006,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330007,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330008,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330009,24 ,2005,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330010,24 ,2005,1212);

INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563077);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563078);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563079);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563080);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563081);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563082);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563083);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563084);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563085);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563086);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563087);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563088);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563089);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563090);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563091);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3563092);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',823045);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',823046);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',823047);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',823048);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',741001);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',741002);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',741003);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',741004);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',741005);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',741006);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',1811010);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',1811011);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',1811012);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',1811013);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',1811014);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330001);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330002);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330003);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330004);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330005);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330006);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330007);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330008);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330009);
INSERT INTO OperatorWagon (operatorVatNumber, wagonNumber) VALUES ('PT509017800',3330010);

INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'1245');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'1278');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'1325');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'1104');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'985');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'987');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (2,'988');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'1525');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (2,'1523');
INSERT INTO GaugeWagonModel (gaugeId, wagonModelId) VALUES (1,'1212');

INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, bogiesId) VALUES (1,'Siemens','Eurosprinter',5600,220,87,19.2,3,4.375,300,1);
INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, bogiesId) VALUES (2,'Sorefame - Alsthom','CP 1900',1623,100,117,19.084,3.062,4.31,396,2);
INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, bogiesId) VALUES (3,'Stadler','E4000',3178,120,124,23.02,3000,4.264,400,3);

INSERT INTO Electric (id, voltage, frequency, locomotiveModelId) VALUES (1, 25000,50,1);

INSERT INTO Diesel (id, combustivelCapacity, locomotiveModelId) VALUES (1, 4882,2);
INSERT INTO Diesel (id, combustivelCapacity, locomotiveModelId) VALUES (2, 6700,3);

INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (5621,'Inês',1995,70,1);
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (5623,'Paz',1995,70,1);
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (5630,'Helena',1996,70,1);
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (1903,'Eva',1981,42.5,2);
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (5034,'Adriana',2017,100,3);
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (5036,'Marina',2017,100,3);
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (335001,'',2019,100,3);
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model) VALUES (335003,'',2019,100,3);

INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5621,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5623,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5630,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (1903,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5034,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5036,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (335001,'PT507832388');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (335003,'PT507832388');

INSERT INTO Train (id, dateTrain, timeTrain) VALUES (5421,TO_DATE('2025-10-03', 'YYYY-MM-DD'),TO_DATE('09:45:00', 'HH24:MI:SS'));
INSERT INTO Train (id, dateTrain, timeTrain) VALUES (5435,TO_DATE('2025-10-03', 'YYYY-MM-DD'),TO_DATE('18:00:00', 'HH24:MI:SS'));
INSERT INTO Train (id, dateTrain, timeTrain) VALUES (5437,TO_DATE('2025-10-06', 'YYYY-MM-DD'),TO_DATE('10:00:00', 'HH24:MI:SS'));

INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5621 ,5421);
INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5623,5421);
INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5623,5435);
INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5621,5437);

INSERT INTO OperatorTrain (operatorVatNumber, trainId) VALUES ('PT509017800',5421);
INSERT INTO OperatorTrain (operatorVatNumber, trainId) VALUES ('PT509017800',5435);
INSERT INTO OperatorTrain (operatorVatNumber, trainId) VALUES ('PT509017800',5437);

INSERT INTO RouteType (id, description) VALUES (1,'simple');
INSERT INTO RouteType (id, description) VALUES (2,'complex');

INSERT INTO Freights (id, dateFreights, trainId) VALUES (2001,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2002,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2003,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2004,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2005,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2006,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5435);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2050,TO_DATE('2025-10-06', 'YYYY-MM-DD'),5437);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2051,TO_DATE('2025-10-06', 'YYYY-MM-DD'),5437);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2007,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);


INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330001,2001);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330002,2001);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330004,2001);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330005,2001);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330006,2001);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563089,2002);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (1811011, 2003);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (1811012, 2003);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (1811013, 2004);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563077, 2005);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563078, 2005);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563079, 2005);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563080, 2005);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330003, 2006);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330007, 2006);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563090, 2007);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563091, 2007);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563092, 2007);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330001, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330002, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330004, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330005, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330006, 2050);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (1811011, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (1811012, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563077, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563078, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563079, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563080, 2051);


INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,50,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,48,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,45,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,43,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,13,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,20,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,8,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,12,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,17,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,21,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,16,2,50);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5421,11,2,50);

INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,11,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,16,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,21,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,17,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,12,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,8,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,20,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,13,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,43,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,45,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,48,1,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5435,50,1,11);

INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,11,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,16,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,21,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,17,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,12,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,8,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,20,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,13,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,43,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,45,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,48,2,11);
INSERT INTO Route (trainId, stationId, routeTypeId, inicialStation) VALUES (5437,50,2,11);


INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,50,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,48,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,45,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,43,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,13,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,20,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,8,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2001,12,50);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,13,13);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,20,13);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,8,13);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,12,13);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,17,13);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,21,13);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,16,13);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2002,11,13);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,50,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,48,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,45,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,43,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,13,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,20,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,8,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,12,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,17,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,21,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,16,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2003,11,50);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,50,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,48,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,45,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,43,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,13,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,20,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,8,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,12,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,17,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2004,21,50);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2005,20,20);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2005,8,20);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2005,12,20);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2005,17,20);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2005,21,20);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2005,16,20);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2005,11,20);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,12,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,8,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,20,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,13,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,43,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,45,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,48,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2006,50,12);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2007,50,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2007,48,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2007,45,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2007,43,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2007,13,50);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2007,5,50);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,12,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,8,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,20,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,13,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,43,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,45,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,48,12);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2050,50,12);

INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,11,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,16,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,21,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,17,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,12,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,8,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,20,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,13,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,43,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,45,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,48,11);
INSERT INTO Path (freightsId, stationId, inicialStation) VALUES (2051,50,11);


INSERT INTO AreaType (id, name) VALUES (1, 'warehouse');
INSERT INTO AreaType (id, name) VALUES (2, 'refrigerated area');
INSERT INTO AreaType (id, name) VALUES (3, 'grain silo');


INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,2);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,5);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,9);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,11);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,13);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,16);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,17);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,18);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,21);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,50);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,45);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,48);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,43);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,3);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,5);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,7);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,11);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,12);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,13);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,16);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,19);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,21);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,50);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,45);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,6);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,11);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,13);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,20);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,23);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,50);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,45);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,43);
