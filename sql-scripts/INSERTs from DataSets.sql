
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

INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT509017800', 'Medway - Operador Ferroviário de Mercadorias, S.A','Medway');
INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT503933813', 'Infraestruturas de Portugal, SA','IP');

INSERT INTO Bogies (id, nameBogie) VALUES (1, 'Bo-Bo');
INSERT INTO Bogies (id, nameBogie) VALUES (2, 'Co-Co');
INSERT INTO Bogies (id, nameBogie) VALUES (3, 'Simples');
INSERT INTO Bogies (id, nameBogie) VALUES (4, 'Duplo');

INSERT INTO Gauge (idGauge, measure) VALUES (1,'1668');
INSERT INTO Gauge (idGauge, measure) VALUES (2,'1435');

INSERT INTO LineSegmentsType (id, description) VALUES (1,'single track');
INSERT INTO LineSegmentsType (id, description) VALUES (2,'double track');

INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (1,'Ramal São Bento - Campanhã','PT503933813',7,5,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (2,'Ramal Campanhã - Nine','PT503933813',5,20,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (3,'Ramal Nine - Barcelos','PT503933813',20,8,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (4,'Ramal Barcelos - Viana','PT503933813',8,17,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (5,'Ramal viana - Caminha','PT503933813',17,21,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (6,'Ramal Caminha - Torre','PT503933813',21,16,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (7,'Ramal Torre - Valença','PT503933813',16,11,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (8,'Ramal Campanhã - Contumil','PT503933813',5,13,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (9,'Ramal Contumil - Ermesinde','PT503933813',13,14,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (10,'Ramal Ermesinde - Lousado','PT503933813',14,4,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (11,'Ramal Lousado - Famalicão','PT503933813',4,18,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (12,'Ramal Famalicão - Nine','PT503933813',18,20,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (13,'Ramal Nine - Tamel','PT503933813',20,2,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (14,'Ramal Tamel - Barcelos','PT503933813',2,8,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (15,'Ramal Barcelos - Barroselas','PT503933813',8,19,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (16,'Ramal Barroselas - Darque','PT503933813',19,12,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (17,'Ramal Darque - Viana do Castelo','PT503933813',12,17,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (18,'Ramal Viana do Castelo - Carreço','PT503933813',17,23,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (19,'Ramal Carreço - Caminha','PT503933813',23,21,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (20,'Ramal Caminha - Vila Nova da Cerveira','PT503933813',21,9,1);
INSERT INTO Line (id, nameLine, ownerId, startStation, endStation, gaugeId) VALUES (21,'Ramal Vila Nova da Cerveira - São Pedro da Torre','PT503933813',9,16,1);

INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (1,8000,2618,1,1,2,1);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (10,8000,29003,1,1,1,2);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (11,8000,10000,1,2,1,2);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (15,8000,5286,1,1,1,3);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (16,8000,6000,1,2,1,3);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (14,8000,10387,1,1,1,4);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (12,8000,12000,1,2,1,4);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (13,6400,8000,1,3,1,4);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (20,8000,6000,1,1,1,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (21,8000,3000,1,2,1,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (22,8000,15000,1,3,1,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (25,8000,20829,1,1,1,6);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmantesOrder,lineSegmentsTypeid, lineId) VALUES (26,8000,4264,1,1,1,7);


INSERT INTO WagonsType (id, description) VALUES (1,'Cereal wagon');
INSERT INTO WagonsType (id, description) VALUES (2,'Covered wagon with sliding door');

INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId, gaugeId) VALUES (1245,'Tadgs 32 94 082 3','Metalsines',17240,3072,4270,120,56,75,4,1,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId, gaugeId) VALUES (1278,'Tdgs 41 94 074 1','Equimetal',9640,3120,4165.5,100,26.2,38,4,1,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId, gaugeId) VALUES (1325,'Gabs 81 94 181 1','Sepsa Cometna',21700,3180,4170,100,50.2,110,4,2,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId, gaugeId) VALUES (1104,'Regmms 32 94 356 3','Metalsines',14040,3104,2535,120,60.6,76.3,4,2,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId, gaugeId) VALUES (985,'Lgs 22 94 441 6','Metalsines',13860,2850,1060,120,28.1,76.3,3,2,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId, gaugeId) VALUES (987,'Sgnss 12 94 455 2','Emef',18116,2950,1030,120,68.4,76.3,4,2,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId, gaugeId) VALUES (988,'Sgnss 12 94 455 2','Emef',18116,2950,1030,120,68.4,76.3,4,2,2);

INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563077, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563078, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563079, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563080, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563081, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563082, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563083, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563084, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563085, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563086, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563087, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563088, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563089, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563090, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563091, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (3563092, 21.2,1987,1104,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (823045,24,1990,1245,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (823046,24,1990,1245,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (823047,24,1990,1245,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (823048,24,1990,1245,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (741001,13.8,1977,1278,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (741002,13.8,1977,1278,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (741003,13.8,1977,1278,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (741004,13.8,1977,1278,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (741005,13.8,1977,1278,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (741006,13.8,1977,1278,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (1811010,29.8 ,1977,1325,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (1811011,29.8 ,1977,1325,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (1811012,29.8 ,1977,1325,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (1811013,29.8 ,1977,1325,'PT509017800');
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId, operator) VALUES (1811014,29.8 ,1977,1325,'PT509017800');

INSERT INTO LocomotiveFuelType (id, description) VALUES (1, 'Electric');
INSERT INTO LocomotiveFuelType (id, description) VALUES (2, 'Diesel');

INSERT INTO Electric (id, voltage, frequency, locomotiveFuelType) VALUES (1, 25,50,1);
INSERT INTO Diesel (id, combustivelCapacity, locomotiveFuelType) VALUES (1, 4882,2);

INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, locomotiveFuelTypeId, bogiesId, gaugeId) VALUES (1,'Siemens','Eurosprinter',5600,220,87,19.2,3,4.375,300,1,1,1);
INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, locomotiveFuelTypeId, bogiesId, gaugeId) VALUES (2,'Sorefame - Alsthom','CP 1900',1623,100,117,19.084,3.062,4.31,396,2,2,1);

INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model, operator) VALUES (5621,'Inês',1995,70,1,'PT509017800');
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model, operator) VALUES (5623,'Paz',1995,70,1,'PT509017800');
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model, operator) VALUES (5630,'Helena',1996,70,1,'PT509017800');
INSERT INTO Locomotive (numberLocomotive, name, yearOfEntry, operationalSpeed, model, operator) VALUES (1903,'Eva',1981,42.5,2,'PT509017800');
