# Code Complexity Analysis for USEI07
Team: Railway Logistics Platform
Sprint: 2 (2024/2025)

## TwoDTree Class

### 1. build(List<Station> stationsSortedByLat, List<Station> stationsSortedByLon)

**Code:**

```java
public void build(List<Station> stationsSortedByLat, List<Station> stationsSortedByLon) {
    this.nodeCount = 0;                                    // O(1)
    this.root = buildRecursive(stationsSortedByLat, 
                               stationsSortedByLon, 0);    // O(n log n)
}
```

**Overall Complexity: O(n log n)**

### 2. buildRecursive(List<Station> axisList, List<Station> otherList, int depth)

**Code:**

```java
private Node2D buildRecursive(List<Station> axisList, List<Station> otherList, int depth) {
    if (axisList.isEmpty()) {                              // O(1)
        return null;                                       // O(1)
    }

    int axis = depth % 2;                                  // O(1)
    int medianIdx = axisList.size() / 2;                   // O(1)
    Station medianStation = axisList.get(medianIdx);       // O(1)

    Node2D node = new Node2D(medianStation, axis);         // O(1)
    this.nodeCount++;                                      // O(1)

    Set<Station> duplicateBucket = new HashSet<>();        // O(1)
    duplicateBucket.add(medianStation);                    // O(1)

    // Aggregate duplicates to the left of the median
    int leftIdx = medianIdx - 1;                           // O(1)
    while (leftIdx >= 0 &&
           axisList.get(leftIdx).getLatitude() == medianStation.getLatitude() &&
           axisList.get(leftIdx).getLongitude() == medianStation.getLongitude()) {
        node.addStation(axisList.get(leftIdx));            // O(log d) - binary search
        duplicateBucket.add(axisList.get(leftIdx));        // O(1)
        leftIdx--;                                         // O(1)
    }                                                      // O(d log d) worst case

    // Aggregate duplicates to the right of the median
    int rightIdx = medianIdx + 1;                          // O(1)
    while (rightIdx < axisList.size() &&
           axisList.get(rightIdx).getLatitude() == medianStation.getLatitude() &&
           axisList.get(rightIdx).getLongitude() == medianStation.getLongitude()) {
        node.addStation(axisList.get(rightIdx));           // O(log d)
        duplicateBucket.add(axisList.get(rightIdx));       // O(1)
        rightIdx++;                                        // O(1)
    }                                                      // O(d log d) worst case

    // Create sublists for recursion
    List<Station> leftAxisList = new ArrayList<>(
        axisList.subList(0, leftIdx + 1));                 // O(n/2)
    List<Station> rightAxisList = new ArrayList<>(
        axisList.subList(rightIdx, axisList.size()));      // O(n/2)

    List<Station> leftOtherList = new ArrayList<>(leftAxisList.size());   // O(1)
    List<Station> rightOtherList = new ArrayList<>(rightAxisList.size()); // O(1)

    double medianCoordinate = node.getSplitCoordinate();   // O(1)

    // Partition the alternate list
    for (Station s : otherList) {                          // O(n) - iterates all stations
        if (duplicateBucket.contains(s)) {                 // O(1) - HashSet lookup
            continue;                                      // O(1)
        }

        double sCoord = Node2D.getCoordinate(s, axis);     // O(1)

        if (sCoord < medianCoordinate) {                   // O(1)
            leftOtherList.add(s);                          // O(1) amortized
        } else {                                           // O(1)
            rightOtherList.add(s);                         // O(1) amortized
        }
    }                                                      // Total: O(n)

    // Recursion on subtrees (swapping list roles)
    node.setLeft(buildRecursive(leftOtherList, leftAxisList, depth + 1));   // T(n/2)
    node.setRight(buildRecursive(rightOtherList, rightAxisList, depth + 1)); // T(n/2)

    return node;                                           // O(1)
}
```

**Overall Complexity: O(n log n)**

**Why O(n log n)?**

The recurrence relation for this algorithm is:
```
T(n) = O(n) + 2 × T(n/2)
```

Where:
- **O(n)**: Partitioning the alternate list at each level
- **2 × T(n/2)**: Two recursive calls on half-sized sublists
- **O(d log d)**: Duplicate aggregation (negligible since d ≪ n)

**Applying Master Theorem:**
- Form: T(n) = a × T(n/b) + f(n)
- Here: a = 2, b = 2, f(n) = O(n)
- log_b(a) = log_2(2) = 1
- f(n) = Θ(n¹) → **Case 2**

**Result:** T(n) = Θ(n log n)

**Example:** For n = 62,000 stations
- Tree height: ~16 levels
- Operations per level: ~62,000 (partitioning)
- Total operations: ~62,000 × 16 ≈ 992,000
- Speedup vs naive construction: Provides balanced tree guaranteeing O(√n + k) range queries

### 3. size()

**Code:**

```java
public int size() {
    return this.nodeCount;                                 // O(1)
}
```

**Overall Complexity: O(1)**

