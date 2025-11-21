# Code Complexity Analysis for USEI06

**Team:** G054  
**Sprint:** 2 (2024/2025)

---

## 1. AVLTree Class

### 1.1 `insert(K key, V value)`

**Code:**
```java
public void insert(K key, V value) {
    root = insert(root, key, value);  // O(log n)
}

private AVLNode<K, V> insert(AVLNode<K, V> node, K key, V value) {
    if (node == null) {                          // O(1)
        size++;                                   // O(1)
        return new AVLNode<>(key, value);        // O(1)
    }

    int cmp = key.compareTo(node.key);           // O(1)
    if (cmp < 0) {                               // O(1)
        node.left = insert(node.left, key, value);   // O(log n) - recursive
    } else if (cmp > 0) {                        // O(1)
        node.right = insert(node.right, key, value); // O(log n) - recursive
    } else {                                     // O(1)
        node.addValue(value);                    // O(m) - m values per key
        return node;                             // O(1)
    }

    node.updateHeight();                         // O(1)
    int balance = node.getBalance();             // O(1)

    // Left-Left Case
    if (balance < -1 && key.compareTo(node.left.key) < 0)  // O(1)
        return rightRotate(node);                // O(1)

    // Right-Right Case
    if (balance > 1 && key.compareTo(node.right.key) > 0)  // O(1)
        return leftRotate(node);                 // O(1)

    // Left-Right Case
    if (balance < -1 && key.compareTo(node.left.key) > 0) {  // O(1)
        node.left = leftRotate(node.left);       // O(1)
        return rightRotate(node);                // O(1)
    }

    // Right-Left Case
    if (balance > 1 && key.compareTo(node.right.key) < 0) {  // O(1)
        node.right = rightRotate(node.right);    // O(1)
        return leftRotate(node);                 // O(1)
    }

    return node;                                 // O(1)
}
```

**Overall Complexity:** O(log n)

---

### 1.2 `search(K key)`

**Code:**
```java
public List<V> search(K key) {
    AVLNode<K, V> node = search(root, key);      // O(log n)
    return node == null ? new ArrayList<>() : node.getValues();  // O(1) or O(m)
}

private AVLNode<K, V> search(AVLNode<K, V> node, K key) {
    if (node == null) {                          // O(1)
        return null;                             // O(1)
    }

    int cmp = key.compareTo(node.key);           // O(1)
    if (cmp < 0) {                               // O(1)
        return search(node.left, key);           // O(log n) - recursive
    }
    if (cmp > 0) {                               // O(1)
        return search(node.right, key);          // O(log n) - recursive
    }
    return node;                                 // O(1)
}
```

**Overall Complexity:** O(log n + m), where m is the number of values at key

---

### 1.3 `rangeSearch(K min, K max)`

**Code:**
```java
public List<V> rangeSearch(K min, K max) {
    List<V> result = new ArrayList<>();          // O(1)
    rangeSearch(root, min, max, result);         // O(log n + k)
    return result;                               // O(1)
}

private void rangeSearch(AVLNode<K, V> node, K min, K max, List<V> result) {
    if (node == null) {                          // O(1)
        return;                                  // O(1)
    }

    if (node.key.compareTo(min) < 0) {           // O(1)
        rangeSearch(node.right, min, max, result);  // O(log n + k) - prune left
    }
    else if (node.key.compareTo(max) > 0) {      // O(1)
        rangeSearch(node.left, min, max, result);   // O(log n + k) - prune right
    }
    else {                                       // O(1)
        rangeSearch(node.left, min, max, result);   // O(k₁)
        result.addAll(node.getValues());         // O(m) - m values at node
        rangeSearch(node.right, min, max, result);  // O(k₂)
    }
}
```

**Overall Complexity:** O(log n + k), where k is the number of results

---

### 1.4 `inOrder()`

**Code:**
```java
public List<V> inOrder() {
    List<V> result = new ArrayList<>();          // O(1)
    inOrder(root, result);                       // O(n)
    return result;                               // O(1)
}

private void inOrder(AVLNode<K, V> node, List<V> result) {
    if (node == null) {                          // O(1)
        return;                                  // O(1)
    }
    inOrder(node.left, result);                  // O(n/2) - visit left
    result.addAll(node.getValues());             // O(m) - m values per node
    inOrder(node.right, result);                 // O(n/2) - visit right
}
```

**Overall Complexity:** O(n)

---

### 1.5 `rightRotate(AVLNode<K, V> y)` and `leftRotate(AVLNode<K, V> x)`

