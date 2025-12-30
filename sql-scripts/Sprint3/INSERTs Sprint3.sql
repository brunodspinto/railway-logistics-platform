INSERT INTO AreaType (id, name) VALUES (1, 'warehouse');
INSERT INTO AreaType (id, name) VALUES (2, 'refrigerated area');
INSERT INTO AreaType (id, name) VALUES (3, 'grain silo');

INSERT INTO StationType (id, description) VALUES (1, 'Freight yards');
INSERT INTO StationType (id, description) VALUES (2, 'Stations');
INSERT INTO StationType (id, description) VALUES (3, 'Terminals');

INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (1, 'São Romão',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (2, 'Tamel',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (3, 'Senhora das Dores',3);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (4, 'Lousado',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (5, 'Porto Campanhã',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (6, 'Leandro',3);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (7, 'Porto São Bento',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (8, 'Barcelos',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (9, 'Vila Nova da Cerveira',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (10, 'Midões',3);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (11, 'Valença',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (12, 'Darque',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (13, 'Contumil',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (14, 'Ermesinde',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (15, 'São Frutuoso',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (16, 'São Pedro da Torre',3);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (17, 'Viana do Castelo',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (18, 'Famalicão',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (19, 'Barroselas',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (20, 'Nine',3);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (21, 'Caminha',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (22, 'Carvalha',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (23, 'Carreço',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (30, 'Braga',3);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (31, ' Manzagão',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (32, 'Cerqueiral',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (33, 'Gemieira',3);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (35, 'Paredes de Coura',1);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (43, 'São Gemil',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (45, 'São Mamede de Infesta',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (48, 'Leça do Balio',2);
INSERT INTO Station (idStation, nameStation, stationTypeId) VALUES (50, 'Leixões',1);

INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,1);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,3);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,4);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,7);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,9);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,10);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,11);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,13);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,16);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,18);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,23);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,30);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,32);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,33);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (1,45);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,1);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,2);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,6);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,7);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,8);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,9);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,12);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,13);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,17);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,20);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,30);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,32);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,33);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,35);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (2,50);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,1);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,3);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,5);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,8);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,11);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,14);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,15);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,19);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,21);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,22);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,23);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,31);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,32);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,43);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,45);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,48);
INSERT INTO AreaTypeStation (areaId, stationId) VALUES (3,50);

INSERT INTO Gauge (measure,name) VALUES (1668,'Bitola Ibérica');
INSERT INTO Gauge (measure,name)  VALUES (1435, 'Bitola Europeia');

INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT509017800', 'Medway - Operador Ferroviário de Mercadorias, S.A','Medway');
INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT507832388', 'Captrain Portugal S.A.','Captrain');
INSERT INTO Operator (vatNumber, name, shortName) VALUES ('PT503933813', 'Infraestruturas de Portugal, SA','IP');


INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (1,'Ramal São Bento - Campanhã','PT503933813',7,5,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (2,'Ramal Camapanhã - Contumil','PT503933813',5,13,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (3,'Ramal Contumil - Nine','PT503933813',13,20,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (4,'Ramal Nine - Barcelos','PT503933813',20,8,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (5,'Ramal Barcelos - Darque','PT503933813',8,12,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (6,'Ramal Darque - Viana','PT503933813',12,17,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (7,'Ramal Viana - Caminha','PT503933813',17,21,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (8,'Ramal Caminha - Torre','PT503933813',21,16,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (9,'Ramal Torre - Valença','PT503933813',16,11,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (21,'Ramal Contumil - São Gemil','PT503933813',13,43,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (22,'Ramal São Gemil - São Mamede de Infesta','PT503933813',43,45,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (23,'Ramal São Mamede de Infesta - Leça do Balio','PT503933813',45,48,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (24,'Ramal Leça do Balio - Leixões','PT503933813',48,50,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (30,'Ramal Braga','PT503933813',31,30,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (31,'Ramal Nine - Manzagão','PT503933813',20,31,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (32,'Ramal Manzagão - Cerqueiral','PT503933813',31,32,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (35,'Ramal Cerqueiral - Gemieira','PT503933813',32,33,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (36,'Ramal Gemieira - Paredes de Coura','PT503933813',33,35,1668);
INSERT INTO Line (id, nameLine, ownerLine, startStation, endStation, gaugeMeasure) VALUES (37,'Ramal Paredes de Coura - Valença','PT503933813',35,11,1668);

INSERT INTO LineSegmentType (id, description) VALUES (1,'single track');
INSERT INTO LineSegmentType (id, description) VALUES (2,'double track');
INSERT INTO LineSegmentType (id, description) VALUES (3,'quadruple track');

INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (1,8000,2618,1,1,3,1);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (3,8000,2443,1,1,3,2);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (10,8000,26560,1,1,2,3);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (11,8000,10000,1,2,2,3);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (15,8000,5286,1,1,2,4);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (16,8000,6000,1,2,2,4);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (14,8000,10387,1,1,2,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (12,8000,12000,1,2,2,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (13,8000,3100,1,3,2,5);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (20,6400,4890,1,1,2,6);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (18,8000,6000,1,1,1,7);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (21,8000,5000,1,2,1,7);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (22,8000,12000,1,3,1,7);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (25,8000,20829,1,1,1,8);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (26,8000,4264,1,1,1,9);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (30,8000,3883,1,1,2,21);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (31,8400,1174,1,1,2,22);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (32,8000,2534,1,2,2,22);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (33,8000,1566,1,1,2,23);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (34,8000,1453,1,2,2,23);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (35,8100,3597,1,1,2,24);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (36,8000,4334,1,2,2,24);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (50,8000,3555,1,1,2,30);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (51,8000,1222,1,5,2,31);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (52,8000,1760,1,4,2,31);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (53,8000,1720,1,3,2,31);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (54,8000,3350,1,2,2,31);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (55,8000,3470,1,1,2,31);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (58,8000,8050,1,1,2,32);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (59,8000,22320,1,1,2,35);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (60,8000,16310,1,1,2,36);
INSERT INTO LineSegment (id, maximumWeigh, lenght, isElectrified, segmentOrder, lineSegmentsTypeid, lineId) VALUES (61,8000,15200,1,1,2,37);

INSERT INTO Siding (id, position, lenght, lineSegmentId) VALUES (1,2000,864,21);
INSERT INTO Siding (id, position, lenght, lineSegmentId) VALUES (2,11000,266,21);

INSERT INTO WagonsType (id, description, freeWagonType) VALUES (1,'Cereal wagon', 2);
INSERT INTO WagonsType (id, description, freeWagonType) VALUES (2,'Covered wagon with sliding door', 3);
INSERT INTO WagonsType (id, description, freeWagonType) VALUES (3,'Container wagon (max 40'' HC)', 1);
INSERT INTO WagonsType (id, description, freeWagonType) VALUES (4,'Biodiesel wagaon', 2);
INSERT INTO WagonsType (id, description, freeWagonType) VALUES (5,'Wood wagon', 2);

INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (1, 'Bo-Bo', 2);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (2, 'Co-Co', 3);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (3, 'Co-Co', 3);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (4, 'Simples', 2);
INSERT INTO Bogies (id, nameBogie, numberBogies) VALUES (5, 'Duplo', 2);

INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1245,'Tadgs 32 94 082 3','Metalsines',17240,3072,4270,120,56,75,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1278,'Tdgs 41 94 074 1','Equimetal',9640,3120,4165.5,100,26.2,38,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1325,'Gabs 81 94 181 1','Sepsa Cometna',21700,3180,4170,100,50.2,110,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1104,'Regmms 32 94 356 3','Metalsines',14040,3104,2535,120,60.6,76.3,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (985,'Lgs 22 94 441 6','Metalsines',13860,2850,1060,120,28.1,76.3,4,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (987,'Sgnss 12 94 455 2','Emef',18116,2950,1030,120,68.4,76.3,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (988,'Sgnss 12 94 455 2','Emef',18116,2950,1030,120,68.4,76.3,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1525,'Zaes 81 94 788','Equimetal',13800,2950,4226,120,57.1,64.6,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1523,'Zaes 81 94 788','Equimetal',13800,2950,4226,120,57.1,64.6,5,1);
INSERT INTO WagonModel (id, nameModel, maker, length, width, height, maxSpeed, payload, volume, bogiesId, wagonsTypeId) VALUES (1212,'Kbs 41 94 333','Simmering',14020,2842,3300,100,25.8,62.7,4,1);

INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (1245,1668);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (1278,1668);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (1325,1668);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (1104,1668);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (985,1668);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (987,1668);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (988,1435);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (1525,1668);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (1523,1435);
INSERT INTO GaugeWagonModel (wagonModelId, gaugeMeasure) VALUES (1212,1668);

INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563077, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563078, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563079, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563080, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563081, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563082, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563083, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563084, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563085, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563086, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563087, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563088, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563089, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563090, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563091, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3563092, 21.2,31899,1104);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823045,24 ,32975,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823046,24 ,32975,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823047,24 ,32975,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (823048,24 ,32975,1245);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741001,13.8 ,28159,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741002,13.8 ,28159,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741003,13.8 ,28159,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741004,13.8 ,28159,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741005,13.8 ,28159,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (741006,13.8 ,28159,1278);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811010, 29.8,28159,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811011, 29.8,28159,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811012, 29.8,28159,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811013, 29.8,28159,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (1811014, 29.8,28159,1325);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330001, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330002, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330003, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330004, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330005, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330006, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330007, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330008, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330009, 14.2,38596,1212);
INSERT INTO Wagon (numberWagon, weight, yearOfEntry, wagonModelId) VALUES (3330010, 14.2,38596,1212);

INSERT INTO Electric (id, voltage, frequency) VALUES (1, 25000,50);
INSERT INTO Diesel (id, combustivelCapacity) VALUES (1, 4882);
INSERT INTO Diesel (id, combustivelCapacity) VALUES (2, 6700);

INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, bogiesId, electricId, dieselId) VALUES (1,'Siemens','Eurosprinter',5600,220,87,19.2,3,4.375,300,1 , 1,null);
INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, bogiesId, electricId, dieselId) VALUES (2,'Sorefame - Alsthom','CP 1900',1623,100,117,19.084,3.062,4.31,396,2 , null,1);
INSERT INTO LocomotiveModel (id, make, modelName, power, maxSpeed, weight, length, width, height, traction, bogiesId, electricId, dieselId) VALUES (3,'Stadler','E4000',3178,120,124,23.02,3000,4.264,400,3 ,  null,2);

INSERT INTO GaugeLocomotiveModel (locomotiveModelId, gaugeMeasure) VALUES (1, 1668);
INSERT INTO GaugeLocomotiveModel (locomotiveModelId, gaugeMeasure) VALUES (2, 1668);
INSERT INTO GaugeLocomotiveModel (locomotiveModelId, gaugeMeasure) VALUES (3, 1668);
INSERT INTO GaugeLocomotiveModel (locomotiveModelId, gaugeMeasure) VALUES (3, 1435);

INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (5621,'Inês', TO_DATE('01/01/1995','DD/MM/YYYY'),70,1);
INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (5623,'Paz', TO_DATE('01/04/1995','DD/MM/YYYY'),70,1);
INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (5630,'Helena', TO_DATE('02/01/1996','DD/MM/YYYY'),70,1);
INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (1903,'Eva', TO_DATE('07/04/1981','DD/MM/YYYY'),42.5,2);
INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (5034,'Adriana', TO_DATE('15/02/2017','DD/MM/YYYY'),100,3);
INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (5036,'Marina', TO_DATE('01/02/2017','DD/MM/YYYY'),100,3);
INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (335.001,'', TO_DATE('07/05/2019','DD/MM/YYYY'),100,3);
INSERT INTO Locomotive (numberLocomotive, name, serviceStart, operationalSpeed, model) VALUES (335.003,'', TO_DATE('03/06/2019','DD/MM/YYYY'),100,3);

INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5621,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5623,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5630,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (1903,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5034,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (5036,'PT509017800');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (335.001,'PT507832388');
INSERT INTO LocomotiveOperator(locomotiveNumber,operatorVatNumber) VALUES (335.003,'PT507832388');

INSERT INTO Train (id, dateTrain, timeTrain, maxLenght) VALUES (5421,TO_DATE('2025-10-03', 'YYYY-MM-DD'),TO_DATE('2025-10-03 09:45:00','YYYY-MM-DD HH24:MI:SS'),100);
INSERT INTO Train (id, dateTrain, timeTrain, maxLenght) VALUES (5435,TO_DATE('2025-10-03', 'YYYY-MM-DD'),TO_DATE('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'),100);
INSERT INTO Train (id, dateTrain, timeTrain, maxLenght) VALUES (5437,TO_DATE('2025-10-06', 'YYYY-MM-DD'),TO_DATE('2025-10-06 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),100);

INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5621 ,5421);
INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5623,5421);
INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5623,5435);
INSERT INTO Locomotive_Train (locomotiveNumber,trainId) VALUES (5621,5437);

INSERT INTO OperatorTrain (operatorVatNumber, trainId) VALUES ('PT509017800',5421);
INSERT INTO OperatorTrain (operatorVatNumber, trainId) VALUES ('PT509017800',5435);
INSERT INTO OperatorTrain (operatorVatNumber, trainId) VALUES ('PT509017800',5437);


INSERT INTO Freights (id, dateFreights, trainId) VALUES (2001,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2002,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2003,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2004,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2005,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5421);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2006,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5435);

INSERT INTO Freights (id, dateFreights, trainId) VALUES (2050,TO_DATE('2025-10-06', 'YYYY-MM-DD'),5437);
INSERT INTO Freights (id, dateFreights, trainId) VALUES (2051,TO_DATE('2025-10-06', 'YYYY-MM-DD'),5437);


INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563077, 2005);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563078, 2005);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563079, 2005);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563080, 2005);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330003, 2006);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330007, 2006);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330001, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330002, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330003, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330004, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330005, 2050);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3330006, 2050);

INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (1811011, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (1811012, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563077, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563078, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563079, 2051);
INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563080, 2051);


INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 24 , TO_DATE('2025-10-03 10:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 23, TO_DATE('2025-10-03 10:15:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 22, TO_DATE('2025-10-03 10:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 21, TO_DATE('2025-10-03 10:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 3, TO_DATE('2025-10-03 11:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 4, TO_DATE('2025-10-03 11:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 5, TO_DATE('2025-10-03 11:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 6, TO_DATE('2025-10-03 11:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 7, TO_DATE('2025-10-03 12:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 8, TO_DATE('2025-10-03 12:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5421, 9, TO_DATE('2025-10-03 12:30:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 9, TO_DATE('2025-10-03 18:15:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 8, TO_DATE('2025-10-03 18:30:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 7 , TO_DATE('2025-10-03 18:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 6 ,TO_DATE('2025-10-03 19:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 5 ,TO_DATE('2025-10-03 19:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 4 ,TO_DATE('2025-10-03 19:30:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 3 ,TO_DATE('2025-10-03 19:45:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 21 ,TO_DATE('2025-10-03 20:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 22 ,TO_DATE('2025-10-03 20:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 23 ,TO_DATE('2025-10-03 20:30:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5435, 24 ,TO_DATE('2025-10-03 20:45:00', 'YYYY-MM-DD HH24:MI:SS') );

INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 9 , TO_DATE('2025-10-06 10:15:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 8 , TO_DATE('2025-10-06 10:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 7 , TO_DATE('2025-10-06 10:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 6 , TO_DATE('2025-10-06 11:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 5 , TO_DATE('2025-10-06 11:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 4 , TO_DATE('2025-10-06 11:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 3 , TO_DATE('2025-10-06 11:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 21 ,TO_DATE('2025-10-06 12:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 22 ,TO_DATE('2025-10-06 12:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 23 ,TO_DATE('2025-10-06 12:30:00','YYYY-MM-DD HH24:MI:SS') );
INSERT INTO TrainLine (trainId, lineId, timeStation) VALUES (5437, 24 ,TO_DATE('2025-10-06 12:45:00', 'YYYY-MM-DD HH24:MI:SS') );



INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2001, 24,TO_DATE('2025-10-03 10:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2001, 23,TO_DATE('2025-10-03 10:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2001, 22,TO_DATE('2025-10-03 10:30:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2001, 21,TO_DATE('2025-10-03 10:45:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2001, 3, TO_DATE('2025-10-03 11:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2001, 4, TO_DATE('2025-10-03 11:15:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2001, 5, TO_DATE('2025-10-03 11:30:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2002, 3, TO_DATE('2025-10-03 11:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2002, 4,TO_DATE('2025-10-03 11:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2002, 5,TO_DATE('2025-10-03 11:30:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2002, 6,TO_DATE('2025-10-03 11:45:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2002, 7,TO_DATE('2025-10-03 12:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2002, 8,TO_DATE('2025-10-03 12:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2002, 9,TO_DATE('2025-10-03 12:30:00', 'YYYY-MM-DD HH24:MI:SS') );

INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 24, TO_DATE('2025-10-03 10:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 23,TO_DATE('2025-10-03 10:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 22,TO_DATE('2025-10-03 10:30:00','YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 21,TO_DATE('2025-10-03 10:45:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 3, TO_DATE('2025-10-03 11:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 4, TO_DATE('2025-10-03 11:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 5, TO_DATE('2025-10-03 11:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 6, TO_DATE('2025-10-03 11:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 7, TO_DATE('2025-10-03 12:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 8, TO_DATE('2025-10-03 12:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2003, 9, TO_DATE('2025-10-03 12:30:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 24, TO_DATE('2025-10-03 10:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 23, TO_DATE('2025-10-03 10:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 22, TO_DATE('2025-10-03 10:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 21, TO_DATE('2025-10-03 10:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 3, TO_DATE('2025-10-03 11:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 4, TO_DATE('2025-10-03 11:15:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 5, TO_DATE('2025-10-03 11:30:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 6, TO_DATE('2025-10-03 11:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2004, 7, TO_DATE('2025-10-03 12:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2005, 4,  TO_DATE('2025-10-03 11:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2005, 5, TO_DATE('2025-10-03 11:30:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2005, 6, TO_DATE('2025-10-03 11:45:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2005, 7, TO_DATE('2025-10-03 12:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2005, 8, TO_DATE('2025-10-03 12:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2005, 9, TO_DATE('2025-10-03 12:30:00', 'YYYY-MM-DD HH24:MI:SS') );

INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2006, 5,  TO_DATE('2025-10-03 19:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2006, 4,  TO_DATE('2025-10-03 19:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2006, 3,  TO_DATE('2025-10-03 19:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2006, 21, TO_DATE('2025-10-03 20:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2006, 22, TO_DATE('2025-10-03 20:15:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2006, 23, TO_DATE('2025-10-03 20:30:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2006, 24, TO_DATE('2025-10-03 20:45:00', 'YYYY-MM-DD HH24:MI:SS') );

INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2050, 5, TO_DATE('2025-10-06 11:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2050, 4, TO_DATE('2025-10-06 11:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2050, 3, TO_DATE('2025-10-06 11:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2050, 21, TO_DATE('2025-10-06 12:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2050, 22, TO_DATE('2025-10-06 12:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2050, 23, TO_DATE('2025-10-06 12:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2050, 24, TO_DATE('2025-10-06 12:45:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 9, TO_DATE('2025-10-06 10:15:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 8, TO_DATE('2025-10-06 10:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 7, TO_DATE('2025-10-06 10:45:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 6, TO_DATE('2025-10-06 11:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 5, TO_DATE('2025-10-06 11:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 4, TO_DATE('2025-10-06 11:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 3, TO_DATE('2025-10-06 11:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 21, TO_DATE('2025-10-06 12:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 22, TO_DATE('2025-10-06 12:15:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 23, TO_DATE('2025-10-06 12:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO LineFreights (freightsId, lineId, timeStation) VALUES (2051, 24, TO_DATE('2025-10-06 12:45:00', 'YYYY-MM-DD HH24:MI:SS'));

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

--INSERT INTO Freights (id, dateFreights, trainId) VALUES (2007,TO_DATE('2025-10-03', 'YYYY-MM-DD'),5437);

--INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563090, 2007);
--INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563091, 2007);
--INSERT INTO WagonFreights (wagonNumber, freightsId) VALUES (3563092, 2007);
