# USEI05 — Returns & Quarantine

## 1. User Story

**As a** quality operator,  
**I want** returned goods to be placed in quarantine so that I can inspect them in the reverse order they arrived, latest first (descending by timestamp, ties by returnId ASC), process them, either discard or restock them, and create an audit file detailing the processing done to each product.

---

## 2. Context

In a warehouse, returned products cannot immediately re-enter inventory. They must first be placed into quarantine to ensure quality and safety. This step prevents damaged or expired goods from being mixed with usable stock.

### Key Principles:

- **Quarantine First**: All returned items must go through quarantine before any decision is made about their fate

- **Inspection Order**: Quarantined items are inspected in the **reverse order they arrived** (latest first), because these are usually the most urgent to process

- **Two Outcomes**: After inspection, items may either be:
    - **Discarded** if they are unsuitable for resale
    - **Restocked** safely back into inventory

- **Accountability**: Every inspection action must be written into a log file with relevant details to ensure traceability and compliance

### Reasons for Returns:

1. **Customer remorse**: The customer returned the item even though it is still in good condition (e.g., unwanted gift, wrong choice). These items can often be restocked.

2. **Damaged**: The product is physically broken or unusable. These items must be discarded.

3. **Expired**: The product has passed its expiry date. These items cannot be sold and must be discarded.

4. **Cycle count**: A mismatch was found during a stock audit. These items are not faulty but must be rechecked before restocking.

---

## 3. Acceptance Criteria

### 3.1 Quarantine Process

- Handle customer returns by placing them in quarantine first
- Returns must not immediately enter active inventory

### 3.2 Inspection Order

- Inspect each return in **reverse chronological order**:
    1. Sort by **timestamp DESC** (latest first)
    2. Ties broken by **returnId ASC**

### 3.3 Inspection Decisions

For each inspected return, the operator must decide:

#### Discard:
- Mark the item as no longer usable
- Remove from quarantine
- Do not add back to inventory
- Record action in audit log

#### Restock:
- Create a new box with the following attributes:
    - `boxId` = "RET-" + `returnId` (e.g., "RET-R105")
    - `receivedAt` = current timestamp (now())
    - `expiryDate` = as provided in the return, or null if unknown
    - `SKU` = original product SKU
    - `qty` = quantity being restocked
- Insert into appropriate bay using **standard FEFO/FIFO insertion rules** (as defined in USEI01)
- This ensures safe reintegration of good items back into the warehouse
- Record action in audit log

### 3.4 Audit Log

Each inspection must write a line to an external audit log file with the following format:

```
timestamp | returnId=... | sku=... | action=Restocked|Discarded | qty=...
```

**Format specifications:**
- Timestamp in readable format (e.g., `2025-09-22 14:32`)
- Fields separated by ` | ` (space-pipe-space)
- Action must be either "Restocked" or "Discarded"
- If partially restocked, include both `qtyRestocked` and `qtyDiscarded`

**Examples:**
```
2025-09-22 14:32 | returnId=R105 | sku=SKU123 | action=Restocked | qty=8
2025-09-22 14:40 | returnId=R106 | sku=SKU200 | action=Discarded | qty=5
2025-09-22 15:15 | returnId=R107 | sku=SKU456 | action=Restocked | qty=3 | qtyDiscarded=2
```

This log provides a clear history of all inspections and ensures that decisions can be reviewed later for auditing or troubleshooting purposes.

---

## 4. Data Sources

### Input File:

#### `returns.csv`
Contains return information with the following structure:
- `returnId`: Unique identifier for the return
- `SKU`: Stock Keeping Unit identifier
- `qty`: Quantity of items returned
- `reason`: Reason for return (customer remorse, damaged, expired, cycle count)
- `timestamp`: When the return was received (ISO format)
- `expiryDate`: Expiry date if known (optional, may be null)

**Example:**
```csv
returnId,SKU,qty,reason,timestamp,expiryDate
R101,SKU123,10,customer remorse,2025-10-20T10:30:00,2025-12-31
R102,SKU456,5,damaged,2025-10-20T11:45:00,
R103,SKU789,8,expired,2025-10-20T14:20:00,2025-10-15
R104,SKU123,3,cycle count,2025-10-20T16:00:00,2025-12-31
```

---

## 5. Validation Rules

During import of `returns.csv`, the system must validate:

1. **Valid returnId**: Must be unique and non-empty
2. **Valid SKU**: Must exist in the items catalog (cross-reference with `items.csv`)
3. **Valid quantity**: Must be positive integer
4. **Valid reason**: Must be one of: "customer remorse", "damaged", "expired", "cycle count"
5. **Valid timestamp**: Must be in ISO format and not in the future
6. **Valid expiryDate**: If provided, must be a valid date

### Error Handling:
- Invalid records should be rejected with clear error messages
- Valid records should still be processed
- Error messages should identify the specific problem and record
- Example: "Return R105: SKU 'XYZ999' not found in items catalog"

---
