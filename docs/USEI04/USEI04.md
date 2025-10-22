# USEI04 — Pick Path Sequencing

## 1. User Story

**As a** picker,  
**I want** to plan the task of picking up several boxes, sequenced using a 2D layout (aisles & bays),  
**So that** I minimize walking distance starting from the entrance at the main corridor, with no return leg.

---

## 2. Context

The warehouses are organized in several **aisles** that are open spaces between rows of bays, which provide pathways to move goods and access **bays** — areas for storing products.

### Warehouse Layout:

- **Main corridor**: Located at coordinate (0, 0) - the entrance
- **Aisles**: Vertical pathways, numbered 0, 1, 2, 3, ...
- **Bays**: Storage locations within aisles, numbered by distance from main corridor
- **Bay location**: Represented as coordinate pair `(aisle, bay)`
    - Example: `(2, 5)` means aisle 2, bay 5

### Distance Model:

- **Horizontal spacing** between adjacent aisles = **3 units**
- Pickers start at **(0, 0)** on the main corridor
- Must visit all required bays to collect boxes
- **No return leg** - one-way trip (doesn't return to entrance)

### Geometry & Distance Rules:

The distance function **D** to go from coordinate `(a1, b1)` to coordinate `(a2, b2)`:

#### Same aisle (a1 == a2):
```
D = |b1 - b2|
```
Simply the vertical distance between bays.

#### Different aisles (a1 ≠ a2):
```
D = b1 + |a1 - a2| × 3 + b2
```
Must go:
1. Down from b1 to the main corridor
2. Move horizontally along corridor (×3 per aisle)
3. Go up to b2

### Total Route Distance:

Let the sequence of coordinates of the n bays to pick be: `c1, c2, ..., cn`  
(each `ci = (ai, bi)`, where `ai` is an aisle and `bi` is a bay)

Starting point: `c0 = (0, 0)`

**Total distance formula:**
```
D_Total = Σ D(ci, ci+1)  for i = 0 to n-1
```

---

## 3. Acceptance Criteria

### 3.1 Input Processing

- **Input**: Set of bays from the picking plan generated in USEI03
- **Duplicate handling**: Duplicate bays must be **merged into a single stop** (sum quantities) before sequencing
- **Reason**: Otherwise the same bay could appear multiple times in the path, which is inefficient

### 3.2 Sequencing Strategies

The system must implement **both strategies** and execute them on the same picking plan:

#### Strategy A — Deterministic Sweep (Ascending by Aisle)

**Algorithm:**
1. Sort the bays **ascending by aisle number**
2. Calculate the distance to pick the boxes in that sorted order (using distance function D)

**Characteristics:**
- Simple and predictable
- Visits aisles in order: 0, 1, 2, 3, ...
- Within same aisle, maintains bay order from input
- Easy to understand and implement

#### Strategy B — Nearest-Neighbour (Greedy)

**Algorithm:**
1. Start at entrance (0, 0)
2. Find the **nearest unvisited bay** using distance function D
3. Move to that bay
4. Repeat step 2 from current position until all bays visited
5. Calculate total distance

**Characteristics:**
- Greedy approach - locally optimal choices
- May produce different total distance than Strategy A
- More complex to implement
- Can be more or less efficient depending on bay distribution

### 3.3 Output Requirements

For **each strategy** (A and B), return:

1. **Path followed**: Ordered list of coordinates (aisle, bay) in visit sequence
2. **Total distance**: Sum of all distances traveled (using formula D_Total)
3. **Distance breakdown** (optional): Distance for each leg of the journey

### 3.4 Comparison

The system should allow comparison between both strategies to determine which produces shorter total distance for a given picking plan.

---

## 4. Data Sources

### Input Data:

#### From USEI03 - Picking Plan:
The picking plan provides:
- `orderId`: Order identifier
- `lineNo`: Line number
- `aisle`: Aisle location
- `bay`: Bay number
- `boxId`: Box identifier
- `SKU`: Product SKU
- `quantity`: Quantity to pick

### Processing:

1. **Extract unique bay locations**: `(aisle, bay)` pairs
2. **Merge duplicates**: If same bay appears multiple times, sum quantities
3. **Create bay list**: Set of unique coordinates to visit

**Example:**
```
Input from USEI03:
- Order 1, Line 1: aisle=1, bay=8, qty=5
- Order 1, Line 2: aisle=2, bay=2, qty=3
- Order 2, Line 1: aisle=1, bay=8, qty=2  ← duplicate bay!
- Order 3, Line 1: aisle=3, bay=4, qty=7

After merging:
- (1, 8): total qty = 7  (5 + 2)
- (2, 2): total qty = 3
- (3, 4): total qty = 7
```

### Required Data from Previous US:

This US depends on:
- **USEI03**: Picking plan with bay locations

---

## 5. Validation Rules

### 5.1 Coordinate Validation

1. **Valid aisle**: Must be non-negative integer (≥ 0)
2. **Valid bay**: Must be non-negative integer (≥ 0)
3. **Realistic values**: Aisles and bays should be within warehouse bounds
4. **Entrance**: Coordinate (0, 0) is reserved for the entrance (not a storage bay)

### 5.2 Bay List Validation

1. **Non-empty**: Must have at least one bay to visit
2. **Unique after merge**: No duplicate bays in final list
3. **Valid coordinates**: All (aisle, bay) pairs must be valid locations
4. **Quantity > 0**: After merging, total quantity per bay must be positive

### 5.3 Distance Calculation Validation

1. **Non-negative distances**: All distance calculations must produce ≥ 0
2. **Consistent formula**: Use the same distance function D for all calculations
3. **Integer arithmetic**: Ensure no floating-point errors in distance calculations
4. **Overflow check**: Total distance shouldn't overflow integer limits

### 5.4 Path Validation

1. **All bays visited**: Every bay in input must appear exactly once in output path
2. **Valid sequence**: Path must be a valid permutation of bay locations
3. **Start from entrance**: Path calculation must start from (0, 0)
4. **Complete path**: No missing bays in the sequence

### Error Handling:

#### Input Validation Errors:

- **Empty bay list**:
  ```
  Error: No bays to visit
  Picking plan contains no valid bay locations
  ```

- **Invalid coordinates**:
  ```
  Error: Invalid bay coordinate
  Bay: aisle=-1, bay=5
  Aisles and bays must be non-negative integers
  ```

- **Entrance in bay list**:
  ```
  Warning: Coordinate (0,0) is the entrance, not a pickable bay
  This coordinate will be skipped
  ```

#### Distance Calculation Errors:

- **Formula mismatch**:
  ```
  Error: Inconsistent distance calculation
  Expected: Same aisle formula D = |b1 - b2|
  Got: Different calculation method
  ```

- **Negative distance**:
  ```
  Error: Distance calculation produced negative value
  From: (2, 5) to (3, 2)
  Calculated: -3 (invalid)
  Check: Distance formula implementation
  ```

#### Strategy A Specific Errors:

- **Sorting failure**:
  ```
  Error: Failed to sort bays by aisle
  Input size: 10 bays
  Sorted size: 8 bays (mismatch)
  ```

- **Incorrect order**:
  ```
  Error: Bays not in ascending aisle order
  Expected: (1,5), (2,3), (3,4)
  Got: (1,5), (3,4), (2,3)
  ```

#### Strategy B Specific Errors:

- **Nearest neighbour not found**:
  ```
  Error: Cannot find nearest unvisited bay
  Current position: (2, 4)
  Remaining bays: 3
  Possible cause: Distance calculation error
  ```

- **Infinite loop detection**:
  ```
  Error: Visited same bay twice in greedy algorithm
  Bay: (3, 5)
  Iteration: 7
  Algorithm logic error detected
  ```

- **Incomplete path**:
  ```
  Error: Not all bays visited
  Expected: 10 bays
  Visited: 8 bays
  Missing: (1, 8), (4, 3)
  ```

#### Path Validation Errors:

- **Missing bays**:
  ```
  Error: Generated path missing required bays
  Input bays: {(1,8), (2,2), (3,4)}
  Output path: {(1,8), (2,2)}
  Missing: (3,4)
  ```

- **Extra bays**:
  ```
  Error: Path contains unexpected bays
  Input bays: 5 locations
  Output path: 6 locations
  Extra bay: (5, 7) - not in original picking plan
  ```

- **Duplicate visits**:
  ```
  Error: Bay appears multiple times in path
  Bay: (2, 3)
  Occurrences: 2
  Note: Duplicates should have been merged during preprocessing
  ```

#### Comparison and Output Errors:

- **Strategy results mismatch**:
  ```
  Warning: Both strategies should visit same bays
  Strategy A visited: 10 bays
  Strategy B visited: 9 bays
  ```

- **Total distance calculation error**:
  ```
  Error: Sum of leg distances doesn't match reported total
  Leg distances sum: 45 units
  Reported total: 43 units
  Recalculation required
  ```

#### General Error Principles:

- **Deterministic results**: Given same input, each strategy should always produce same output
- **Complete documentation**: Log the path and all distance calculations
- **Clear comparison**: Show both strategies' results side-by-side for analysis
- **Validation**: Verify mathematical correctness of distance calculations
- **Debugging info**: Include intermediate steps for troubleshooting
- **Performance**: Both strategies should complete in reasonable time even for large bay lists