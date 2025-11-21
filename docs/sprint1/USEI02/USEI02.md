# USEI02 — Order Eligibility & Allocation

## 1. User Story

**As a** warehouse planner,  
**When I** receive open orders,  
**I want** the system to examine current inventory and allocate quantities from boxes in FEFO order, and produce per-line statuses (ELIGIBLE, PARTIAL, UNDISPATCHABLE) and a list of allocation rows with box and bay information.

---

## 2. Context

A warehouse planner must fulfill the orders it receives. To achieve this, they need to verify from the inventory which order lines can be dispatched and, for those, allocate concrete quantities from boxes using FEFO rules.

The outcome is:
- A list of **per-line statuses** indicating whether each order line can be fulfilled
- A set of **concrete allocation rows** (per order, line, box) for dispatch planning

This process is critical for:
- **Inventory accuracy**: Knowing exactly which boxes will be used
- **Order fulfillment planning**: Understanding what can and cannot be dispatched
- **Customer communication**: Identifying partial or unfulfillable orders early
- **FEFO compliance**: Ensuring products are allocated in the correct expiry order

---

## 3. Acceptance Criteria

### 3.1 Order Processing Sequence

Orders are processed in the following priority order:
1. **priority ASC** (lower number = higher priority)
2. **dueDate ASC** (earliest due date first)
3. **orderId ASC** (tie-breaker)

Within an order, lines are processed by **lineNo ASC** (input order).

### 3.2 Allocation Algorithm

For each line:
1. Allocation walks the SKU's boxes in **FEFO/FIFO order** (as defined in USEI01):
    - Boxes with earliest expiryDate first (null last)
    - Then by receivedAt (oldest first)
    - Then by boxId ASC (tie-break)

2. For each visited bay, allocate:
   ```
   take = min(remainingQty, box.qtyAvailable)
   ```
    - Reduce `remainingQty`
    - Continue until the request is satisfied or boxes end

3. The available box quantity **never goes below zero** during planning

### 3.3 Processing Modes

The system supports two modes via flag: **strict** (default) or **partial**

#### Strict Mode (default):
- A line is **ELIGIBLE** only if its entire requested quantity is allocated
- Otherwise it is **UNDISPATCHABLE**
- No allocations for UNDISPATCHABLE lines are kept

#### Partial Mode:
- If `0 < allocated < requested`: mark the line **PARTIAL** and keep the allocated portion
- If `allocated = 0`: mark **UNDISPATCHABLE**
- If `allocated = requested`: mark **ELIGIBLE**

### 3.4 Output Requirements

The system must return two outputs:

#### Output 1: Orders Eligibility List
For each order line:
- `orderId`: Order identifier
- `lineNo`: Line number within the order
- `sku`: Product SKU
- `requestedQty`: Quantity requested
- `allocatedQty`: Quantity successfully allocated
- `status`: ELIGIBLE | PARTIAL | UNDISPATCHABLE

#### Output 2: Allocation Details
For each allocation fragment (one row per box used):
- `orderId`: Order identifier
- `lineNo`: Line number
- `sku`: Product SKU
- `qty`: Quantity allocated from this box
- `boxId`: Box identifier
- `aisle`: Aisle location
- `bay`: Bay location

---

## 4. Data Sources

### Input Files:

#### `orders.csv`
Contains order header information:
- `orderId`: Unique order identifier
- `dueDate`: Date by which order must be fulfilled (ISO format)
- `priority`: Priority level (lower number = higher priority)

**Example:**
```csv
orderId,dueDate,priority
ORD001,2025-10-25,1
ORD002,2025-10-26,2
ORD003,2025-10-25,1
```

#### `order_lines.csv`
Contains order line details:
- `orderId`: Reference to order
- `lineNo`: Line number within order (sequence)
- `sku`: Product SKU requested
- `qty`: Quantity requested

**Example:**
```csv
orderId,lineNo,sku,qty
ORD001,1,SKU123,50
ORD001,2,SKU456,30
ORD002,1,SKU123,20
ORD003,1,SKU789,100
```

### Required Data from Previous US:

This US depends on:
- **USEI01**: Current inventory state (boxes in bays with FEFO/FIFO ordering)
- **items.csv**: Product information for SKU validation
- **bays.csv**: Bay locations

---

## 5. Validation Rules

### 5.1 Orders Validation

During import of `orders.csv`:
1. **Valid orderId**: Must be unique and non-empty
2. **Valid dueDate**: Must be a valid date in ISO format (YYYY-MM-DD)
3. **Valid priority**: Must be a positive integer (1, 2, 3, ...)
4. **Future dates**: dueDate should typically be in the future or today (warning if past)

### 5.2 Order Lines Validation

During import of `order_lines.csv`:
1. **Valid orderId**: Must reference an existing order in `orders.csv`
2. **Valid lineNo**: Must be a positive integer, unique within the order
3. **Valid SKU**: Must exist in the items catalog (`items.csv`)
4. **Valid qty**: Must be a positive integer (> 0)
5. **Duplicate lines**: Same orderId + lineNo should not appear twice

### 5.3 Processing Validation

During allocation:
1. **Inventory existence**: Verify SKU exists in current inventory
2. **Available quantity**: Check that boxes have available quantity > 0
3. **Box integrity**: Ensure box quantities haven't been corrupted
4. **Bay references**: Verify all boxes have valid aisle/bay locations

### Error Handling:

#### Import Errors:
- **Invalid order reference**:
  ```
  Error: Order line references non-existent order
  Line: orderId=ORD999, lineNo=1
  ```

- **Unknown SKU**:
  ```
  Error: SKU not found in items catalog
  Order: ORD001, Line: 2, SKU: UNKNOWN123
  ```

- **Invalid quantity**:
  ```
  Error: Quantity must be positive
  Order: ORD002, Line: 1, qty: -5
  ```

- **Duplicate line**:
  ```
  Error: Duplicate order line detected
  Order: ORD001, Line: 1 appears multiple times
  ```

#### Processing Errors:
- **SKU not in inventory**:
  ```
  Warning: SKU requested but not available in inventory
  Order: ORD001, Line: 2, SKU: SKU456
  Status: UNDISPATCHABLE
  ```

- **Insufficient stock**:
  ```
  Info: Partial allocation - insufficient inventory
  Order: ORD001, Line: 1, Requested: 100, Available: 75
  Status: PARTIAL (if partial mode enabled)
  ```

#### Mode-Specific Handling:

**Strict Mode:**
- If any line cannot be fully allocated, mark UNDISPATCHABLE
- Discard all allocations for that line
- Continue processing remaining lines

**Partial Mode:**
- Keep partial allocations
- Mark line as PARTIAL
- Include allocated quantity in output

#### General Error Principles:
- **Fail gracefully**: Don't stop processing all orders if one fails
- **Clear messages**: Identify exact order, line, and issue
- **Audit trail**: Log all allocation decisions
- **Data consistency**: Never leave inventory in inconsistent state
- **Rollback capability**: If needed, be able to undo allocations