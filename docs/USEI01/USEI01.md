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

## 3. Acceptance Criteria

### 3.1 Stock Initialization/Unloading Wagons

The file `wagons.csv` contains the available stock per SKU. Each wagon's contents should be assigned to an aisle/bay, and their products must be inserted into the correct position inside the bay according to the following rules:

**Sorting Order:**
1. **Expiry date** (earliest first; null last)
2. **receivedAt** (oldest first)
3. **boxId ASC** (tie-break)

### 3.2 Dispatch Operation

The operation `dispatch` must:
- Always consume stock from the **"front"** of the bay list, guaranteeing FEFO/FIFO behaviour
- If a bay becomes empty, it remains in the WMS (the bay still exists), but the box list is empty
- **Only delete empty boxes, not bays**
- Support **partial dispatch across multiple bays**: if the target bay runs out, continue in the next bay (ascending number) that holds that SKU

### 3.3 Relocation

The operation `relocation` must:
- Update a box's `warehouseId`/`aisle`/`bay` only
- Do not re-sort its new bay if its `expiryDate`/`receivedAt` is unchanged
- Insert it into the new bay's FEFO position

---

## 4. Data Sources

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

## 5. Validation Rules

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
