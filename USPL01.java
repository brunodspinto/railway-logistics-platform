@startuml
' Modelo conceptual do domínio ferroviário (USPL01)

class Line {
  name
  owner
}

class LineSegment {
  length
  trackType
  electrified
  gauge
}

class Station {
  name
  location
}

class Operator {
  name
}

class Locomotive {
  type
  power
  gauge
}

class Wagon {
  type
  capacity
  gauge
}

class Freight {
  departureDate
  arrivalDate
  status
}

class Route {
}

class Path {
}

class Terminal {
  name
  location
}

class Warehouse {
  code
}

' Perfis de utilizadores
class TrainDriver
class StationMaster
class StationStorageManager
class FreightManager
class Planner

' Relações
Line "1 " -- "many " LineSegment
LineSegment "many" -- "many" Station : connects >
Operator "1" -- "many  " Locomotive
Operator "1  " -- "many" Wagon
Freight "1" -- "many" Wagon
Freight "1" -- "1   " Route
Route "1 " -- "many" Path
Path "1   " -- "  many" Station : ordered >
Freight "1 " -- "1 " Station : origin
Freight "1  " -- "1  " Station : destination

Station "1 " -- "many " Terminal
Terminal "1 " -- "many  " Warehouse

' Ligações utilizadores
FreightManager -- Freight
StationMaster -- Station
StationStorageManager -- Warehouse
Planner -- Route
TrainDriver -- Locomotive

@enduml
