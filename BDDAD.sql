CREATE TABLE Line (
    line_id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    owner VARCHAR2(100)
);

CREATE TABLE Line_Segment (
    segment_id NUMBER PRIMARY KEY,
    line_id NUMBER NOT NULL,
    length_km NUMBER(6,2),
    track_type VARCHAR2(10),
    electrified CHAR(1),
    gauge NUMBER(6),
    max_weight NUMBER(10),
    speed_limit NUMBER(3)
);

CREATE TABLE Station (
    station_id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    location VARCHAR2(100),
    type VARCHAR2(30)
);

CREATE TABLE Line_Connection (
    segment_id NUMBER NOT NULL,
    station_id NUMBER NOT NULL,
    PRIMARY KEY (segment_id, station_id)
);

CREATE TABLE Operator (
    operator_id NUMBER PRIMARY KEY,
    name VARCHAR2(100)
);

CREATE TABLE Locomotive (
    locomotive_id NUMBER PRIMARY KEY,
    operator_id NUMBER NOT NULL,
    make VARCHAR2(50),
    model VARCHAR2(50),
    year_service NUMBER(4),
    type VARCHAR2(10),
    power NUMBER(6),
    acceleration NUMBER(4,2),
    dimensions VARCHAR2(100),
    total_weight NUMBER(8),
    num_bogies NUMBER(2),
    fuel_capacity NUMBER(8),
    gauge NUMBER(6)
);

CREATE TABLE Wagon (
    wagon_id NUMBER PRIMARY KEY,
    operator_id NUMBER NOT NULL,
    type VARCHAR2(20),
    payload_capacity NUMBER(8),
    volume_capacity NUMBER(8,2),
    dimensions VARCHAR2(100),
    tare NUMBER(8),
    gauge NUMBER(6)
);

CREATE TABLE Freight (
    freight_id NUMBER PRIMARY KEY,
    origin_station NUMBER,
    destination_station NUMBER,
    departure_date DATE NOT NULL,
    arrival_date DATE NOT NULL
);

CREATE TABLE Freight_Wagon (
    freight_id NUMBER NOT NULL,
    wagon_id NUMBER NOT NULL,
    PRIMARY KEY (freight_id, wagon_id)
);

CREATE TABLE Route (
    route_id NUMBER PRIMARY KEY,
    freight_id NUMBER NOT NULL
);

CREATE TABLE Route_Station (
    route_id NUMBER NOT NULL,
    station_id NUMBER NOT NULL,
    order_num NUMBER NOT NULL,
    PRIMARY KEY (route_id, order_num)
);

ALTER TABLE Line_Segment ADD CONSTRAINT FK_LineSegment_Line FOREIGN KEY (line_id) REFERENCES Line(line_id);

ALTER TABLE Line_Connection ADD CONSTRAINT FK_LineConnection_Segment FOREIGN KEY (segment_id) REFERENCES Line_Segment(segment_id);

ALTER TABLE Line_Connection ADD CONSTRAINT FK_LineConnection_Station FOREIGN KEY (station_id) REFERENCES Station(station_id);

ALTER TABLE Locomotive ADD CONSTRAINT FK_Locomotive_Operator FOREIGN KEY (operator_id) REFERENCES Operator(operator_id);

ALTER TABLE Wagon ADD CONSTRAINT FK_Wagon_Operator FOREIGN KEY (operator_id) REFERENCES Operator(operator_id);

ALTER TABLE Route ADD CONSTRAINT FK_Route_Freight FOREIGN KEY (freight_id) REFERENCES Freight(freight_id);

ALTER TABLE Route_Station ADD CONSTRAINT FK_RouteStation_Route FOREIGN KEY (route_id) REFERENCES Route(route_id);

ALTER TABLE Route_Station ADD CONSTRAINT FK_RouteStation_Station FOREIGN KEY (station_id) REFERENCES Station(station_id);

ALTER TABLE Freight ADD CONSTRAINT FK_Freight_Origin FOREIGN KEY (origin_station) REFERENCES Station(station_id);

ALTER TABLE Freight ADD CONSTRAINT FK_Freight_Destination FOREIGN KEY (destination_station) REFERENCES Station(station_id);

ALTER TABLE Freight_Wagon ADD CONSTRAINT FK_FreightWagon_Freight FOREIGN KEY (freight_id) REFERENCES Freight(freight_id);

ALTER TABLE Freight_Wagon ADD CONSTRAINT FK_FreightWagon_Wagon FOREIGN KEY (wagon_id) REFERENCES Wagon(wagon_id);