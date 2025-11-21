# USEI03 — Picking Plans

## 1. User Story

**As a** planner,  
**I want** to pack the allocation rows produced by USEI02 into capacity-bounded trolleys, choosing one of the packing heuristics,  
**So that** pickers can complete runs without overloading trolleys.

---

## 2. Context

To satisfy the list of orders elaborated in USEI02, it is required to collect products from one or more points in the warehouse – a process named **"picking"**.

Pickers often use a **trolley (or cart)** to collect items, but all trolleys have a **weight (kg) capacity** that must be respected. Therefore, it is necessary to have a pick plan that dispatches orders, or parts of orders, that fit within the capacity of the trolley, so pickers can safely transport without exceeding the trolley's capacity.

### Key Concepts:

- **Trolley**: A cart used by pickers with a maximum weight capacity
- **Picking Plan**: An organized list of items to be collected in a single trolley run
- **Packing Heuristics**: Algorithms to efficiently distribute items across multiple trolleys
- **Weight Capacity**: Maximum weight (kg) a trolley can carry safely

### Importance:

- **Safety**: Prevents overloading trolleys
- **Efficiency**: Minimizes number of trolley trips
- **Optimization**: Better space/weight utilization
- **Planning**: Clear instructions for warehouse pickers

---

## 3. Acceptance Criteria

### 3.1 Trolley Capacity

- **No limit** on the number of trolleys that can be used
- **Capacity** (in kg) must be defined by the planner
- Each trolley's capacity must be respected (cannot exceed)
- Trolleys should be filled as efficiently as possible

### 3.2 Packing Heuristics

The warehouse supports three packing heuristics:

#### Heuristic 1: First Fit (FF)
- Place the item in the **first available trolley** where it fits
- Scan order: **input order** of the allocation rows (from USEI02)
- Simple and fast approach
- May not be the most space-efficient

#### Heuristic 2: First Fit Decreasing (FFD)
- **Sort** unpicked allocations from USEI02 by **weight** (largest to smallest)
- Place the item in the **first available trolley** where it fits
- Better space utilization than FF
- Heavy items placed first

#### Heuristic 3: Best Fit Decreasing (BFD)
- **Sort** all unpicked allocations from USEI02 by **weight** (largest to smallest)
- Put the item into the trolley that will result in the **smallest amount of remaining unused capacity**
- This is the "tightest fit" approach
- Generally the most space-efficient
- More complex computation

### 3.3 Handling Items That Don't Fit

If a single order line does not fit in the remaining capacity of the current trolley, either:

#### Option A: Split It
- Put part of the quantity in the current trolley
- Log a **"partial allocation"**
- Carry the remainder to the next trolley

#### Option B: Defer It
- Log **"skipped due to capacity"**
- Put the full line in the next trolley

The planner should choose which approach to use.

### 3.4 Output Requirements

For **each heuristic**, the system must display:

#### Summary Information:
1. **Total number of trolleys** necessary to dispatch the orders
2. **Utilization of each trolley**: `usedWeight/capacityWeight` (e.g., 85% full)

#### Detailed Picking Plan:
For each trolley, provide the picking plan with all:
- `orderId`: Order identifier
- `lineNo`: Line number
- `aisle`: Aisle location
- `bay`: Bay number
- `boxId`: Box identifier
- `SKU`: Product SKU
- `quantity`: Quantity to pick from this box

---

## 4. Data Sources

### Input Data:

#### From USEI02 - Allocation Rows:
The allocation output from USEI02 provides:
- `orderId`: Order identifier
- `lineNo`: Line number
- `sku`: Product SKU
- `qty`: Quantity allocated
- `boxId`: Box identifier
- `aisle`: Aisle location
- `bay`: Bay location

#### From items.csv:
Product weight information is needed:
- `SKU`: Product identifier
- `unitWeight`: Weight per unit (kg)

**Calculation**:
```
itemWeight = qty × unitWeight
```

### Required Data from Previous US:

