| Name                           | Labels    | Description                                                                         |
|:-------------------------------|:----------|:------------------------------------------------------------------------------------|
| bogieId                        | Attribute | Foreign key referencing the bogie entity.                                           |
| Bogies                         | Entity    | Specifies bogie.                                                                    |
| combustibleCapacity            | Attribute | Specifies fuel capacity.                                                            |
| description_LineSegmentsType   | Attribute | Textual description of the LineSegmentsType entity.                                 |
| description_LocomotiveFuelType | Attribute | Textual description of the LocomotiveFuelType entity.                               |
| description_StationType        | Attribute | Textual description of the StationType entity.                                      |
| description_WagonsType         | Attribute | Textual description of the WagonsType entity.                                       |
| Diesel                         | Entity    | Specifies diesel fuel type.                                                         |
| Electric                       | Entity    | Specifies electric fuel type.                                                       |
| endStation                     | Attribute | Specifies final station.                                                            |
| frequency                      | Attribute | Specifies frequency.                                                                |
| Gauge                          | Entity    | Specifies gauge.                                                                    |
| gaugeId_Line                   | Attribute | Foreign key referencing the gauge entity.                                           |
| gaugeId_LocomotiveModel        | Attribute | Foreign key referencing the gauge entity.                                           |
| gaugeId_WagonModel             | Attribute | Foreign key referencing the gauge entity.                                           |
| height_LocomotiveModel         | Attribute | Specifies height.                                                                   |
| height_WagonModel              | Attribute | Specifies height.                                                                   |
| id_Bogies                      | Attribute | Unique identifier of the id entity.                                                 |
| id_Diesel                      | Attribute | Unique identifier of the id entity.                                                 |
| id_Electric                    | Attribute | Unique identifier of the id entity.                                                 |
| id_Line                        | Attribute | Unique identifier of the id entity.                                                 |
| id_LineSegment                 | Attribute | Unique identifier of the id entity.                                                 |
| id_LineSegmentsType            | Attribute | Unique identifier of the id entity.                                                 |
| id_LocomotiveFuelType          | Attribute | Unique identifier of the id entity.                                                 |
| id_LocomotiveModel             | Attribute | Unique identifier of the id entity.                                                 |
| id_StationType                 | Attribute | Unique identifier of the id entity.                                                 |
| id_WagonModel                  | Attribute | Unique identifier of the id entity.                                                 |
| id_WagonsType                  | Attribute | Unique identifier of the id entity.                                                 |
| idGauge                        | Attribute | Unique identifier of the idGauge entity.                                            |
| idStation                      | Attribute | Unique identifier of the idStation entity.                                          |
| isElectrified                  | Attribute | Boolean value indicating whether the line segment supports electric locomotives.    |
| length_LineSegment             | Attribute | Total length of the entity, measured in meters.                                     |
| length_LocomotiveModel         | Attribute | Total length of the entity, measured in meters.                                     |
| length_WagonModel              | Attribute | Total length of the entity, measured in meters.                                     |
| Line                           | Entity    | Specifies railway line composed of several segments.                                |
| lineId                         | Attribute | Foreign key referencing the line entity.                                            |
| LineSegment                    | Entity    | Specifies section of railway line between two points.                               |
| LineSegmentsType               | Entity    | Specifies category that defines the type of a railway line segment.                 |
| lineSegmentsTypeId             | Attribute | Foreign key referencing the lineSegmentsType entity.                                |
| Locomotive                     | Entity    | Specifies motorized vehicle that provides traction for freight trains.              |
| LocomotiveFuelType             | Entity    | Specifies motorized vehicle that provides traction for freight trains.              |
| locomotiveFuelType_Diesel      | Attribute | Specifies motorized vehicle that provides traction for freight trains.              |
| locomotiveFuelType_Electric    | Attribute | Specifies motorized vehicle that provides traction for freight trains.              |
| locomotiveFuelTypeId           | Attribute | Foreign key referencing the locomotiveFuelType entity.                              |
| LocomotiveModel                | Entity    | Specifies motorized vehicle that provides traction for freight trains.              |
| make                           | Attribute | Specifies manufacturer.                                                             |
| maximumWeight                  | Attribute | Maximum supported load per meter of track, expressed in kilograms per meter (kg/m). |
| maxLoad                        | Attribute | Maximum load.                                                                       |
| maxSpeed_LocomotiveModel       | Attribute | Maximum speed.                                                                      |
| maxSpeed_WagonModel            | Attribute | Maximum speed.                                                                      |
| measure                        | Attribute | Specifies gauge measurement.                                                        |
| model                          | Attribute | Specifies model reference.                                                          |
| modelName                      | Attribute | Specifies model name.                                                               |
| name_Gauge                     | Attribute | Specifies gauge name.                                                               |
| name_Operator                  | Attribute | Refers to the railway operating company responsible for this locomotive or wagon.   |
| nameBogie                      | Attribute | Specifies bogie name.                                                               |
| nameLine                       | Attribute | Specifies official name of the railway line.                                        |
| nameModel                      | Attribute | Specifies model name.                                                               |
| nameStation                    | Attribute | Official name of the railway station.                                               |
| numberLocomotive               | Attribute | Specifies motorized vehicle that provides traction for freight trains.              |
| numberWagon                    | Attribute | Specifies freight vehicle used to transport goods on the railway.                   |
| operationSpeed                 | Attribute | Specifies operating speed.                                                          |
| Operator                       | Entity    | Refers to the railway operating company responsible for this locomotive or wagon.   |
| operator_Locomotive            | Attribute | Refers to the railway operating company responsible for this locomotive or wagon.   |
| operator_Wagon                 | Attribute | Refers to the railway operating company responsible for this locomotive or wagon.   |
| ownerId                        | Attribute | Foreign key referencing the owner entity.                                           |
| power                          | Attribute | Specifies power.                                                                    |
| segmentNoOrder                 | Attribute | Defines the sequential order of the segment within a railway line.                  |
| shortName                      | Attribute | Specifies short name.                                                               |
| startStation                   | Attribute | Specifies initial station.                                                          |
| Station                        | Entity    | Specifies railway location where stops or loading/unloading operations occur.       |
| StationType                    | Entity    | Specifies station type.                                                             |
| stationTypeId                  | Attribute | Foreign key referencing the stationType entity.                                     |
| traction                       | Attribute | Specifies traction type.                                                            |
| vatNumber                      | Attribute | Specifies company tax number.                                                       |
| voltage                        | Attribute | Specifies voltage.                                                                  |
| Wagon                          | Entity    | Specifies freight vehicle used to transport goods on the railway.                   |
| WagonModel                     | Entity    | Specifies freight vehicle used to transport goods on the railway.                   |
| wagonModelId                   | Attribute | Foreign key referencing the wagonModel entity.                                      |
| WagonsType                     | Entity    | Specifies freight vehicle used to transport goods on the railway.                   |
| wagonsTypeId                   | Attribute | Foreign key referencing the wagonsType entity.                                      |
| weight_LocomotiveModel         | Attribute | Specifies weight.                                                                   |
| weight_Wagon                   | Attribute | Total weight of the wagon, expressed in kilograms.                                  |
| width_LocomotiveModel          | Attribute | Unique identifier of the width entity.                                              |
| width_WagonModel               | Attribute | Unique identifier of the width entity.                                              |
| yearOfEntry_Locomotive         | Attribute | Specifies year of entry into service.                                               |
| yearOfEntry_Wagon              | Attribute | Specifies year of entry into service.                                               |