package com.group.motorphapp.datamanager;

import model.Employee;
import model.TimeLog;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDataManager {
    // Define constants for file paths using absolute paths
    private static final String USER_DIR = System.getProperty("user.dir");
    public static final String EMPLOYEE_DATA_FILE = USER_DIR + "/src/main/java/com/group/motorphapp/Resources/employee-data.tsv";
    public static final String TIME_LOG_FILE = USER_DIR + "/src/main/java/com/group/motorphapp/Resources/attendance-record.csv";

    public EmployeeDataManager() {
        // Check if files exist when manager is created
        checkFilesExist();
    }

    // Method to check if necessary files exist
    private void checkFilesExist() {
        File employeeFile = new File(EMPLOYEE_DATA_FILE);
        File timeLogFile = new File(TIME_LOG_FILE);

        System.out.println("Checking file paths:");
        System.out.println("Employee data file path: " + employeeFile.getAbsolutePath());
        System.out.println("Time log file path: " + timeLogFile.getAbsolutePath());

        if (!employeeFile.exists()) {
            System.err.println("WARNING: Employee data file not found!");
        } else {
            System.out.println("Employee data file exists and is " +
                    (employeeFile.canWrite() ? "writable" : "not writable"));
        }

        if (!timeLogFile.exists()) {
            System.err.println("WARNING: Time log file not found!");
        } else {
            System.out.println("Time log file exists and is " +
                    (timeLogFile.canWrite() ? "writable" : "not writable"));
        }
    }

    // Create backup of file before modifying
    private void backupFile(String filePath) {
        try {
            File originalFile = new File(filePath);
            File backupFile = new File(filePath + ".bak");

            Files.copy(originalFile.toPath(), backupFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Backup created: " + backupFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to create backup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get all employees from the file
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEE_DATA_FILE))) {
            // Skip header line
            String line = reader.readLine();

            // Read data lines
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                // Print the row for debugging
                System.out.println("Processing row: " + line);
                
                if (data.length >= 19) { // Adjust to match the actual TSV format
                    try {
                        String employeeNumber = data[0];
                        String lastName = data[1];
                        String firstName = data[2];
                        // Skip birthdate at index 3
                        // Skip address at index 4
                        // Skip phone at index 5
                        String sssNumber = data[6];
                        String philhealthNumber = data[7];
                        String pagibigNumber = data[8];
                        String tinNumber = data[9];
                        // Skip employmentStatus at index 10
                        String position = data[11];
                        // Skip supervisor at index 12
                        
                        // Parse salary information
                        double basicSalary = parseMoneyValue(data[13]);
                        double riceSubsidy = parseMoneyValue(data[14]);
                        double phoneAllowance = parseMoneyValue(data[15]);
                        double clothingAllowance = parseMoneyValue(data[16]);
                        
                        Employee employee = new Employee(
                                employeeNumber,
                                lastName,
                                firstName,
                                basicSalary,
                                sssNumber,
                                philhealthNumber,
                                pagibigNumber,
                                tinNumber,
                                position,
                                riceSubsidy,
                                phoneAllowance,
                                clothingAllowance
                        );
                        employees.add(employee);
                    } catch (Exception e) {
                        System.err.println("Error processing employee data row: " + e.getMessage());
                        e.printStackTrace();
                        // Continue processing other rows instead of crashing
                    }
                } else {
                    System.err.println("Row has insufficient data: " + data.length + " columns, expected at least 19");
                }
            }
            System.out.println("Successfully loaded " + employees.size() + " employees from file");
        } catch (IOException e) {
            System.err.println("Error reading employee data: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }
    
    // Helper method to parse money values that might contain commas
    private double parseMoneyValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        // Remove commas and other non-numeric characters except for decimal point
        String cleanValue = value.replace(",", "").trim();
        try {
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing money value: " + value);
            return 0.0;
        }
    }

    // Get a specific employee by number
    public Employee getEmployee(String employeeNumber) {
        List<Employee> employees = getEmployees();
        for (Employee employee : employees) {
            if (employee.getEmployeeNumber().equals(employeeNumber)) {
                return employee;
            }
        }
        return null;
    }

    // Get all time logs from the file
    public List<TimeLog> getTimeLogs() {
        List<TimeLog> timeLogs = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // First check if file exists
        File timeLogFile = new File(TIME_LOG_FILE);
        if (!timeLogFile.exists() || !timeLogFile.canRead()) {
            System.err.println("Time log file does not exist or cannot be read: " + TIME_LOG_FILE);
            return timeLogs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TIME_LOG_FILE))) {
            // Skip header line
            String line = reader.readLine();
            if (line == null) {
                System.out.println("Time log file is empty or has no header");
                return timeLogs;
            }

            // Read data lines
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length >= 4) {
                        String employeeNumber = data[0];
                        LocalDate date = LocalDate.parse(data[1], dateFormatter);

                        // Parse time entries (some might be empty)
                        LocalTime timeIn = data[2].isEmpty() ? null : LocalTime.parse(data[2]);
                        LocalTime timeOut = data[3].isEmpty() ? null : LocalTime.parse(data[3]);

                        TimeLog timeLog = new TimeLog(employeeNumber, date, timeIn, timeOut);
                        timeLogs.add(timeLog);
                    } else {
                        System.err.println("Invalid time log format: " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing time log line: " + line + " - " + e.getMessage());
                    // Continue processing other lines
                }
            }
            System.out.println("Successfully loaded " + timeLogs.size() + " time logs from file");
        } catch (IOException e) {
            System.err.println("Error reading time log data: " + e.getMessage());
            e.printStackTrace();
        }

        return timeLogs;
    }

    // Get refreshed employee list from file
    public List<Employee> getRefreshedEmployees() {
        return getEmployees();
    }

    // Get refreshed time log list from file
    public List<TimeLog> getRefreshedTimeLogs() {
        return getTimeLogs();
    }

    // Add a new employee
    public boolean addEmployee(Employee newEmployee) {
        try {
            System.out.println("Attempting to add employee: " + newEmployee.getEmployeeNumber());

            // First, create a backup
            backupFile(EMPLOYEE_DATA_FILE);

            // Read all employees
            List<Employee> allEmployees = getEmployees();

            // Check if employee already exists
            for (Employee employee : allEmployees) {
                if (employee.getEmployeeNumber().equals(newEmployee.getEmployeeNumber())) {
                    System.err.println("Employee already exists: " + newEmployee.getEmployeeNumber());
                    return false;
                }
            }

            // Add the new employee
            allEmployees.add(newEmployee);

            // Write all employees back to file
            return writeEmployeesToFile(allEmployees);

        } catch (Exception e) {
            System.err.println("Error adding employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update employee data in the file
    public boolean updateEmployee(Employee updatedEmployee) {
        try {
            System.out.println("Attempting to update employee: " + updatedEmployee.getEmployeeNumber());

            // First, create a backup
            backupFile(EMPLOYEE_DATA_FILE);

            // Read all employees
            List<Employee> allEmployees = getEmployees();
            boolean found = false;

            // Replace the employee to update
            for (int i = 0; i < allEmployees.size(); i++) {
                if (allEmployees.get(i).getEmployeeNumber().equals(updatedEmployee.getEmployeeNumber())) {
                    allEmployees.set(i, updatedEmployee);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // If employee not found, add them as new
                allEmployees.add(updatedEmployee);
                System.out.println("Employee not found for update, adding as new: " + updatedEmployee.getEmployeeNumber());
            }

            // Write all employees back to file
            return writeEmployeesToFile(allEmployees);

        } catch (Exception e) {
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete employee from the file
    public boolean deleteEmployee(String employeeNumber) {
        try {
            System.out.println("Attempting to delete employee: " + employeeNumber);

            // First, create backups
            backupFile(EMPLOYEE_DATA_FILE);
            backupFile(TIME_LOG_FILE);

            // Read all employees
            List<Employee> allEmployees = getEmployees();
            boolean found = false;

            // Remove the employee
            for (int i = 0; i < allEmployees.size(); i++) {
                if (allEmployees.get(i).getEmployeeNumber().equals(employeeNumber)) {
                    allEmployees.remove(i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.err.println("Employee not found for deletion: " + employeeNumber);
                return false;
            }

            // Also delete associated time logs
            boolean timeLogsDeleted = deleteEmployeeTimeLogs(employeeNumber);

            // Write all employees back to file
            boolean employeesUpdated = writeEmployeesToFile(allEmployees);

            return employeesUpdated && timeLogsDeleted;

        } catch (Exception e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete time logs for an employee
    private boolean deleteEmployeeTimeLogs(String employeeNumber) {
        try {
            // Read all time logs
            List<TimeLog> allTimeLogs = getTimeLogs();
            List<TimeLog> updatedTimeLogs = new ArrayList<>();

            // Filter out the employee's time logs
            for (TimeLog timeLog : allTimeLogs) {
                if (!timeLog.getEmployeeNumber().equals(employeeNumber)) {
                    updatedTimeLogs.add(timeLog);
                }
            }

            System.out.println("Removed " + (allTimeLogs.size() - updatedTimeLogs.size()) +
                    " time logs for employee " + employeeNumber);

            // Write updated time logs back to file
            return writeTimeLogsToFile(updatedTimeLogs);

        } catch (Exception e) {
            System.err.println("Error deleting employee time logs: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Write employees to file
    private boolean writeEmployeesToFile(List<Employee> employees) {
        try {
            // Create a temporary file
            Path tempFile = Files.createTempFile("employee-data", ".tmp");
            System.out.println("Created temporary file: " + tempFile.toString());

            // Write header and data to temp file
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                // Write header
                writer.write("Employee #\tLast Name\tFirst Name\tPosition\tSSS #\tPhilhealth #\tPag-ibig #\tTIN #\tBasic Salary\tRice Subsidy\tPhone Allowance\tClothing Allowance");
                writer.newLine();

                // Write employee data
                for (Employee emp : employees) {
                    writer.write(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%.2f\t%.2f\t%.2f\t%.2f",
                            emp.getEmployeeNumber(),
                            emp.getLastName(),
                            emp.getFirstName(),
                            emp.getPosition(),
                            emp.getSssNumber(),
                            emp.getPhilhealthNumber(),
                            emp.getPagibigNumber(),
                            emp.getTinNumber(),
                            emp.getBasicSalary(),
                            emp.getRiceSubsidy(),
                            emp.getPhoneAllowance(),
                            emp.getClothingAllowance()));
                    writer.newLine();
                }
            }

            // Replace the original file with the temp file
            Path targetPath = Paths.get(EMPLOYEE_DATA_FILE);
            Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Successfully wrote " + employees.size() + " employees to file");

            return true;
        } catch (IOException e) {
            System.err.println("Error writing employees to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Write time logs to file
    private boolean writeTimeLogsToFile(List<TimeLog> timeLogs) {
        try {
            // Create a temporary file
            Path tempFile = Files.createTempFile("time-logs", ".tmp");
            System.out.println("Created temporary file: " + tempFile.toString());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // Write header and data to temp file
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                // Write header
                writer.write("Employee Number,Date,Time-in,Time-out");
                writer.newLine();

                // Write time log data
                for (TimeLog log : timeLogs) {
                    String timeIn = log.getTimeIn() != null ? log.getTimeIn().toString() : "";
                    String timeOut = log.getTimeOut() != null ? log.getTimeOut().toString() : "";

                    writer.write(String.format("%s,%s,%s,%s",
                            log.getEmployeeNumber(),
                            log.getDate().format(dateFormatter),
                            timeIn,
                            timeOut));
                    writer.newLine();
                }
            }

            // Replace the original file with the temp file
            Path targetPath = Paths.get(TIME_LOG_FILE);
            Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Successfully wrote " + timeLogs.size() + " time logs to file");

            return true;
        } catch (IOException e) {
            System.err.println("Error writing time logs to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}