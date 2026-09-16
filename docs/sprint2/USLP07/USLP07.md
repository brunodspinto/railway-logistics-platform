# USLP07 - Manual Train Scheduler

**Freight Management System - Sprint 2**  
**Version:** 1.0  
**Date:** November 2025

---

## 📖 Overview

The Manual Train Scheduler (USLP07) enables freight managers to manually define train routes and automatically detect/resolve conflicts on single-track railway segments. The system calculates realistic schedules, identifies temporal overlaps, and applies necessary delays to prevent train collisions.

---

## 🎯 Main Features

### 1. Manual Path Definition
- Station-by-station path input with real-time validation
- Direct connection verification between consecutive stations
- Distance and line information feedback

### 2. Automatic Conflict Detection
- Identifies temporal overlaps on single-track segments
- Compares all trains scheduled for the same date
- Detects conflicts in both opposite and same-direction travel

### 3. Intelligent Conflict Resolution
- Determines which train should wait (latest arrival)
- Calculates optimal waiting station
- Applies 5-minute safety margins
- Updates schedules with calculated delays

### 4. Realistic Schedule Calculation
- Travel time based on line limits and train capabilities
- Weight-to-power ratio considerations
- Freight operation stop times
- Dynamic schedule adjustments

---

## ⚙️ Assumptions & Design Decisions

### Travel Time Calculation
```
Effective Speed = MIN(Line Max Speed, Train Max Speed)

Weight/Power Penalty:
  If ratio > 0.15: Apply up to 30% speed reduction
  
Travel Time = Distance / Effective Speed
```

### Stop Duration Rules
```
Base Stop Time = 10 minutes
Freight Operation = +30 minutes per load/unload

Total = Base + (Operations × 30 min)
```

### Conflict Resolution Logic
- **Waiting Train**: Arrives LATER at conflict segment
- **Passing Train**: Arrives FIRST at conflict segment
- **Safety Margin**: 5 minutes after segment is cleared
- **Short Paths** (2 stations): Origin is waiting station

---

## 🚦 System Workflow
```
┌─────────────────────────┐
│  1. Input Train Data    │
│  - ID, Operator, Date   │
│  - Manual Path          │
│  - Locomotives          │
│  - Freights             │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│  2. Load Existing       │
│     Trains (same date)  │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│  3. Calculate Schedules │
│  - Travel times         │
│  - Stop durations       │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│  4. Detect Conflicts    │
│  - Single-track check   │
│  - Temporal overlap     │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│  5. Resolve Conflicts   │
│  - Calculate delays     │
│  - Update schedules     │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│  6. Display Results     │
│  - Schedule             │
│  - Freight ops          │
│  - Crossing ops         │
│  - Summary              │
└─────────────────────────┘
```

---

## 📊 Output Format

### Schedule Table
```
Station              Arr.   Dep.   Speed      Distance   Operation
--------------------------------------------------------------------
Viana do Castelo     13:18  13:32  0.0 km/h   0.0 km     ORIGIN
Caminha              13:44  13:54  120 km/h   23.0 km    STOP (10 min)
```

### Freight Operations
```
Caminha (ID: 21):
   LOAD Freight #2005 (4 wagons, 84.8 tons) to Leixões
   UNLOAD Freight #2006 (2 wagons, 67.5 tons) from Barcelos
```

### Crossing Operations
```
1. Train 7020 waits at Caminha for 14 min (until 13:32) 
   while Train 5421 passes
```

### Summary
```
Total distance:    23.0 km
Total duration:    0h 26m
Average speed:     120.0 km/h
Total weight:      135.0 tons
Total power:       5600 kW
```

---

## 🧪 Test Scenarios

### **Scenario 1: No Conflict (Clear Path)**

**Purpose**: Validate normal scheduling without conflicts

**Input:**
```
Train ID: 7010
Operator: PT509017800
Date: 03/10/2025
Time: 16:00
Path: 17, 21, DONE
Locomotives: 5621
Freights: 2006
```

**Expected Result:**
- ✅ Schedule calculated successfully
- ✅ No conflicts detected
- ✅ "No crossing operations required - clear path!"

---

### **Scenario 2: Conflict with Resolution (Overlap)**

