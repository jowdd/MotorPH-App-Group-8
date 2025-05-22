package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.group.motorphapp.datamanager.EmployeeDataManager;

public class PayrollSystem {
    private List<Employee> employees;
    private List<TimeLog> timeLogs;
    private EmployeeDataManager dataManager;

    public PayrollSystem() {
        this.employees = new ArrayList<>();
        this.timeLogs = new ArrayList<>();
        this.dataManager = new EmployeeDataManager();

        // Initialize with data from files
        loadDataFromFiles();
    }

    private void loadDataFromFiles() {
        this.employees = dataManager.getEmployees();
        this.timeLogs = dataManager.getTimeLogs();
    }

    // Add new employee
    public boolean addEmployee(Employee employee) {
        // Add to memory
        employees.add(employee);

        // Persist to file
        return dataManager.updateEmployee(employee);
    }

    // Add or replace employee in memory
    public void updateEmployee(String employeeNumber, Employee updatedEmployee) {
        // First, see if the employee exists
        boolean found = false;
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeNumber().equals(employeeNumber)) {
                employees.set(i, updatedEmployee);
                found = true;
                break;
            }
        }

        // If not found, add as new
        if (!found) {
            employees.add(updatedEmployee);
        }

        // Update in file
        dataManager.updateEmployee(updatedEmployee);
    }

    // Delete employee from memory
    public void deleteEmployee(String employeeNumber) {
        employees.removeIf(emp -> emp.getEmployeeNumber().equals(employeeNumber));

        // Also remove associated time logs
        timeLogs.removeIf(log -> log.getEmployeeNumber().equals(employeeNumber));

        // Delete from file
        dataManager.deleteEmployee(employeeNumber);
    }

    /**
     * Finds an employee by their employee number
     * @param employeeNumber The employee number to search for
     * @return The found Employee object, or null if not found
     */
    public Employee findEmployee(String employeeNumber) {
        for (Employee employee : employees) {
            if (employee.getEmployeeNumber().equals(employeeNumber)) {
                return employee;
            }
        }
        return null;
    }

    // Getters and setters
    public List<Employee> getAllEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<TimeLog> getTimeLogs() {
        return timeLogs;
    }

    public void setTimeLogs(List<TimeLog> timeLogs) {
        this.timeLogs = timeLogs;
    }

    // Refresh data from files
    public void refreshData() {
        loadDataFromFiles();
    }

    /**
     * Gets time logs for a specific employee within a date range
     * @param employeeNumber The employee number to find time logs for
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return A list of time logs for the employee within the date range
     */
    public List<TimeLog> getEmployeeTimeLogs(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        List<TimeLog> employeeTimeLogs = new ArrayList<>();

        for (TimeLog log : timeLogs) {
            if (log.getEmployeeNumber().equals(employeeNumber)) {
                LocalDate logDate = log.getDate();
                if ((logDate.isEqual(startDate) || logDate.isAfter(startDate)) &&
                        (logDate.isEqual(endDate) || logDate.isBefore(endDate))) {
                    employeeTimeLogs.add(log);
                }
            }
        }

        return employeeTimeLogs;
    }
}