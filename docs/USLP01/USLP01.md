# USLP01 - Domain Model

## User Story
**As a Product Owner**, I want the domain model to be created (conceptual level).

## Description
This model will be an essential communication element between all the stakeholders. The domain model diagram is a "dynamic" document, which should continuously reflect the stakeholders' shared understanding of the domain.

## Acceptance Criteria
- The data model should cover the activity of the railway system operation
- What is expected: a "basic" model shared in Visual Paradigm or the documentation in GitHub (e.g., PlantUML)

## Key Domain Concepts

### Railway Infrastructure
- **Rail Lines**: Routes connecting stations and terminals
- **Line Segments**: Portions of lines with specific characteristics
    - Single or double track
    - Electrified or not
    - Gauge (width), maximum weight, speed limit
- **Stations/Terminals**: Facilities for loading/unloading
- **Freight Yards**: Areas for sorting and assembling trains

### Rolling Stock
- **Locomotives**: Engines that pull freight trains
    - Diesel or electric
    - Power, acceleration, dimensions
    - Weight, fuel capacity, gauge
- **Wagons/Freight Cars**: Specialized for different goods
    - Boxcars, Flatcars, Tank cars, Hopper cars, Refrigerated cars
    - Payload, volume capacity, dimensions, tare

### Operations
- **Freight**: Set of wagons transported from A to B
- **Path**: Ordered list of stations for travel
- **Route**: Simple (single freight) or complex (multiple freights)

### Users
- Train Drivers
- Station Masters
- Station Storage Manager
- Freight Manager
- Schedulers/Planners

## Deliverables
- Domain model diagram (Visual Paradigm or PlantUML)
- Model should be maintained in SVG format
- Documentation in repository (GitHub)

## Notes
- This is a living document that evolves with project understanding
- Model should facilitate communication between all team members and stakeholders
- Should integrate concepts from all course units (ARQCP, BDDAD, ESINF, FSIAP, LAPR3)