# USLP03 - Travel Time Calculator - Assumptions

## 1. Overview

This document describes the technical assumptions made during the implementation of USLP03 (Travel Time Calculator) for Sprint 1.

## 2. Acceptance Criteria

- Acceleration is assumed to be instantaneous
- Sections are listed in the order they will be covered
- Speed is considered in the calculation of the trip

## 3. Data Available in CSV Files

The following data is available from the provided CSV files:

**locomotives.csv**
- Max Speed: Real Data (examples: 220 km/h, 100 km/h)

**segments.csv**
- Length (m): Real Data (examples: 2618 m, 29003 m)
- Max Weight (kg/m): Real Data (examples: 8000, 6400)
- Max Speed: MISSING

## 4. Critical Assumption: Segment Maximum Speed

### 4.1 Problem

The CSV file segments.csv does not include a Max Speed column for railway segments. Without this data, we cannot determine the infrastructure speed limit for each track section.

### 4.2 Solution

We infer the maximum speed from the Max Weight (kg/m) column using the following mapping:

**Mapping Logic:**
- If Max Weight is greater than or equal to 8000 kg/m, then Max Speed is 120 km/h (Robust infrastructure)
- Otherwise, Max Speed is 100 km/h (Standard infrastructure)

This logic is implemented in the LineSegment class using a conditional statement that checks the maxWeightKgPerM value and returns the appropriate speed limit.

### 4.3 Justification

This correlation is based on European Railway Standards:

**High Standard (ERA Class B)**
- Max Weight: 8000+ kg/m
- Typical Max Speed: 120-160 km/h
- Example: Portuguese Northern Line

**Standard (ERA Class C)**
- Max Weight: 6400-7200 kg/m
- Typical Max Speed: 80-120 km/h
- Example: Regional lines

**Light Infrastructure**
- Max Weight: less than 6400 kg/m
- Typical Max Speed: 60-80 km/h
- Example: Secondary branches

Sources:
- European Railway Agency (ERA) - Technical Specifications for Interoperability (TSI)
- Infraestruturas de Portugal (IP) - Line Characteristics Manual

### 4.4 Impact

This assumption directly affects the travel time calculation:
```
Effective Speed = MIN(locomotive.maxSpeed, line.minMaxSpeed)

Example:
  Locomotive Inês: 220 km/h (from CSV)
  Segment 1: 120 km/h (inferred from 8000 kg/m)
  Segment 2: 120 km/h (inferred from 8000 kg/m)
  
  Result: Effective Speed = MIN(220, 120) = 120 km/h
  Travel Time = 31.621 km / 120 km/h = 0.26 hours (approximately 16 minutes)
```

### 4.5 Code Location

File: src/main/java/domain/LineSegment.java

Lines: 56-58
```java
public int getMaxSpeedKmh() {
    return maxWeightKgPerM >= 8000 ? 120 : 100;
}
```

## 5. Additional Assumptions

### 5.1 Instantaneous Acceleration

**From:** Acceptance Criteria (explicit requirement)

**Assumption:** Train reaches maximum speed immediately (no acceleration phase)

**Formula:** time = distance / speed

**Impact:** Simplifies calculation; real-world times would be slightly longer

**Code:** TravelTimeCalculator.java:84

### 5.2 Constant Velocity

**Assumption:** Train maintains constant speed throughout the journey

**Justification:** Consequence of instantaneous acceleration assumption

**Impact:** No speed variations for curves, gradients, or weather conditions

**Code:** TravelTimeCalculator.java:81-84

### 5.3 Line Speed Equals Minimum of All Segments

**Assumption:** Line maximum speed is the most restrictive segment

**Formula:** line.maxSpeed = MIN(segment1.speed, segment2.speed, ...)

**Justification:** Operational safety - train cannot exceed any segment limit

**Example:**
```
Segment 1: 120 km/h
Segment 2: 100 km/h (most restrictive)
Segment 3: 120 km/h

Result: Line Max Speed = 100 km/h
```

**Code:** Line.java:46-50

### 5.4 Direct Connection Only

**From:** Acceptance Criteria ("with a direct connection between them")

**Assumption:** Only routes with a single line (no multi-hop connections)

**Example:**
```
Allowed: Porto São Bento to Porto Campanhã (1 line)
Not allowed: Porto to Valença (requires 7 lines)
```

**Impact:** User cannot calculate routes requiring transfers

**Code:** TravelTimeCalculator.java:53-58

### 5.5 No Intermediate Stops

**Assumption:** Continuous travel from origin to destination (no station stops)

**Justification:** Sprint 1 simplification (no timetables/schedules)

**Impact:** Real-world times would be 2-5 minutes longer per stop

**Code:** Implicit in calculation

## 6. Data Integration Strategy

### 6.1 Sprint 1 Approach: CSV Files

**Decision:** Use CSV files as the primary data source for Sprint 1.

**Rationale:**

**Time constraints:** Sprint 1 deadline (October 27) prioritizes functional implementation over architectural complexity

**Simplicity:** CSV parsing is straightforward and requires no external database dependencies

**Testability:** Static files enable deterministic unit testing without database mocking

**Compliance:** The non-functional requirements explicitly state "A significant part of the integration will be carried out through files" (Section 3.4)

**Implementation:**
- Railway infrastructure data (facilities, lines, segments) extracted from BDDAD dataset
- Locomotive specifications from provided sample data
- Data loaded at application startup via CSVReader utility class

**Files Structure:**
```
/data
  ├── facilities.csv       (stations and terminals)
  ├── segments.csv         (line segments with physical characteristics)
  ├── lines.csv           (railway lines connecting endpoints)
  └── locomotives.csv     (rolling stock specifications)
```

### 6.2 Future Evolution: Oracle Database Integration

**Target:** Sprint 2 and beyond

**Objective:** Migrate from file-based to database-centric architecture, aligning with the requirement that "The database will be the main repository of information for the system" (Section 3.4).

**Benefits:**

**Single source of truth:** Eliminate data duplication between BDDAD and LAPR3 components

**Real-time consistency:** Reflect database changes immediately without manual CSV updates

**Scalability:** Support larger datasets and concurrent access

**Professional architecture:** Industry-standard approach for enterprise systems


### 6.3 Justification for Phased Approach

This incremental migration strategy balances:

**Sprint 1 delivery:** Functional MVP within deadline constraints

**Technical debt management:** Planned refactoring prevents rushed implementations

**Risk mitigation:** CSV fallback ensures system stability during database integration

**Learning curve:** Team can master JDBC concepts progressively across sprints

**Alignment with Agile Principles:**
- Delivers working software in Sprint 1
- Embraces changing requirements (file to database)
- Maintains sustainable development pace