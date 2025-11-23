USEI07 - Build a balanced 2D-Tree on Latitude/Longitude - Complexity Analysis
Context
Construction of a balanced 2D tree over ~64k European stations using coordinates (latitude, longitude). The goal is to support efficient spatial queries (range search, nearest-neighbour) through a balanced construction strategy that leverages the AVL indices already created in USEI06.

Construction Algorithm
Balanced Construction Strategy
The construction uses the recursive algorithm buildRecursive which:

Receives two sorted lists: one by the current axis and another by the alternate axis
Selects the median of the current axis list as the root node
Groups duplicates (same coordinates) in the same node (bucket)
Partitions the remaining lists recursively for left/right subtrees


Temporal Complexity Analysis
1. Method build(List<Station> stationsSortedByLat, List<Station> stationsSortedByLon)
   javapublic void build(List<Station> stationsSortedByLat, List<Station> stationsSortedByLon) {
   this.nodeCount = 0;                                    // O(1)
   this.root = buildRecursive(stationsSortedByLat,
   stationsSortedByLon, 0);    // T(n)
   }
   Complexity: O(n log n)
   Justification:

Initialization: O(1)
Recursive call: dominated by buildRecursive complexity


2. Method buildRecursive(List<Station> axisList, List<Station> otherList, int depth)
   javaprivate Node2D buildRecursive(List<Station> axisList, List<Station> otherList, int depth) {
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

Detailed Analysis by Section
A. Initialization and Median Selection
javaint axis = depth % 2;
int medianIdx = axisList.size() / 2;
Station medianStation = axisList.get(medianIdx);
Node2D node = new Node2D(medianStation, axis);
Complexity: O(1)

B. Duplicate Aggregation
javawhile (leftIdx >= 0 && /* duplicate conditions */) {
node.addStation(axisList.get(leftIdx));  // O(log d) - binary search
duplicateBucket.add(axisList.get(leftIdx));
leftIdx--;
}
Complexity: O(d log d), where d is the number of duplicates at the same coordinates
Justification:

Each addStation() performs a binary search on the bucket's sorted station list: O(log d)
Worst case: d consecutive duplicates: O(d log d)
In practice, d is very small (typically 1-3 stations per coordinate)


C. Sublist Creation
javaList<Station> leftAxisList = new ArrayList<>(axisList.subList(0, leftIdx + 1));
List<Station> rightAxisList = new ArrayList<>(axisList.subList(rightIdx, axisList.size()));
Complexity: O(n)
Justification:

subList() creates a view: O(1)
ArrayList(Collection) constructor copies elements: O(sublist size)
Total: O(n/2) + O(n/2) = O(n)


D. Alternate List Partitioning
javafor (Station s : otherList) {                    // O(n)
if (duplicateBucket.contains(s)) continue;   // O(1)
double sCoord = Node2D.getCoordinate(s, axis);
if (sCoord < medianCoordinate)
leftOtherList.add(s);
else
rightOtherList.add(s);
}
Complexity: O(n)
Justification:

Iterates all n stations in otherList
HashSet lookup: O(1)
ArrayList add: O(1) amortized

Critical Note: This is the most expensive operation at each recursion level.

E. Recursive Calls
javanode.setLeft(buildRecursive(leftOtherList, leftAxisList, depth + 1));   // T(n/2)
node.setRight(buildRecursive(rightOtherList, rightAxisList, depth + 1)); // T(n/2)


**Complexity:** 2 × T(n/2)

---

### **Total Recurrence**

**For one recursion level:**

T(n) = O(n)           [partitioning]
+ O(d log d)     [duplicate aggregation, d ≪ n]
+ 2 × T(n/2)     [recursion on subtrees]


**Simplifying (d is negligible compared to n):**

T(n) = O(n) + 2 × T(n/2)


**Applying the Master Theorem:**
- Form: T(n) = a × T(n/b) + f(n)
- Here: a = 2, b = 2, f(n) = O(n)
- log_b(a) = log_2(2) = 1
- f(n) = Θ(n^1) → **Case 2**

**Result:**

T(n) = Θ(n log n)

Total Construction Complexity
Prerequisite: The lists stationsSortedByLat and stationsSortedByLon are already sorted (obtained via latIndex.inOrder() and lonIndex.inOrder() from USEI06)
OperationComplexityObtain sorted lists (AVL inOrder)O(n) each2D-Tree constructionO(n log n)TOTALO(n log n)

Space Complexity Analysis
javaSet<Station> duplicateBucket = new HashSet<>();          // O(d) per node
List<Station> leftAxisList = new ArrayList<>(...);       // O(n/2)
List<Station> rightAxisList = new ArrayList<>(...);      // O(n/2)
List<Station> leftOtherList = new ArrayList<>(...);      // O(n/2)
List<Station> rightOtherList = new ArrayList<>(...);     // O(n/2)
Space per recursive call: O(n)
Maximum recursion depth: O(log n) (balanced tree)
Total space on recursion stack: O(n log n)
Note: Due to list copies at each level, auxiliary space is O(n log n), which could be optimized using indices instead of sublists.

Properties of the Resulting Tree
Tree Height

Expected: ≈ log₂(n) ≈ 16 for n = 62,000
Guaranteed: Balanced, as it always divides by the median

Number of Nodes

Without duplicates: n nodes
With duplicates: < n nodes (multiple stations can be in the same node/bucket)

Bucket Sizes

Majority: 1 station per node
Some: 2-3 stations (e.g., "Lisbon Santa Apolónia" and "Lisbon Oriente" at 38.71387, -9.122271)


Auxiliary Methods
3. height() and heightRecursive(Node2D node)
   javapublic int height() {
   return heightRecursive(root);                          // T(n)
   }

private int heightRecursive(Node2D node) {
if (node == null) return 0;                            // O(1)
return 1 + Math.max(heightRecursive(node.getLeft()),
heightRecursive(node.getRight())); // T(n)
}
Complexity: O(n) - visits all nodes once

4. getDistinctBucketSizes()
   javapublic Set<Integer> getDistinctBucketSizes() {
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


**Complexity:** O(n) - complete tree traversal

---

## Comparison: 2D-Tree vs. Linear Scan

| Operation | Linear Scan | Balanced 2D-Tree |
|----------|-------------|-------------------|
| Construction | O(1) | **O(n log n)** |
| Range Query | O(n) | **O(√n + k)** |
| Nearest-Neighbour | O(n) | **O(log n)** (average) |
| Space | O(n) | **O(n)** |

**Advantage:** Spatial queries ~250x faster (for n=62k, √n ≈ 250)

---

## Summary Table - Complexities

| Method | Time Complexity | Space Complexity |
|--------|----------------------|---------------------|
| `build()` | **O(n log n)** | O(n) |
| `buildRecursive()` | **O(n log n)** | O(n log n) [stack] |
| `size()` | **O(1)** | O(1) |
| `height()` | **O(n)** | O(log n) [stack] |
| `getDistinctBucketSizes()` | **O(n)** | O(k + log n) [k = distinct sizes] |

**Legend:**
- **n** = total number of stations (~62,000)
- **d** = number of duplicates at the same coordinates (typically 1-3)
- **k** = number of results returned by a query

---

## Implementation Notes

1. **Pre-sorting:** Input lists must be sorted (obtained via AVL inOrder from USEI06)
2. **Axis Swapping:** At each recursion level, `axisList` and `otherList` swap roles
3. **Duplicate Preservation:** Stations with same coordinates are kept sorted by name using `Collections.binarySearch()`
4. **Determinism:** The resulting tree is always the same for the same input dataset

---

## Execution Example

**Dataset:** 62,142 valid stations

**Expected result:**

Size (Nodes)        : ~61,500 (some nodes have buckets with 2-3 stations)
Height              : 16-17 (Target ~15.9)
Buckets (Distinct)  : [1, 2, 3] (majority=1, some duplicates=2-3)
Build Time          : O(n log n) ≈ 62,142 × 16 ≈ 994,272 operations