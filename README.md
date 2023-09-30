## Employee Analysis Program

This program reads employee data from a CSV file and performs various analyses based on the user's input.

## Usage

1. Run the program and enter the path to the CSV file when prompted.
2. Choose from the following options:
   - **1. Analyze employees who worked for 7 consecutive days:**
     Analyzes employees who worked for 7 or more consecutive days.
   - **2. Analyze employees with short breaks:**
     Analyzes employees with less than 10 hours between shifts but greater than 1 hour.
   - **3. Analyze employees who worked for more than 14 hours in a single shift:**
     Identifies employees who worked for an extended period in a single shift.
   - **4. Exit:**
     Exits the program.

## Analysis Details

- The program checks the employee data for consecutive working days, short breaks, and long shifts.
- It uses date and time information to determine the duration of shifts and breaks.

## How to Run

1. Compile the program using `javac employee1.java`.
2. Run the program using `java employee1`.

Make sure you have Java installed on your system.

## Input Handling and File Reading

The program handles user input for the file path and choice selection using the `Scanner` class in Java.

The file reading is managed through Java's `BufferedReader` class. The program reads the employee data from the provided CSV file.

## Methods

### analyzeEmployeesConsecutiveDays(BufferedReader br)
Analyzes employees who worked for 7 or more consecutive days.

### analyzeEmployeesShortBreaks(BufferedReader br)
Analyzes employees with less than 10 hours between shifts but greater than 1 hour.

### analyzeEmployeesLongShifts(BufferedReader br)
Identifies employees who worked for more than 14 hours in a single shift.

### isTimeDifferenceLessThan10Hours(String timeOut, String timeIn)
Checks if the time difference between timeOut and timeIn is less than 10 hours.

### isTimeDifferenceMoreThan14Hours(String timeIn, String timeOut)
Checks if the time difference between timeIn and timeOut is more than 14 hours.

### isSameDay(String dateStr1, String dateStr2)
Checks if two date strings represent the same day.

### isNextDay(String targetDateStr, String currentDateStr)
Checks if the target date is exactly one day after the current date.

## Notes

- The program assumes specific positions and data structure within the CSV file. Modify accordingly for different formats.
- Ensure the CSV file contains relevant and accurate employee data for meaningful analysis.
