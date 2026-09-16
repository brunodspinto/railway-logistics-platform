# USLP02 - Cargo Handling Interface

## User Story
**As a Station Storage Manager**, I intend to access the Cargo Handling functionality at a Railway Terminal and the results through a user-friendly text-based interface.

## Description
This user story provides the interface layer for accessing all cargo handling operations developed in ESINF (USEI01-USEI05). The interface must be text-based and user-friendly.

## Functional Requirements

### Menu Structure
The interface should provide access to:

1. **Wagon Unloading (USEI01)**
    - Load wagons from CSV
    - Display inventory by SKU, aisle, and bay
    - Support FEFO/FIFO ordering

2. **Order Processing (USEI02)**
    - Load orders from CSV
    - Check order eligibility
    - Allocate inventory to orders
    - Display allocation results

3. **Picking Plans (USEI03)**
    - Generate picking plans using different heuristics (FF, FFD, BFD)
    - Display trolley utilization
    - Show complete picking instructions

4. **Pick Path Sequencing (USEI04)**
    - Calculate optimal picking paths
    - Compare Strategy A (Ascending) vs Strategy B (Nearest-Neighbour)
    - Display total distances

5. **Returns & Quarantine (USEI05)**
    - Process returned goods
    - Manage quarantine queue
    - Generate audit logs

## Interface Requirements

### Input Validation
- Clear error messages for invalid inputs
- Confirmation prompts for critical operations
- Data validation before processing

### Output Display
- Well-formatted results with clear headers
- Tabular data where appropriate
- Summary statistics when relevant
- Success/failure messages

### Navigation
- Main menu with numbered options
- Easy return to main menu
- Exit option
- Clear instructions for each operation

## Data Files Required
- `bays.csv`: Warehouse structure
- `wagons.csv`: Incoming cargo
- `items.csv`: Product master data
- `orders.csv`: Customer orders
- `order_lines.csv`: Order details
- `returns.csv`: Returned products

## Technical Requirements

### Technology
- Text-based interface (console/terminal)
- Java implementation
- Integration with ESINF algorithms

### Error Handling
- Graceful handling of file not found
- Invalid data format handling
- Clear error messages
- System should remain stable

### User Experience
- Simple and intuitive navigation
- Consistent formatting
- Responsive feedback
- Help/instructions available

## Example Interface Flow

```
                _-====-__-======-__-========-_____-============-__
              _(                                                 _)
           OO(             Logistics on Rails 🚂                 )_
          0  (_                                               _)
        o0     (_                                           _)
       o         '=-___-===-_____-========-___________-===-dwb-='
     .o                                _________
    . ______          ______________  |         |      _____
  _()_||__|| ________ |            |  |_________|   __||___||__
 (LOGISTICS| |      | |            | __Y______00_| |_         _|
/-OO----OO""="OO--OO"="OO--------OO"="OO-------OO"="OO-------OO"=P

Welcome to the Logistics on Rails System (Sprint 1)

--- MAIN MENU ---
1. Know the Development Team
2. Run Program (USEI01–USEI05)
3. About the Project
0. Exit

Select an option: _
```

## Acceptance Criteria
- Text-based interface successfully implemented
- All ESINF functionalities (USEI01-USEI05) accessible
- Clear navigation between different operations
- Results displayed in readable format
- Error handling implemented
- User can perform multiple operations in sequence

## Dependencies
- USEI01-USEI05 must be implemented
- CSV data files available
- Java console I/O libraries

## Testing Considerations
- Test with sample CSV files
- Verify all menu options work
- Check error handling with invalid inputs
- Ensure results match ESINF specifications