**Code:**
```java
private AVLNode<K, V> rightRotate(AVLNode<K, V> y) {
    AVLNode<K, V> x = y.left;                    // O(1)
    AVLNode<K, V> B = x.right;                   // O(1)

    x.right = y;                                 // O(1)
    y.left = B;                                  // O(1)

    y.updateHeight();                            // O(1)
    x.updateHeight();                            // O(1)

    return x;                                    // O(1)
}
```

**Overall Complexity:** O(1)

---

## 2. StationIndexes Class

### 2.1 `buildIndexes(List<Station> stations)`

**Code:**
```java
public Map<Station, String> buildIndexes(List<Station> stations) {
    long start = System.currentTimeMillis();     // O(1)
    Map<Station, String> rejected = new HashMap<>();  // O(1)
    List<Station> validStations = new ArrayList<>();  // O(1)

    // Validation loop
    for (Station station : stations) {           // O(n)
        String error = station.getValidationError();  // O(1)
        if (error != null) {                     // O(1)
            rejected.put(station, error);        // O(1)
            continue;                            // O(1)
        }
        validStations.add(station);              // O(1)
    }

    // Build AVL indexes
    for (Station station : validStations) {      // O(n)
        latIndex.insert(station.getLatitude(), station);      // O(log n)
        lonIndex.insert(station.getLongitude(), station);     // O(log n)
        
        CompositeKey key = new CompositeKey(                  // O(1)
            station.getTimeZoneGroup(), 
            station.getCountry()
        );
        tzIndex.insert(key, station);            // O(log n)
    }

    // Build 2D-tree (USEI07 - not analyzed here)
    List<Station> stationsSortedByLat = latIndex.inOrder();  // O(n)
    List<Station> stationsSortedByLon = lonIndex.inOrder();  // O(n)
    spatialIndex.build(stationsSortedByLat, stationsSortedByLon);  // O(n)

    this.total = validStations.size();           // O(1)
    this.buildTime = System.currentTimeMillis() - start;  // O(1)

    return rejected;                             // O(1)
}
```

**Overall Complexity:** O(n log n)

---

## 3. CoordinateQuery Class

### 3.1 `queryByLatitudeRange(double minLat, double maxLat)`

**Code:**
```java
public QueryResult queryByLatitudeRange(double minLat, double maxLat) {
    long start = System.nanoTime();              // O(1)

    // Validation
    if (minLat < -90 || minLat > 90 || maxLat < -90 || maxLat > 90) {  // O(1)
        throw new IllegalArgumentException("Latitude must be in [-90, 90]");
    }
    if (minLat > maxLat) {                       // O(1)
        throw new IllegalArgumentException("minLat must be <= maxLat");
    }

    AVLTree<Double, Station> latIndex = indexes.getLatitudeIndex();  // O(1)
    List<Station> results = latIndex.rangeSearch(minLat, maxLat);  // O(log n + k)

    sortByLatitudeThenName(results);             // O(k²)

    long time = (System.nanoTime() - start) / 1_000_000;  // O(1)

    QueryResult r = new QueryResult("LATITUDE_RANGE", results, time, results.size());  // O(1)
    r.addMeta("minLatitude", minLat);            // O(1)
    r.addMeta("maxLatitude", maxLat);            // O(1)
    r.addMeta("complexity", "O(log n + k)");     // O(1)
    return r;                                    // O(1)
}
```

**Overall Complexity:** O(log n + k²)

---

### 3.2 `queryByLongitudeRange(double minLon, double maxLon)`

**Code:** (Similar structure to queryByLatitudeRange)

**Overall Complexity:** O(log n + k²)

---

### 3.3 `queryByExactCoordinates(double lat, double lon)`

**Code:**
```java
public QueryResult queryByExactCoordinates(double lat, double lon) {
    long start = System.nanoTime();              // O(1)

    AVLTree<Double, Station> latIndex = indexes.getLatitudeIndex();  // O(1)
    List<Station> latResults = latIndex.search(lat);  // O(log n + m)

    List<Station> results = new ArrayList<>();   // O(1)
    for (Station station : latResults) {         // O(m)
        if (Math.abs(station.getLongitude() - lon) < 0.000001) {  // O(1)
            results.add(station);                // O(1)
        }
    }

    sortByName(results);                         // O(m²)

    long time = (System.nanoTime() - start) / 1_000_000;  // O(1)

    QueryResult r = new QueryResult("EXACT_COORDINATES", results, time, 1);  // O(1)
    r.addMeta("latitude", lat);                  // O(1)
    r.addMeta("longitude", lon);                 // O(1)
    r.addMeta("complexity", "O(log n)");         // O(1)
    return r;                                    // O(1)
}
```

