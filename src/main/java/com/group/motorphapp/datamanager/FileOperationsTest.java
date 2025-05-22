package com.group.motorphapp.datamanager;

import com.group.motorphapp.model.Employee;

public class FileOperationsTest {
    public static void main(String[] args) {
        System.out.println("Starting file operations test");

        EmployeeDataManager manager = new EmployeeDataManager();

        // Load all employees
        var employees = manager.getEmployees();
        System.out.println("Loaded " + employees.size() + " employees");

        if (employees.size() > 0) {
            // Try updating the first employee
            Employee firstEmployee = employees.get(0);
            System.out.println("Testing update on employee: " + firstEmployee.getEmployeeNumber());

            // Create updated employee with a small change
            Employee updatedEmployee = new Employee(
                    firstEmployee.getEmployeeNumber(),
                    firstEmployee.getLastName(),
                    firstEmployee.getFirstName(),
                    firstEmployee.getBasicSalary(),
                    firstEmployee.getSssNumber(),
                    firstEmployee.getPhilhealthNumber(),
                    firstEmployee.getPagibigNumber(),
                    firstEmployee.getTinNumber(),
                    firstEmployee.getPosition(),
                    firstEmployee.getRiceSubsidy() + 100, // Increase rice subsidy
                    firstEmployee.getPhoneAllowance(),
                    firstEmployee.getClothingAllowance()
            );

            // Try to update
            boolean updateResult = manager.updateEmployee(updatedEmployee);
            System.out.println("Update result: " + (updateResult ? "SUCCESS" : "FAILED"));

            // Try deleting (if you want to test)
            // boolean deleteResult = manager.deleteEmployee(firstEmployee.getEmployeeNumber());
            // System.out.println("Delete result: " + (deleteResult ? "SUCCESS" : "FAILED"));
        }

        System.out.println("Test completed");
    }
}