**Purpose**: Demonstrate automatic conflict detection and resolution

**Input:**
```
Train ID: 7020
Operator: PT509017800
Date: 03/10/2025
Time: 13:18
Path: 21, 17, DONE
Locomotives: 5623
Freights: 2006
```

**Expected Result:**
```
CONFLICTS DETECTED: 1

Line: Ramal Viana - Caminha (single track)
  Train 5421: 13:15 -> 13:27
  Train 7020: 13:18 -> 13:30

Resolved 1 of 1 crossings

CROSSING OPERATIONS:
  Train 7020 waits at Caminha for 14 min (until 13:32)
  while Train 5421 passes
```

**Analysis:**
- Train 5421 enters segment at 13:15 (Viana → Caminha)
- Train 7020 enters segment at 13:18 (Caminha → Viana)
- **OVERLAP DETECTED**: 13:18 < 13:27
- **RESOLUTION**: Train 7020 waits (arrived later)
- **DELAY**: 14 minutes (13:18 → 13:32)
- **SAFETY**: 5 min margin after 13:27 clear time

---

### **Scenario 3: Multi-Station Path with Freights**

**Purpose**: Test complex routing with multiple freight operations

**Input:**
```
Train ID: 7050
Operator: PT509017800
Date: 06/10/2025
Time: 10:00
Path: 8, 12, 17, 21, DONE
Locomotives: 5621, 5623
Freights: 2005, 2006
```

**Expected Result:**
- ✅ Multi-segment route (4 stations, 3 line segments)
- ✅ Multiple freight operations:
    - LOAD at Barcelos (2 freights)
    - UNLOAD at Caminha (1 freight)
- ✅ Extended stop times (40+ minutes at Barcelos)
- ✅ No conflicts (different date)
- ✅ Increased total power (2 locomotives = 11,200 kW)

---

## 🔧 Technical Implementation

### Key Classes
- **Train**: Main entity (path, locomotives, freights)
- **TrainSchedule**: Timeline with entries per station
- **ScheduleEntry**: Station timing (arrival, departure, speed)
- **Conflict**: Temporal overlap on single-track segment
- **CrossingOperation**: Resolved conflict with delay details

### Core Algorithms

**1. Effective Speed Calculation**
```java
double baseSpeed = Math.min(lineMaxSpeed, trainMaxSpeed);
double weightPowerRatio = totalWeight / totalPower;

if (weightPowerRatio > 0.15) {
    double penalty = Math.min(0.3, (weightPowerRatio - 0.15) * 2);
    baseSpeed *= (1 - penalty);
}
```

**2. Overlap Detection**
```java
boolean hasOverlap = (start1 < end2) && (start2 < end1);
```

**3. Delay Calculation**
```java
LocalDateTime safeDeparture = passingClearTime.plusMinutes(5);
long delay = Duration.between(waitingArrival, safeDeparture).toMinutes();
```

---

## 🚨 Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| "No direct connection found" | Missing intermediate station | Add all stations in path |
| "No waiting station found" | Cannot locate station before conflict | Verify path includes pre-conflict station |
| "Conflict detected but not resolved" | Delay ≤ 0 minutes | Trains may not actually overlap |
| "Station not found" | Invalid station ID | Use 'LIST' command to see valid IDs |

---

## 📈 Performance

- **Conflict Detection**: O(n² × m) where n = trains, m = segments
- **Schedule Calculation**: O(k) where k = path length
- **Path Validation**: O(k) where k = number of stations

---

## ✅ Validation Criteria

The system successfully:
- ✅ Validates manual paths with connection checking
- ✅ Calculates realistic travel times (physics-based)
- ✅ Detects single-track conflicts with temporal analysis
- ✅ Resolves conflicts with minimal delay strategy
- ✅ Updates schedules dynamically
- ✅ Provides clear operational output

---

## 📚 References

- Project Assignment: `sem3_pi_2025_26_en_2_21.pdf`
- Section: 3.4.2 Sprint 2 - USLP07
- Related Requirements: USLP03 (travel time), USLP05 (domain model)

---

**Authors**: Railway Logistics Platform team  
**Course**: LAPR3/ESINF/BDDAD/ARQCP Integration Project  
**Academic Year**: 2025/2026