**Overall Complexity:** O(log n + m²), where m is stations at (lat, lon)

---

### 3.4 `sortByName(List<Station> stations)` - Insertion Sort

**Code:**
```java
private void sortByName(List<Station> stations) {
    int n = stations.size();                     // O(1)

    for (int i = 1; i < n; i++) {                // O(n) outer loop
        Station key = stations.get(i);           // O(1)
        int j = i - 1;                           // O(1)

        while (j >= 0 && stations.get(j).getName().compareTo(key.getName()) > 0) {  // O(n) worst
            stations.set(j + 1, stations.get(j));  // O(1)
            j--;                                 // O(1)
        }

        stations.set(j + 1, key);                // O(1)
    }
}
```

**Overall Complexity:**
- Best Case: O(n) - already sorted
- Average Case: O(n²)
- Worst Case: O(n²) - reverse sorted

---

### 3.5 `sortByLatitudeThenName(List<Station> stations)` - Insertion Sort

**Code:**
```java
private void sortByLatitudeThenName(List<Station> stations) {
    int n = stations.size();                     // O(1)

    for (int i = 1; i < n; i++) {                // O(n) outer loop
        Station key = stations.get(i);           // O(1)
        int j = i - 1;                           // O(1)

        while (j >= 0) {                         // O(n) worst case
            Station current = stations.get(j);   // O(1)
            int latCompare = Double.compare(current.getLatitude(), key.getLatitude());  // O(1)

            boolean shouldMove = false;          // O(1)
            if (latCompare > 0) {                // O(1)
                shouldMove = true;               // O(1)
            } else if (latCompare == 0) {        // O(1)
                if (current.getName().compareTo(key.getName()) > 0) {  // O(1)
                    shouldMove = true;           // O(1)
                }
            }

            if (!shouldMove) break;              // O(1)

            stations.set(j + 1, current);        // O(1)
            j--;                                 // O(1)
        }

        stations.set(j + 1, key);                // O(1)
    }
}
```

**Overall Complexity:** O(n²)

---

## 4. TimeZoneQuery Class

### 4.1 `queryByTimeZoneGroup(String tzGroup)`

**Code:**
```java
public QueryResult queryByTimeZoneGroup(String tzGroup) {
    long start = System.nanoTime();              // O(1)
    List<Station> results = new ArrayList<>();   // O(1)
    int visited = 0;                             // O(1)

    AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();  // O(1)
    List<Station> all = tzIndex.inOrder();       // O(n)

    for (Station s : all) {                      // O(n)
        visited++;                               // O(1)
        if (s.getTimeZoneGroup().equals(tzGroup)) {  // O(1)
            results.add(s);                      // O(1)
        }
    }

    sortByCountryThenName(results);              // O(k²)

    long time = (System.nanoTime() - start) / 1_000_000;  // O(1)

    QueryResult r = new QueryResult("TIME_ZONE_GROUP", results, time, visited);  // O(1)
    r.addMeta("timeZoneGroup", tzGroup);         // O(1)
    r.addMeta("complexity", "O(k log n)");       // O(1)
    return r;                                    // O(1)
}
```

**Overall Complexity:** O(n + k²), where k is results matching tzGroup

---

### 4.2 `queryByTimeZoneGroupAndCountry(String tzGroup, String country)`

**Code:**
```java
public QueryResult queryByTimeZoneGroupAndCountry(String tzGroup, String country) {
    long start = System.nanoTime();              // O(1)

    CompositeKey key = new CompositeKey(tzGroup, country);  // O(1)
    AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();  // O(1)

    List<Station> results = tzIndex.search(key);  // O(log n + k)

    long time = (System.nanoTime() - start) / 1_000_000;  // O(1)

    QueryResult r = new QueryResult("TIME_ZONE_COUNTRY", results, time, 1);  // O(1)
    r.addMeta("timeZoneGroup", tzGroup);         // O(1)
    r.addMeta("country", country);               // O(1)
    r.addMeta("complexity", "O(log n + k)");     // O(1)
    return r;                                    // O(1)
}
```

**Overall Complexity:** O(log n + k)

---

### 4.3 `queryByTimeZoneWindow(List<String> tzGroups)`

