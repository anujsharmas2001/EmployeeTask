import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EmpTask {

	public static void main(String[] args) throws ParseException {
	    Scanner scanner = new Scanner(System.in);

	    // Prompt the user to enter the path of the file
	    System.out.println("Enter path of file: ");
	    String filename = scanner.nextLine();
	    
//	    Sample path for testing
//	    filename = "C:\\Users\\abhay\\Downloads\\Assignment_Timecard.xlsx - Sheet1.csv";

	    try {
	        BufferedReader br = new BufferedReader(new FileReader(filename));

	        // Skip the header line
	        br.readLine();

	        while (true) {
	            // Display the menu and prompt the user to choose an option
	            System.out.println("\nSelect an option:");
	            System.out.println("1. Analyze employees who worked for 7 consecutive days");
	            System.out.println("2. Analyze employees with less than 10 hours between shifts but greater than 1 hour");
	            System.out.println("3. Analyze employees who worked for more than 14 hours in a single shift");
	            System.out.println("4. Exit");

	            int choice = scanner.nextInt();

	            if (choice == 4) {
	                System.err.print("Exited.....");
	                break;
	            }

	            switch (choice) {
	                case 1:
	                    // Reopen the file and analyze employees for 7 consecutive days
	                    br = new BufferedReader(new FileReader(filename));
	                    br.readLine();  // Skip the header line
	                    analyzeEmployeesConsecutiveDays(br);
	                    break;
	                case 2:
	                    // Reopen the file and analyze employees for short breaks
	                    br = new BufferedReader(new FileReader(filename));
	                    br.readLine();  // Skip the header line
	                    analyzeEmployeesShortBreaks(br);
	                    break;
	                case 3:
	                    // Reopen the file and analyze employees for long shifts
	                    br = new BufferedReader(new FileReader(filename));
	                    br.readLine();  // Skip the header line
	                    analyzeEmployeesLongShifts(br);
	                    break;
	                default:
	                    System.out.println("Invalid option. Please choose a valid option.");
	            }
	        }

	        scanner.close();
	    } catch (IOException e) {
	        System.err.println("Error reading the file: " + e.getMessage());
	    }
	}

   
    /**
     * Analyzes employees who worked for 7 consecutive days.
     *
     * @param br BufferedReader to read the CSV data.
     * @throws IOException If there's an error reading the CSV data.
     * @throws ParseException If there's an error parsing the timestamps.
     */
    private static void analyzeEmployeesConsecutiveDays(BufferedReader br) throws IOException, ParseException {
        String currentPositionId = null;
        String currentTimeIn = null;
        String line;
        int consecutiveDaysCount = 0;
        int flag = 1;

        while ((line = br.readLine()) != null) {
            List<String> employeeData = parseCSVLine(line);

            // Adjust the positions based on the provided CSV structure
            String positionId = employeeData.get(0);
            String employeeName = employeeData.get(7); // Remove surrounding quotes

            // Parse the time in
            String timeIn = null;
            if (!employeeData.get(2).isEmpty()) {
                timeIn = employeeData.get(2);
            }

            // Check if it's the same employee
            if (positionId.equals(currentPositionId)) {
                if (isNextDay(timeIn, currentTimeIn)) {
                    consecutiveDaysCount++;
                } else if (!isSameDay(timeIn, currentTimeIn)) {
                    consecutiveDaysCount = 1;
                }
            } else {
                consecutiveDaysCount = 1; // Reset for a new employee
                flag = 1;
            }

            // Update current details for comparison
            currentPositionId = positionId;
            currentTimeIn = timeIn;

            // Check if the employee has worked for 7 consecutive days
            if (consecutiveDaysCount == 7 && flag == 1) {
                System.out.println("\nEmployee Name: " + employeeName + "\nPositionId :" + currentPositionId +
                        "\nReason: Worked 7 consecutive days ");
                flag = 0;
            }
        }
    }


    /**
     * Analyzes employees for short breaks between shifts.
     *
     * @param br BufferedReader to read the CSV data.
     * @throws IOException If there's an error reading the CSV data.
     * @throws ParseException If there's an error parsing the timestamps.
     */
    private static void analyzeEmployeesShortBreaks(BufferedReader br) throws IOException, ParseException {
        String currentPositionId = null;
        String currentTimeIn = null;
        String currentTimeOut = null;
        String line;
        int consecutiveDaysCount = 0;
        int flag = 1;

        while ((line = br.readLine()) != null) {
            List<String> employeeData = parseCSVLine(line);

            // Adjust the positions based on the provided CSV structure
            String positionId = employeeData.get(0);
            String employeeName = employeeData.get(7); // Remove surrounding quotes
            // Parse the time in and time out
            String timeInStr = employeeData.get(2);
            String timeOutStr = employeeData.get(3);

            // Check if timeIn and timeOut are not empty and for the same positionId
            if (!timeInStr.isEmpty() && !timeOutStr.isEmpty() && positionId.equals(currentPositionId)) {
                if (isSameDay(timeInStr, currentTimeIn) && isTimeDifferenceLessThan10Hours(currentTimeOut, timeInStr)) {
                    consecutiveDaysCount++;
                    flag = 0;
                }

                // Check if the employee has a short break
                if (flag == 0 && consecutiveDaysCount == 1 && isTimeDifferenceLessThan10Hours(currentTimeOut, timeInStr)) {
                    System.out.println("\nEmployee: " + employeeName + "\nPositionId :" + currentPositionId +
                            "\nReason: Employees with less than 10 hours between shifts but greater than 1 hour ");
                    flag = 1;
                }
            } else {
                consecutiveDaysCount = 0;
                flag = 1;
                currentPositionId = positionId;
                currentTimeIn = timeInStr;
                currentTimeOut = timeOutStr;
            }
        }
    }



    /**
     * Checks if the time difference between two timestamps is less than 10 hours.
     *
     * @param timeOut The timestamp for timeOut.
     * @param timeIn The timestamp for timeIn.
     * @return True if the time difference is greater than 1 hour and less than 10 hours, false otherwise.
     * @throws ParseException If there's an error parsing the timestamps.
     */
    private static boolean isTimeDifferenceLessThan10Hours(String timeOut, String timeIn) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date dateOut = format.parse(timeOut);
        Date dateIn = format.parse(timeIn);

        // Calculate the time difference in hours
        long diffInMilli = dateIn.getTime() - dateOut.getTime();
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilli);

        // Check if the time difference is within the specified range
        return diffInHours > 1 && diffInHours < 10;
    }

    /**
     * Checks if the time difference between two timestamps is more than 14 hours.
     *
     * @param timeIn The timestamp for timeIn.
     * @param timeOut The timestamp for timeOut.
     * @return True if the time difference is greater than 14 hours, false otherwise.
     * @throws ParseException If there's an error parsing the timestamps.
     */
    private static boolean isTimeDifferenceMoreThan14Hours(String timeIn, String timeOut) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date dateOut = format.parse(timeOut);
        Date dateIn = format.parse(timeIn);

        // Calculate the time difference in hours
        long diffInMilli = dateOut.getTime() - dateIn.getTime();
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilli);

        // Check if the time difference is more than 14 hours
        return diffInHours > 14;
    }




    private static void analyzeEmployeesLongShifts(BufferedReader br) throws IOException, ParseException {
        String line;

        while ((line = br.readLine()) != null) {
            // Parse the CSV line into a list of employee data
            List<String> employeeData = parseCSVLine(line);

            // Extract relevant data from the employee data list
            String positionId = employeeData.get(0);
            String employeeName = employeeData.get(7); // Remove surrounding quotes
            String timeInStr = employeeData.get(2);
            String timeOutStr = employeeData.get(3);

            // Check if timeIn and timeOut are not empty and for the same positionId
            if (!timeInStr.isEmpty() && !timeOutStr.isEmpty()) {
                // Check if the time difference is more than 14 hours
                if (isTimeDifferenceMoreThan14Hours(timeInStr, timeOutStr)) {
                    // Print the employee details and reason for more than 14 hours shift
                    System.out.println("\nEmployee: " + employeeName + "\nPositionId: " + positionId +
                            "\nReason: Employee who worked for more than 14 hours in a single shift");
                }
            }
        }
    }


    private static boolean isSameDay(String dateStr1, String dateStr2) {
        try {
            // Create a date formatter to parse the date and time strings
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

            // Parse the date strings into Date objects
            Date date1 = dateFormat.parse(dateStr1);
            Date date2 = dateFormat.parse(dateStr2);

            // Create Calendar instances and set their times to the parsed dates
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            // Compare the year, month, and day of the month for both dates
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            // Handle any parsing errors and return false
            System.err.println("Error parsing dates: " + e.getMessage());
            return false;
        }
    }



    private static boolean isNextDay(String targetDateStr, String currentDateStr) throws ParseException {
        // Create a date formatter to parse the date and time strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        // Parse the target date string and current date string to Date objects
        Date targetDate = dateFormat.parse(targetDateStr);
        Date currentDate = dateFormat.parse(currentDateStr);

        // Create Calendar instances and set their times to the parsed dates
        Calendar calTarget = Calendar.getInstance();
        calTarget.setTime(targetDate);

        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTime(currentDate);

        // Add 1 day to the current date
        calCurrent.add(Calendar.DAY_OF_YEAR, 1);

        // Check if the target date is exactly one day after the current date
        return calTarget.get(Calendar.YEAR) == calCurrent.get(Calendar.YEAR) &&
                calTarget.get(Calendar.MONTH) == calCurrent.get(Calendar.MONTH) &&
                calTarget.get(Calendar.DAY_OF_MONTH) == calCurrent.get(Calendar.DAY_OF_MONTH);
    }


    private static List<String> parseCSVLine(String line) {
        // List to hold the fields extracted from the CSV line
        List<String> fields = new ArrayList<>();
        // StringBuilder to build the current field
        StringBuilder currentField = new StringBuilder();
        // Flag to indicate if the current character is within double quotes
        boolean inQuotes = false;

        // Iterate through each character in the input line
        for (char c : line.toCharArray()) {
            // Check if the character is a double quote
            if (c == '"') {
                // Toggle the inQuotes flag to handle fields within double quotes
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // If the character is a comma and not within double quotes, add the current field to the list and reset the current field
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                // If not a comma, append the character to the current field
                currentField.append(c);
            }
        }

        // Add the last field after the loop ends
        fields.add(currentField.toString());

        // Remove surrounding quotes from fields if present
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            // Check if the field has at least 2 characters and starts and ends with double quotes
            if (field.length() >= 2 && field.charAt(0) == '"' && field.charAt(field.length() - 1) == '"') {
                // Remove the surrounding quotes
                fields.set(i, field.substring(1, field.length() - 1));
            }
        }

        // Return the list of fields extracted from the CSV line
        return fields;
    }




}
