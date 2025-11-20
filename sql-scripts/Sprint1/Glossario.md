| Name                           | Labels    | Description                                                                                                                               |
|:-------------------------------|:----------|:------------------------------------------------------------------------------------------------------------------------------------------|
| Bogies                         | Entity    | Refers to wheeled structures located beneath railway vehicles, such as wagons.                                                            |
| combustibleCapacity            | Attribute | Refers to the amount of fuel Locomotive that can hold, measured in liters (L).                                                            |
| description_LineSegmentsType   | Attribute | Textual description of the LineSegmentsTy (single or double track).                                                                       |
| description_LocomotiveFuelType | Attribute | Textual description of the LocomotiveFuelType (Electric or Diesel).                                                                       |
| description_StationType        | Attribute | Textual description of the StationType (Freight yards, stations, and terminals).                                                          |
| description_WagonsType         | Attribute | Textual description of the WagonsType (Boxcars, Flatcars, Tank cars, Hopper cars, Refrigerated cars).                                     |
| Diesel                         | Entity    | Specifies diesel fuel type that have voltage and frequency.                                                                               |
| Electric                       | Entity    | Specifies electric fuel type that have a capacity.                                                                                        |
| Gauge                          | Entity    | Refers to a measurement or standard used in railway terms, it often refers to the distance between the inner sides of the two rails.      |                                                                                                                     |
| length_LineSegment             | Attribute | Refers to the size(m) of a line segment.                                                                                                  |
| length_LocomotiveModel         | Attribute | Total length of the Locomotive Model, measured in meters.                                                                                 |
| length_WagonModel              | Attribute | Total length of the Wagon Model, measured in meters.                                                                                      |
| Line                           | Entity    | Specifies railway line composed of several segments that connect two end points.                                                          |
| LineSegment                    | Entity    | Specifies a part of railway line that have lenght, maximum weigh, if is eletrified and order.                                             |
| LineSegmentsType               | Entity    | Specifies category that defines the type of a railway line segment((single or double track).                                              |
| Locomotive                     | Entity    | Specifies motorized vehicle that provides traction for freight trains.                                                                    |
| LocomotiveFuelType             | Entity    | Specifies the fuel type (Electric or Diesel) of a locomotive.                                                                             |
| LocomotiveModel                | Entity    | Specifies a model of vehicle that provides traction for freight trains that have a name, maker unique configuration.                      |
| make                           | Attribute | Specifies manufacturer of a Locomotive.                                                                                                   |
| maxSpeed_LocomotiveModel       | Attribute | Maximum speed of a Locomotive.                                                                                                            |
| maxSpeed_WagonModel            | Attribute | Maximum speed of a Wagon.                                                                                                                 |
| measure                        | Attribute | Specifies gauge measurement.                                                                                                              |
| model                          | Attribute | Specifies a unique model of a Locomotiva and a Wagon.                                                                                     |
| modelName                      | Attribute | Specifies model name.                                                                                                                     |
| name_Operator                  | Attribute | Refers to the railway operating company responsible for this locomotive or wagon.                                                         |
| nameBogie                      | Attribute | Specifies bogie name (Bo-Bo, Co-Co, Simples, Duplo).                                                                                      |
| nameLine                       | Attribute | Specifies official name of the railway line.                                                                                              |
| nameModel                      | Attribute | Specifies model name.                                                                                                                     |
| nameStation                    | Attribute | Official name of the railway station.                                                                                                     |
| numberLocomotive               | Attribute | Specifies the identifier of a motorized vehicle that provides traction for freight trains.                                                |
| numberWagon                    | Attribute | Specifies the identifier of a freight vehicle used to transport goods on the railway.                                                     |
| operationSpeed                 | Attribute | Specifies velocity of a Locomotive.                                                                                                       |
| Operator                       | Entity    | Refers to the railway operating company responsible for this locomotive or wagon.                                                         |
| PayLoad                        | Attribute | Maximum Cargo.                                                                                                                            |
| power                          | Attribute | Refers to the amount of energy a locomotive can generate to move itself and its train.                                                    |
| shortName                      | Attribute | Specifies short name of an Operator.                                                                                                      |
| Station                        | Entity    | Specifies railway location where stops or loading/unloading operations occur.                                                             |
| StationType                    | Entity    | Specifies station type(Freight yards, stations, and terminals).                                                                           |
| traction                       | Attribute | Refers to the force exerted by a locomotive on the surface that allows it to move and maintain grip.                                      |
| vatNumber                      | Attribute | Specifies the inique number of a Operator of the company.                                                                                 |
| voltage                        | Attribute | Specifies the mesure in Volts.                                                                                                            |
| Wagon                          | Entity    | Specifies freight vehicle used to transport goods on the railway.                                                                         |
| WagonModel                     | Entity    | Specifies the configuration of freight vehicle used to transport goods on the railway.                                                    |
| WagonsType                     | Entity    | Specifies the type freight vehicle used to transport goods on the railway (Boxcars, Flatcars, Tank cars, Hopper cars, Refrigerated cars). |
| weight_Wagon                   | Attribute | Total weight of the wagon, expressed in kilograms.                                                                                        |
| yearOfEntry_Locomotive         | Attribute | Specifies year of a Locomotive entry into service.                                                                                        |
| yearOfEntry_Wagon              | Attribute | Specifies year of a Wagon entry into service.                                                                                             |