**Code:**
```java
public QueryResult queryByTimeZoneWindow(List<String> tzGroups) {
    long start = System.nanoTime();              // O(1)
    List<Station> results = new ArrayList<>();   // O(1)
    int visited = 0;                             // O(1)

    AVLTree<CompositeKey, Station> tzIndex = indexes.getTimeZoneIndex();  // O(1)

    for (String tzGroup : tzGroups) {            // O(m) - m timezones
        List<Station> all = tzIndex.inOrder();   // O(n)

        for (Station s : all) {                  // O(n)
            visited++;                           // O(1)
            if (s.getTimeZoneGroup().equals(tzGroup)) {  // O(1)
                results.add(s);                  // O(1)
            }
        }
    }

    results = removeDuplicates(results);         // O(k²)

    sortByCountryThenName(results);              // O(k²)

    long time = (System.nanoTime() - start) / 1_000_000;  // O(1)

    QueryResult r = new QueryResult("TIME_ZONE_WINDOW", results, time, visited);  // O(1)
    r.addMeta("timeZoneWindow", tzGroups.toString());  // O(1)
    r.addMeta("windowSize", tzGroups.size());    // O(1)
    r.addMeta("complexity", "O(m * log n + k)"); // O(1)
    return r;                                    // O(1)
}
```

**Overall Complexity:** O(m × n + k²), where m is number of timezones, k is total results

---

### 4.4 `sortByCountryThenName(List<Station> stations)` - Insertion Sort

**Code:**
```java
private void sortByCountryThenName(List<Station> stations) {
    int n = stations.size();                     // O(1)

    for (int i = 1; i < n; i++) {                // O(n) outer loop
        Station key = stations.get(i);           // O(1)
        int j = i - 1;                           // O(1)

        while (j >= 0) {                         // O(n) worst case
            Station current = stations.get(j);   // O(1)
            int countryCompare = current.getCountry().compareTo(key.getCountry());  // O(1)

            boolean shouldMove = false;          // O(1)
            if (countryCompare > 0) {            // O(1)
                shouldMove = true;               // O(1)
            } else if (countryCompare == 0) {    // O(1)
                if (current.getName().compareTo(key.getName()) > 0) {  // O(1)
                    shouldMove = true;           // O(1)
                }
            }

            if (!shouldMove) break;              // O(1)

            stations.set(j + 1, current);        // O(1)
            j--;                                 // O(1)
        }

        stations.set(j + 1, key);                // O(1)
    }
}
```

**Overall Complexity:** O(n²)

---

### 4.5 `removeDuplicates(List<Station> stations)`

**Code:**
```java
private List<Station> removeDuplicates(List<Station> stations) {
    List<Station> unique = new ArrayList<>();    // O(1)

    for (Station station : stations) {           // O(n) outer loop
        boolean found = false;                   // O(1)

        for (Station existing : unique) {        // O(n) inner loop
            if (existing.equals(station)) {      // O(1)
                found = true;                    // O(1)
                break;                           // O(1)
            }
        }

        if (!found) {                            // O(1)
            unique.add(station);                 // O(1)
        }
    }

    return unique;                               // O(1)
}
```

**Overall Complexity:** O(n²)

---

## 5. CompositeKey Class

### 5.1 `compareTo(CompositeKey other)`

**Code:**
```java
@Override
public int compareTo(CompositeKey other) {
    int tzCompare = this.timeZoneGroup.compareTo(other.timeZoneGroup);  // O(s₁)
    if (tzCompare != 0) {                        // O(1)
        return tzCompare;                        // O(1)
    }
    return this.country.compareTo(other.country);  // O(s₂)
}
```

**Overall Complexity:** O(s₁ + s₂), where s₁ and s₂ are string lengths  
**In Practice:** O(1) - strings are short ("CET", "PT")

---

## Summary Table

| Class | Method | Time Complexity |
|-------|--------|-----------------|
| **AVLTree** | `insert(key, value)` | O(log n) |
| | `search(key)` | O(log n + m) |
| | `rangeSearch(min, max)` | O(log n + k) |
| | `inOrder()` | O(n) |
| | `rightRotate()` / `leftRotate()` | O(1) |
| **StationIndexes** | `buildIndexes(stations)` | O(n log n) |
| **CoordinateQuery** | `queryByLatitudeRange()` | O(log n + k²) |
| | `queryByLongitudeRange()` | O(log n + k²) |
| | `queryByExactCoordinates()` | O(log n + m²) |
| | `sortByName()` | O(n²) |
| | `sortByLatitudeThenName()` | O(n²) |
| **TimeZoneQuery** | `queryByTimeZoneGroup()` | O(n + k²) |
| | `queryByTimeZoneGroupAndCountry()` | O(log n + k) |
| | `queryByTimeZoneWindow()` | O(m×n + k²) |
| | `sortByCountryThenName()` | O(n²) |
| | `removeDuplicates()` | O(n²) |
| **CompositeKey** | `compareTo()` | O(1) |

**Legend:**
- n = total number of stations (62,142)
- k = number of results returned by query
- m = number of values at specific key
- s = string length