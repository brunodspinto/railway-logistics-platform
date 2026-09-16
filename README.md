# Railway Logistics Platform

A multi-module Java/Maven project that models an end-to-end railway logistics operation: warehouse handling, freight scheduling, spatial querying of a Europe-wide station dataset, network optimisation over the Belgian rail graph, and a station controller written in C and RISC-V assembly that drives a Raspberry Pi Pico W peripheral. Persistence and reporting are delivered through PL/SQL on Oracle.

## Features

### `warehouseMngt` — Cargo Handling at a Railway Terminal
- CSV ingestion for bays, items, orders, wagons and returns.
- Wagon unloading with a **global FEFO/FIFO sort** across all incoming boxes before distribution.
- Inventory dispatch honouring FEFO across bays and supporting partial dispatch (box replaced by a remainder box).
- Box relocation between bays with FEFO-aware insertion and capacity checks.
- Order allocation from stock.
- Picking planning via bin-packing heuristics (`FF`, `FFD`, `BFD`) that group items into trolleys.
- Pick-path sequencing with two strategies and a warehouse-specific distance metric (same aisle vs. cross-aisle penalty).
- Returns pipeline: inspection (`RESTOCK` / `DISCARD`), quarantine handling and audit logging to file.

### `freightMngt` — Freight & Train Operations
- Travel-time calculation between stations (USLP03).
- Train scheduling — automatic (`SchedulerService`) and manual (`ManualSchedulerUI`) — using an Oracle-backed repository (USLP07).
- Route planner producing a route manifest (USLP08).
- Train assembly service that composes locomotives and wagons into a train (USLP09).
- Traffic scheduler that detects conflicts and crossing operations on shared line segments (USLP10).
- Domain model: `Freight`, `Line`, `LineSegment`, `Locomotive`, `Wagon`, `WagonModel`, `Train`, `Station`, `Conflict`, `CrossingOperation`.

### `networkMngt` — Spatial Indexing of a European Station Dataset
- Loader for the bundled Europe-wide stations dataset (`res/train_stations_europe.csv`).
- Time-zone index with windowed queries (USEI06).
- Balanced **2D-tree (k-d tree)** built over station coordinates (USEI07).
- Geographical-area search on a bounding box (USEI08).
- Nearest-N proximity search (USEI09).
- Radius search plus density summaries backed by an **AVL tree** and composite keys (USEI10).

### `networkOptimization` — Graph Algorithms on the Belgian Rail Network
- `BelgianNetworkLoader` reads `res/stations.csv` and `res/lines.csv` into an in-house graph (`Graph` / `MapGraph`), with a Graphviz **DOT exporter** for visualisation.
- USEI11 — Directed line upgrade plan (topological sort over a DAG of upgrades).
- USEI12 — Minimal backbone network via MST with a Union-Find structure.
- USEI13 — Rail-hub centrality analysis.
- USEI14 — Maximum throughput between hubs using the **Edmonds–Karp** max-flow algorithm.
- USEI15 — Risk-aware shortest paths with the **Bellman–Ford** algorithm, including negative-cycle detection and a detailed cycle report.

### `stationMngt` — RISC-V Station Controller (ARQCP)
- Sprint 2: individual RISC-V assembly exercises (`USAC01`–`USAC09`) building up the low-level primitives.
- Sprint 3: integrated station controller built for **RV64IMAFDC** (`riscv64-linux-gnu-gcc/as`) and run on the host through **`qemu-riscv64-static`** (see `stationMngt/ARQCP/sprint3/Makefile/Makefile`). C core covers board, config loader, log manager, sensors manager, serial comm, track manager, light controller and UI; assembly routines `usac01.s`, `usac03.s`, `usac04.s` and `usac14.s` are linked in from `src/assembly/`.
- Physical rig — a Raspberry Pi Pico W with two track light groups (green/yellow/red) and a DHT11 temperature/humidity sensor — is connected as a serial peripheral and driven by a small `station_controller.ino` firmware; commands such as `GE,01` (green LED on track 1) and `GTH` (read temperature/humidity) are exchanged over USB serial. Wiring is documented in `stationMngt/ARQCP/sprint3/docs/HARDWARE_SETUP.md`.

