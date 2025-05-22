package com.group.motorphapp.filereader;

import com.group.motorphapp.model.Employee;
import com.group.motorphapp.model.TimeLog;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    // Updated file paths to match your actual resource files
    private static final String EMPLOYEE_FILE_PATH = "/src/main/java/com/group/motorphapp/Resources/employee-data.tsv";
    private static final String TIME_LOG_FILE_PATH = "/src/main/java/com/group/motorphapp/Resources/attendance-record.csv";

    // File paths for actual file writing (using absolute paths)
    private static final String EMPLOYEE_OUTPUT_PATH = "/src/main/java/com/group/motorphapp/Resources/employee-data.tsv";
    private static final String TIME_LOG_OUTPUT_PATH = "/src/main/java/com/group/motorphapp/Resources/attendance-record.csv";

    public List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();

        try {
            InputStream is = getResourceStream(EMPLOYEE_FILE_PATH);
            if (is == null) {
                System.err.println("Error: Could not find resource " + EMPLOYEE_FILE_PATH);
                return employees; // Return empty list
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Skip header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");

                String employeeNumber = fields[0];
                String lastName = fields[1];
                String firstName = fields[2];
                // Skip fields 3-5 (birthdate, address, phone)
                String sssNumber = fields[6];
                String philhealthNumber = fields[7];
                String tinNumber = fields[8]; // TIN is at index 8
                String pagibigNumber = fields[9];
                // fields[10] is employment status
                String position = fields[11];
                // Skip immediate supervisor field[12]
                double basicSalary = Double.parseDouble(fields[13].replace(",", ""));
                double riceSubsidy = Double.parseDouble(fields[14].replace(",", ""));
                double phoneAllowance = Double.parseDouble(fields[15].replace(",", ""));
                double clothingAllowance = Double.parseDouble(fields[16].replace(",", ""));

                Employee employee = new Employee(
                        employeeNumber, lastName, firstName, basicSalary,
                        sssNumber, philhealthNumber, pagibigNumber,
                        tinNumber, position, riceSubsidy, phoneAllowance, clothingAllowance
                );

                employees.add(employee);
            }

            reader.close();

        } catch (IOException e) {
            System.err.println("Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public List<TimeLog> loadTimeLogs() {
        List<TimeLog> timeLogs = new ArrayList<>();

        try {
            InputStream is = getResourceStream(TIME_LOG_FILE_PATH);
            if (is == null) {
                System.err.println("Error: Could not find resource " + TIME_LOG_FILE_PATH);
                return timeLogs; // Return empty list
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Skip header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");  // Changed to comma separator for CSV format

                String employeeNumber = fields[0];
                LocalDate date = LocalDate.parse(fields[3], DateTimeFormatter.ofPattern("MM/dd/yyyy"));  // Date is now in field 3

                // Parse time in and time out
                LocalTime timeIn = null;
                if (fields.length > 4 && !fields[4].isEmpty()) {
                    timeIn = LocalTime.parse(fields[4], DateTimeFormatter.ofPattern("H:mm"));
                }

                LocalTime timeOut = null;
                if (fields.length > 5 && !fields[5].isEmpty()) {
                    timeOut = LocalTime.parse(fields[5], DateTimeFormatter.ofPattern("H:mm"));
                }

                // Create TimeLog with the correct parameter types
                TimeLog timeLog = new TimeLog(employeeNumber, date, timeIn, timeOut);
                timeLogs.add(timeLog);
            }

            reader.close();

        } catch (IOException e) {
            System.err.println("Error loading time log data: " + e.getMessage());
            e.printStackTrace();
        }

        return timeLogs;
    }

    /**
     * Saves a new employee record to the employee data file
     * @param employee The employee to save
     * @return true if successful, false otherwise
     */
    public boolean saveEmployee(Employee employee) {
        try {
            // First, read the entire file to get the header and existing data
            List<String> lines = new ArrayList<>();
            String header = "";

            try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEE_OUTPUT_PATH))) {
                header = reader.readLine(); // Save the header
                lines.add(header);

                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            // Format the new employee data
            String newEmployeeData = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%,.0f\t%,.0f\t%,.0f\t%,.0f\t%,.0f\t%.2f",
                    employee.getEmployeeNumber(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    "", // Birthday placeholder
                    "", // Address placeholder
                    "", // Phone placeholder
                    employee.getSssNumber(),
                    employee.getPhilhealthNumber(),
                    employee.getTinNumber(),
                    employee.getPagibigNumber(),
                    "Regular", // Status placeholder
                    employee.getPosition(),
                    "", // Supervisor placeholder
                    employee.getBasicSalary(),
                    employee.getRiceSubsidy(),
                    employee.getPhoneAllowance(),
                    employee.getClothingAllowance(),
                    employee.getBasicSalary() / 2, // Semi-monthly rate
                    employee.getBasicSalary() / 168 // Hourly rate (assuming 168 work hours per month)
            );

            lines.add(newEmployeeData);

            // Write all lines back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(EMPLOYEE_OUTPUT_PATH))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Employee record saved successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("Error saving employee data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves a new time log record to the attendance file
     * @param timeLog The time log to save
     * @param employeeLastName The employee's last name
     * @param employeeFirstName The employee's first name
     * @return true if successful, false otherwise
     */
    public boolean saveTimeLog(TimeLog timeLog, String employeeLastName, String employeeFirstName) {
        try {
            // First, read the entire file to get the header and existing data
            List<String> lines = new ArrayList<>();
            String header = "";

            try (BufferedReader reader = new BufferedReader(new FileReader(TIME_LOG_OUTPUT_PATH))) {
                header = reader.readLine(); // Save the header
                lines.add(header);

                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            // Format date and times
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

            LocalDate date = timeLog.getDate();
            LocalTime timeIn = timeLog.getTimeIn();
            LocalTime timeOut = timeLog.getTimeOut();

            String formattedDate = date != null ? date.format(dateFormatter) : "";
            String formattedTimeIn = timeIn != null ? timeIn.format(timeFormatter) : "";
            String formattedTimeOut = timeOut != null ? timeOut.format(timeFormatter) : "";

            // Format the new time log data
            String newTimeLogData = String.format("%s,%s,%s,%s,%s,%s",
                    timeLog.getEmployeeNumber(),
                    employeeLastName,
                    employeeFirstName,
                    formattedDate,
                    formattedTimeIn,
                    formattedTimeOut
            );

            lines.add(newTimeLogData);

            // Write all lines back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TIME_LOG_OUTPUT_PATH))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Time log record saved successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("Error saving time log data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing employee record in the file
     * @param updatedEmployee The updated employee information
     * @return true if successful, false otherwise
     */
    public boolean updateEmployee(Employee updatedEmployee) {
        try {
            // Read the entire file
            List<String> lines = new ArrayList<>();
            String header = "";
            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEE_OUTPUT_PATH))) {
                header = reader.readLine(); // Save the header
                lines.add(header);

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\t");
                    String employeeNumber = fields[0];

                    if (employeeNumber.equals(updatedEmployee.getEmployeeNumber())) {
                        // Replace this line with updated employee data
                        String updatedData = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%,.0f\t%,.0f\t%,.0f\t%,.0f\t%,.0f\t%.2f",
                                updatedEmployee.getEmployeeNumber(),
                                updatedEmployee.getLastName(),
                                updatedEmployee.getFirstName(),
                                fields[3], // Preserve existing birthday
                                fields[4], // Preserve existing address
                                fields[5], // Preserve existing phone
                                updatedEmployee.getSssNumber(),
                                updatedEmployee.getPhilhealthNumber(),
                                updatedEmployee.getTinNumber(),
                                updatedEmployee.getPagibigNumber(),
                                fields[10], // Preserve existing status
                                updatedEmployee.getPosition(),
                                fields[12], // Preserve existing supervisor
                                updatedEmployee.getBasicSalary(),
                                updatedEmployee.getRiceSubsidy(),
                                updatedEmployee.getPhoneAllowance(),
                                updatedEmployee.getClothingAllowance(),
                                updatedEmployee.getBasicSalary() / 2, // Semi-monthly rate
                                updatedEmployee.getBasicSalary() / 168 // Hourly rate
                        );
                        lines.add(updatedData);
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }

            if (!found) {
                System.err.println("Employee not found. Cannot update.");
                return false;
            }

            // Write all lines back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(EMPLOYEE_OUTPUT_PATH))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Employee record updated successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("Error updating employee data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to try different approaches to loading resources
    private InputStream getResourceStream(String resourcePath) {
        // Try with original path
        InputStream is = getClass().getResourceAsStream(resourcePath);

        if (is == null) {
            // Try without leading slash
            String pathWithoutSlash = resourcePath.startsWith("/") ?
                    resourcePath.substring(1) : resourcePath;
            is = getClass().getResourceAsStream(pathWithoutSlash);
        }

        if (is == null) {
            // Try with class loader
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        }

        if (is == null) {
            // Try with class loader and no leading slash
            String pathWithoutSlash = resourcePath.startsWith("/") ?
                    resourcePath.substring(1) : resourcePath;
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathWithoutSlash);
        }

        if (is == null) {
            // Try as a file in the project directory
            try {
                // Try with src prefix and leading slash removed for file path
                String filePath = "src" + (resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath);
                File file = new File(filePath);
                if (file.exists()) {
                    return new FileInputStream(file);
                }
            } catch (FileNotFoundException e) {
                // Ignore and continue trying
            }
        }

        return is;
    }
}