### 4. height() and heightRecursive(Node2D node)

**Code:**

```java
public int height() {
    return heightRecursive(root);                          // O(n)
}

private int heightRecursive(Node2D node) {
    if (node == null) return 0;                            // O(1)
    return 1 + Math.max(heightRecursive(node.getLeft()), 
                        heightRecursive(node.getRight())); // T(n/2) + T(n/2)
}
```

**Overall Complexity: O(n)** - visits all nodes once

### 5. getDistinctBucketSizes()

**Code:**

```java
public Set<Integer> getDistinctBucketSizes() {
    Set<Integer> sizes = new HashSet<>();                  // O(1)
    collectBucketSizes(root, sizes);                       // O(n)
    return sizes;                                          // O(1)
}

private void collectBucketSizes(Node2D node, Set<Integer> sizes) {
    if (node == null) return;                              // O(1)
    sizes.add(node.getBucketSize());                       // O(1)
    collectBucketSizes(node.getLeft(), sizes);             // T(n/2)
    collectBucketSizes(node.getRight(), sizes);            // T(n/2)
}
```

**Overall Complexity: O(n)** - complete tree traversal

### 6. haversineKm(double lat1, double lon1, double lat2, double lon2)

**Code:**

```java
public static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
    final double R = 6371.0;                               // O(1) - Earth radius in km
    double dLat = Math.toRadians(lat2 - lat1);             // O(1)
    double dLon = Math.toRadians(lon2 - lon1);             // O(1)
    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +       // O(1)
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon/2) * Math.sin(dLon/2);   // O(1)
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); // O(1)
    return R * c;                                          // O(1)
}
```

**Overall Complexity: O(1)**

## Node2D Class

### 1. Constructor Node2D(Station station, int axis)

**Code:**

```java
public Node2D(Station station, int axis) {
    this.stations = new ArrayList<>();                     // O(1)
    this.stations.add(station);                            // O(1)
    this.axis = axis;                                      // O(1)
    this.splitCoordinate = getCoordinate(station, axis);   // O(1)
    this.left = null;                                      // O(1)
    this.right = null;                                     // O(1)
}
```

**Overall Complexity: O(1)**

### 2. addStation(Station station)

**Code:**

```java
public void addStation(Station station) {
    int pos = Collections.binarySearch(stations, station); // O(log d)
    if (pos < 0) {                                         // O(1)
        stations.add(-pos - 1, station);                   // O(d) - worst case shift
    }
}
```

**Overall Complexity:**
- **Best Case:** O(log d) - station already exists
- **Average Case:** O(d) - needs to shift elements
- **Worst Case:** O(d) - insert at beginning

Where **d** = number of stations in the bucket (typically 1-3)

### 3. getCoordinate(Station s, int axis)

**Code:**

```java
public static double getCoordinate(Station s, int axis) {
    return (axis == 0) ? s.getLatitude() : s.getLongitude(); // O(1)
}
```

**Overall Complexity: O(1)**

### 4. Getters

**Code:**

```java
public List<Station> getStations() {
    return stations;                                       // O(1)
}

public int getBucketSize() {
    return stations.size();                                // O(1)
}

public double getSplitCoordinate() {
    return splitCoordinate;                                // O(1)
}

public int getAxis() {
    return axis;                                           // O(1)
}

public Node2D getLeft() {
    return left;                                           // O(1)
}

public Node2D getRight() {
    return right;                                          // O(1)
}
```

**Overall Complexity: O(1)** for all getters

### 5. Setters

**Code:**

```java
public void setLeft(Node2D left) {
    this.left = left;                                      // O(1)
}

public void setRight(Node2D right) {
    this.right = right;                                    // O(1)
}
```

**Overall Complexity: O(1)** for all setters

## Summary Table

| Class | Method | Time Complexity | Notes |
|-------|--------|-----------------|-------|
| **TwoDTree** | `build(...)` | **O(n log n)** | Balanced construction using sorted lists |
| | `buildRecursive(...)` | **O(n log n)** | Master Theorem Case 2 |
| | `size()` | **O(1)** | Returns cached node count |
| | `height()` | **O(n)** | Traverses entire tree |
| | `getDistinctBucketSizes()` | **O(n)** | Complete traversal |
| | `haversineKm(...)` | **O(1)** | Fixed mathematical operations |
| **Node2D** | `Node2D(...)` | **O(1)** | Constructor |
| | `addStation(...)` | **O(d)** | Binary search + shift [¹] |
| | `getCoordinate(...)` | **O(1)** | Static helper |
| | All getters | **O(1)** | Direct field access |
| | All setters | **O(1)** | Direct field assignment |

**Legend:**
- **n** = total number of stations (~62,000)
- **d** = number of duplicate stations at same coordinates (typically 1-3)
- **k** = number of results returned by spatial query

**Notes:**

[¹] `addStation()` uses binary search O(log d) to find insertion position, then shifts elements O(d). In practice, d is very small (1-3 stations per coordinate).

## Construction Strategy Analysis

### Balanced Build Approach