This US depends on:
- **USEI02**: Allocation rows (what to pick and from where)
- **items.csv**: Unit weight for calculating total weight per allocation

---

## 5. Validation Rules

### 5.1 Trolley Capacity Validation

1. **Valid capacity**: Must be a positive number (kg)
2. **Realistic capacity**: Should be reasonable for a trolley (e.g., 50-500 kg)
3. **Minimum capacity**: Should be at least able to hold the smallest item

### 5.2 Weight Calculation Validation

1. **Valid unitWeight**: Each SKU must have a valid unitWeight in items.csv
2. **Positive weights**: All weights must be positive
3. **Calculation accuracy**: `totalWeight = qty × unitWeight`
4. **Overflow check**: Ensure weight calculations don't overflow

### 5.3 Allocation Validation

Before packing:
1. **Complete allocations**: Verify all allocations from USEI02 are present
2. **Valid quantities**: All quantities must be positive
3. **Valid locations**: All aisle/bay references must be valid
4. **SKU consistency**: SKUs must match between allocations and items catalog

### 5.4 Packing Validation

During packing process:
1. **Capacity not exceeded**: Each trolley's used weight ≤ capacity
2. **All items packed**: Every allocation must be assigned to a trolley
3. **Order integrity**: Items from same order line should ideally stay together
4. **No lost items**: Total quantity in = total quantity out

### Error Handling:

#### Input Validation Errors:

- **Missing weight information**:
  ```
  Error: Unit weight not found for SKU
  SKU: SKU123 - cannot calculate total weight
  Solution: Check items.csv for missing unitWeight values
  ```

- **Invalid trolley capacity**:
  ```
  Error: Trolley capacity must be positive
  Provided: 0 kg or negative value
  ```

- **Item too heavy**:
  ```
  Error: Single item exceeds trolley capacity
  Item: orderId=ORD001, line=1, weight=150kg
  Trolley capacity: 100kg
  Solution: Increase trolley capacity or split the item quantity
  ```

#### Packing Errors:

- **Capacity exceeded** (should never happen with correct algorithm):
  ```
  Error: Trolley capacity exceeded - algorithm error
  Trolley #3: used=105kg, capacity=100kg
  ```

- **Incomplete packing**:
  ```
  Error: Not all items were packed
  Remaining items: 5 allocations
  Total weight unpacked: 25kg
  ```

- **Zero-weight items**:
  ```
  Warning: Item has zero weight
  SKU: SKU456, orderId: ORD002, line: 2
  Action: Item packed but doesn't consume capacity
  ```

#### Heuristic-Specific Handling:

**First Fit (FF):**
- Process allocations in input order
- If item doesn't fit in any existing trolley, create new trolley
- Simple error handling - just track if item was placed

**First Fit Decreasing (FFD):**
- Sort by weight (descending) before processing
- Handle sorting of equal-weight items consistently (by orderId, lineNo)
- Same placement logic as FF

**Best Fit Decreasing (BFD):**
- Sort by weight (descending) before processing
- For each item, scan ALL trolleys to find best fit
- Handle ties (multiple trolleys with same remaining capacity) consistently
- More complex but most efficient

#### Split vs Defer Handling:

**If Split Strategy:**
```
Info: Order line split across trolleys
Order: ORD001, Line: 1, SKU: SKU123
Trolley #1: 30 units (45kg)
Trolley #2: 20 units (30kg)
Total: 50 units as requested
```

**If Defer Strategy:**
```
Info: Order line deferred to next trolley
Order: ORD002, Line: 3, SKU: SKU456
Reason: Doesn't fit in current trolley (remaining capacity: 5kg, needed: 15kg)
Moved to: Trolley #4
```

#### General Error Principles:

- **Deterministic results**: Same input should always produce same output
- **Complete reporting**: Show all trolleys with their contents and utilization
- **Clear metrics**: Display efficiency metrics (utilization %, number of trolleys)
- **Audit trail**: Log all packing decisions for review
- **Comparison**: Allow comparing results of different heuristics side-by-side