### `sql-scripts` — Oracle PL/SQL
- Three sprints of scripts (`Sprint1`, `Sprint2`, `Sprint3`) containing:
  - `CREATE TABLE` DDL evolving with the domain.
  - `INSERT` DML from the sprint datasets.
  - One PL/SQL file per BDDAD user story (`USBD08` … `USBD45`) implementing the corresponding query or procedure.
- Logical relational model exported as PNG/SVG.

## Tech Stack

- **Java** — `warehouseMngt` and `freightMngt` target Java 17; `networkMngt` and `networkOptimization` target Java 23.
- **Maven** (multi-module) — root `pom.xml` aggregates the four Java modules.
- **JUnit 5**, **Mockito** — automated tests (`mvn test`).
- **Oracle Database** + **PL/SQL** — persistence and BDDAD queries.
- **C** + **RISC-V (RV64IMAFDC) assembly** — `stationMngt`, executed on the host under **QEMU**; the Raspberry Pi Pico W acts as a USB serial peripheral running a small Arduino-style sketch.
- **Graphviz DOT** — graph visualisation output from `networkOptimization`.

## Getting Started

### Requirements

- **JDK 23 or newer** (covers all four Java modules).
- **Maven 3.9+**.
- **Oracle Database** access (used by `freightMngt` through `DatabaseConnection`).
- For `stationMngt`: `make`, the RISC-V toolchain (`riscv64-linux-gnu-gcc`, `riscv64-linux-gnu-as`) and `qemu-riscv64-static`. To exercise the physical rig you also need the hardware described in `stationMngt/ARQCP/sprint3/docs/HARDWARE_SETUP.md`.

### Build

From the repository root:

```bash
mvn clean package
```

This builds all four Java modules and runs their tests.

### Run each module

Each Java module ships its own interactive menu. `compile exec:java` compiles the module first and then launches its `main`:

```bash
# Warehouse operations (USEI01–USEI05)
mvn -pl warehouseMngt compile exec:java -Dexec.mainClass=org.example.Main

# Freight & train scheduling (USLP03, USLP07–USLP10)
mvn -pl freightMngt   compile exec:java -Dexec.mainClass=org.example.MainGeral

# Spatial queries over the European stations dataset (USEI06–USEI10)
mvn -pl networkMngt   compile exec:java -Dexec.mainClass=org.example.Main

# Network optimisation menu (USEI11–USEI15) — Belgian rail network
mvn -pl networkOptimization compile exec:java -Dexec.mainClass=org.example.MainMenu
```

`freightMngt` needs an Oracle connection and stops at startup without one. Before running it, create the local configuration file (it is ignored by Git) and fill in `db.hostname`, `db.port`, `db.servicename`, `db.username` and `db.password`:

```bash
cp freightMngt/src/main/resources/database.properties.example freightMngt/src/main/resources/database.properties
```

For the station controller:

```bash
cd stationMngt/ARQCP/sprint3/Makefile
make            # build the station_app and USAC10–USAC16 tests
make run_app    # run station_app under qemu-riscv64-static
```

For the database layer, execute the SQL files in `sql-scripts/SprintX/` in order (`CREATE TABLEs …sql` → `INSERTs …sql` → the individual `USBDxx.sql` scripts) against an Oracle schema.

## Project Structure

```
.
├── warehouseMngt/         # Warehouse & returns (Java 17)
├── freightMngt/           # Freight scheduling & routing (Java 17)
├── networkMngt/           # Spatial indexing of stations (Java 23)
├── networkOptimization/   # Graph algorithms on the Belgian rail network (Java 23)
├── stationMngt/ARQCP/     # C + RISC-V assembly station controller (QEMU host + Pico W peripheral)
├── sql-scripts/           # PL/SQL DDL, DML and USBD queries (per sprint)
├── res/                   # Datasets (CSV) and shared resources
├── docs/                  # Sprint documentation and domain artefacts
└── pom.xml                # Maven aggregator
```

## Team

- Bruno Pinto
- David Ribeiro
- Diogo Azevedo
- Eduardo Oliveira
- Rafael Santos

## Academic Context

Developed as the Integrative Project (LAPR3) of the 2nd year, 1st semester of the Degree in Informatics Engineering at ISEP – Polytechnic of Porto, 2025/2026, integrating ARQCP, BDDAD, ESINF and FSIAP, across three sprints.