The 2D-tree construction follows an optimal O(n log n) strategy:

1. **Input:** Two pre-sorted lists (from AVL trees in USEI06)
   - `stationsSortedByLat`: sorted by latitude
   - `stationsSortedByLon`: sorted by longitude

2. **Median Selection:** At each recursion level, select the median from the current axis list → O(1)

3. **Duplicate Handling:** Aggregate all stations with identical coordinates into a bucket → O(d log d), d ≪ n

4. **Partitioning:** Split the alternate list based on median coordinate → O(n)

5. **Recursion:** Build left and right subtrees with swapped axis lists → 2 × T(n/2)

### Properties of the Resulting Tree

| Property | Value | Explanation |
|----------|-------|-------------|
| **Height** | ~log₂(n) ≈ 16 | Balanced by median selection |
| **Nodes** | ~61,500 | Less than n due to duplicate buckets |
| **Balance** | Guaranteed | Always splits at median |
| **Bucket Sizes** | [1, 2, 3] | Most nodes: 1 station; some: 2-3 |

### Comparison: 2D-Tree vs. Linear Scan

| Operation | Linear Scan | Balanced 2D-Tree | Speedup |
|-----------|-------------|------------------|---------|
| **Construction** | O(1) | O(n log n) | N/A |
| **Range Query** | O(n) | O(√n + k) | ~250× [²] |
| **Nearest-Neighbour** | O(n) | O(log n) | ~3,800× |
| **Space** | O(n) | O(n) | Same |

[²] For n = 62,000: √n ≈ 249. Pruning skips ~99.6% of nodes.

## Space Complexity

### TwoDTree Class
- **Tree Structure:** O(n) - one node per unique coordinate (or bucket for duplicates)
- **Build Method:** O(n log n) - recursion stack depth O(log n) × O(n) auxiliary space per level

### Node2D Class
- **Per Node:** O(d) - stores d stations in bucket (typically 1-3)
- **Total Tree:** O(n) - all stations stored exactly once across all buckets

## Implementation Notes

1. **Determinism:** Tree structure is fully deterministic given sorted input lists
2. **Duplicate Preservation:** Stations with identical coordinates are sorted by name using binary search
3. **Axis Alternation:** Each recursion level alternates between latitude (axis=0) and longitude (axis=1)
4. **List Swapping:** The roles of `axisList` and `otherList` swap at each recursion level to maintain sorted order
5. **Optimization:** Pre-sorted lists from AVL trees eliminate O(n log n) sorting overhead per level

## Example Execution

**Dataset:** 62,142 valid European railway stations

**Expected Output:**
```
══════════════════════════════════════════════════════════
 SPATIAL INDEX (2D-Tree)
──────────────────────────────────────────────────────────
 Size (Nodes)        : 61,487
 Height              : 16 (Target ~15.9)
 Buckets (Distinct)  : [1, 2, 3]
══════════════════════════════════════════════════════════
```

**Interpretation:**
- **Nodes < Stations:** Some coordinates have multiple stations (e.g., Lisbon Santa Apolónia and Lisbon Oriente at 38.71387, -9.122271)
- **Height ≈ Target:** Tree is well-balanced (16 vs theoretical 15.9)
- **Bucket Sizes:** Majority of nodes have 1 station; rare duplicates have 2-3

## Complexity Proof: Why O(n log n)?

### Recurrence Relation

At each recursion level:
- **Partitioning alternate list:** O(n)
- **Duplicate aggregation:** O(d log d) ≈ O(1) since d ≪ n
- **Sublist creation:** O(n)
- **Recursive calls:** 2 × T(n/2)

**Total:** T(n) = O(n) + 2 × T(n/2)

### Master Theorem Application

Given: T(n) = a × T(n/b) + f(n)

- a = 2 (two recursive calls)
- b = 2 (problem size halves)
- f(n) = O(n) (linear work per level)

Calculate: log_b(a) = log₂(2) = 1

Since f(n) = Θ(n¹) = Θ(n^log_b(a)), we have **Case 2**:

**Result:** T(n) = Θ(n log n)

### Tree Levels Analysis

| Level | Nodes | Work per Node | Total Work |
|-------|-------|---------------|------------|
| 0 | 1 | O(n) | O(n) |
| 1 | 2 | O(n/2) | O(n) |
| 2 | 4 | O(n/4) | O(n) |
| ... | ... | ... | ... |
| log n | n | O(1) | O(n) |

**Total:** O(n) × O(log n) = **O(n log n)**

## Critical Optimization: Avoiding Re-sorting

**Problem:** Naive 2D-tree construction sorts at every recursion level → O(n log² n)

**Solution:** Pre-sort once using AVL trees (USEI06), then maintain sorted order through intelligent partitioning → O(n log n)

**Key Insight:** By swapping `axisList` and `otherList` roles at each level, we ensure:
- Median selection remains O(1)
- No re-sorting needed
- Partitioning preserves sorted order

This optimization is **critical** for achieving O(n log n) complexity.