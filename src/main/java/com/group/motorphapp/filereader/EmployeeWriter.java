package com.group.motorphapp.filereader;

import model.Employee;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class EmployeeWriter {
    private static final String EMPLOYEE_FILE_PATH = "src/main/java/com/group/motorphapp/Resources/employee-data.tsv";

    public void appendEmployeeToFile(Employee employee) throws IOException {
        // Get the file path
        URL resourceUrl = getClass().getResource(EMPLOYEE_FILE_PATH);
        if (resourceUrl == null) {
            throw new IOException("Cannot find resource file: " + EMPLOYEE_FILE_PATH);
        }

        String filePath = resourceUrl.getPath();
        File file = new File(filePath);

        // Ensure the file exists
        if (!file.exists()) {
            throw new IOException("Employee data file not found: " + filePath);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            // Format employee data as TSV line
            // Note: This simplified version only includes the fields we collect
            // Add placeholders for birth date, address, phone, etc.
            String line = String.format("%s\t%s\t%s\t\t\t\t%s\t%s\t%s\t%s\tRegular\t%s\tN/A\t%.2f\t%.2f\t%.2f\t%.2f",
                    employee.getEmployeeNumber(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    employee.getSssNumber(),
                    employee.getPhilhealthNumber(),
                    employee.getTinNumber(),
                    employee.getPagibigNumber(),
                    employee.getPosition(),
                    employee.getBasicSalary(),
                    employee.getRiceSubsidy(),
                    employee.getPhoneAllowance(),
                    employee.getClothingAllowance());

            writer.println(line);
        }
    }
}