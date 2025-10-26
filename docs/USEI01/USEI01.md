# USEI01 — Wagons Unloading (Inventory Replenishment)

## 1. User Story

**As a** terminal operator,  
**I want** unloading operations of wagons to automatically store inventory using FEFO and/or FIFO logic,  
**So that** I can ensure the correct dispatch order and minimize product spoilage.

---

## 2. Context

When wagons arrive at the terminal and are unloaded into warehouses, the sequence in which products are stored and dispatched is critical for maintaining inventory accuracy, traceability, and operational efficiency.

### Key Principles:

- **Perishable goods**: Must be handled strictly according to their expiry dates, following the **First-Expired-First-Out (FEFO)** principle. This ensures that goods closest to expiration are dispatched first, minimizing spoilage and waste.

- **Non-perishable goods**: Without expiry dates, should be managed under a **First-In-First-Out (FIFO)** discipline. This guarantees a fair rotation of stocks and prevents inventory stagnation.

- **Traceability**: Each item with a unique SKU is stored in boxes, each with a unique location code (Aisle/Bay) to facilitate picking and restocking. Every movement of goods is meticulously recorded in the warehouse management system (WMS).

---

## 3. Bay Allocation Strategy  ← 🆕 NOVA SECÇÃO AQUI

### 3.1 Allocation Goals
The allocation strategy aims to:
- **SKU Consolidation**: Keep same SKU products together for efficient picking
- **Load Balancing**: Distribute boxes across aisles to enable parallel operations
- **Deterministic Behavior**: Same input produces same allocation

### 3.2 Allocation Algorithm

The system uses a **two-phase allocation strategy**:

#### Phase 1: SKU Consolidation (Priority)
When a box arrives, the system first attempts to place it in a bay that **already contains the same SKU** and has available capacity.

**Benefits:**
- Reduces picking time (all units of same SKU in fewer locations)
- Simplifies inventory management
- Minimizes bay fragmentation

**Selection Logic:**
```
1. Find all bays containing the SKU with available space
2. Select the bay with the MOST available capacity
3. Place box using FEFO/FIFO insertion
```

#### Phase 2: Round-Robin Distribution (Fallback)
If no bay contains the SKU (or all are full), the system uses **Round-Robin across aisles** to balance load:

**Benefits:**
- Enables parallel picking operations (multiple pickers in different aisles)
- Prevents aisle bottlenecks
- Distributes wear across warehouse infrastructure

**Selection Logic:**
```
1. Group available bays by aisle number
2. Select aisle using Round-Robin counter (rotates: A1 → A2 → A3 → A1...)
3. Within selected aisle, use first available bay (ascending bay number)
4. Increment Round-Robin counter for next allocation
```

### 3.3 FEFO/FIFO Preservation
**Critical:** Regardless of which bay is selected, boxes are **always inserted in FEFO/FIFO order within the bay**:
- Perishable: Earliest expiry first
- Non-perishable: Oldest receivedAt first
- Tie-break: boxId ascending

This ensures that **dispatch operations maintain FEFO/FIFO globally** by processing bays in order.


## 4. Acceptance Criteria

### 4.1 Stock Initialization/Unloading Wagons

The file `wagons.csv` contains the available stock per SKU. Each wagon's contents should be assigned to an aisle/bay, and their products must be inserted into the correct position inside the bay according to the following rules:

**Sorting Order:**
1. **Expiry date** (earliest first; null last)
2. **receivedAt** (oldest first)
3. **boxId ASC** (tie-break)

### 4.2 Dispatch Operation

The operation `dispatch` must:
- Always consume stock from the **"front"** of the bay list, guaranteeing FEFO/FIFO behaviour
- If a bay becomes empty, it remains in the WMS (the bay still exists), but the box list is empty
- **Only delete empty boxes, not bays**
- Support **partial dispatch across multiple bays**: if the target bay runs out, continue in the next bay (ascending number) that holds that SKU

### 4.3 Relocation

The operation `relocation` must:
- Update a box's `warehouseId`/`aisle`/`bay` only
- Do not re-sort its new bay if its `expiryDate`/`receivedAt` is unchanged
- Insert it into the new bay's FEFO position

---

## 5. Data Sources

### Input Files:

#### `wagons.csv`
Contains wagon inventory data with the following structure:
- `wagonId`: Unique identifier for the wagon
- `boxId`: Unique identifier for each box (must be unique in the warehouse)
- `SKU`: Stock Keeping Unit identifier
- `qty`: Quantity of items in the box
- `expiryDate`: Expiry date for perishable products (optional)
- `receivedAt`: ISO timestamp of when the wagon was received (mandatory)

#### `bays.csv`
Contains warehouse bay capacity information:
- `warehouseId`: Warehouse identifier
- `aisle`: Aisle number
- `bay`: Bay number
- `capacityBoxes`: Maximum number of boxes that can be stored

#### `items.csv`
Contains product information:
- `SKU`: Stock Keeping Unit identifier
- `name`: Product name
- `category`: Product category
- `unit`: Unit of measurement
- `volume`: Volume per unit
- `unitWeight`: Weight per unit

---

## 6. Validation Rules

During import, the system must validate:

1. **Unknown SKUs**: Detection of wagons not linked to any known product in `items.csv`
2. **Negative quantities**: Rejection of negative or invalid quantities
3. **Invalid dates**: Rejection of missing or invalid dates/timestamps
4. **Unique boxId**: Ensure that the boxId is unique in the warehouse
5. **Mandatory receivedAt**: If receivedAt is missing, reject the record

### Error Handling:
- The system should **reject invalid records** and report the problem clearly
- **Valid records should still be loaded**
- Error messages must be **clear and deterministic**, identifying the exact problem (e.g., "SKU 'XYZ123' in wagon 'W001' not found in items catalog")

---
