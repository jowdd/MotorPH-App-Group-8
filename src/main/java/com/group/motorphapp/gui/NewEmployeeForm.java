package com.group.motorphapp.gui;

import model.Employee;
import model.PayrollSystem;
import com.group.motorphapp.filereader.EmployeeWriter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class NewEmployeeForm extends JFrame {
    private EmployeeListView parentFrame;
    private PayrollSystem payrollSystem;

    private JTextField employeeNumberField;
    private JTextField lastNameField;
    private JTextField firstNameField;
    private JTextField positionField;
    private JTextField basicSalaryField;
    private JTextField sssNumberField;
    private JTextField philhealthNumberField;
    private JTextField tinNumberField;
    private JTextField pagibigNumberField;
    private JTextField riceSubsidyField;
    private JTextField phoneAllowanceField;
    private JTextField clothingAllowanceField;

    public NewEmployeeForm(EmployeeListView parentFrame, PayrollSystem payrollSystem) {
        this.parentFrame = parentFrame;
        this.payrollSystem = payrollSystem;

        setTitle("Add New Employee");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        // Create main panel with scrolling capability
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add form fields
        formPanel.add(new JLabel("Employee Number:"));
        employeeNumberField = new JTextField();
        formPanel.add(employeeNumberField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Position:"));
        positionField = new JTextField();
        formPanel.add(positionField);

        formPanel.add(new JLabel("Basic Salary:"));
        basicSalaryField = new JTextField();
        formPanel.add(basicSalaryField);

        formPanel.add(new JLabel("SSS Number:"));
        sssNumberField = new JTextField();
        formPanel.add(sssNumberField);

        formPanel.add(new JLabel("PhilHealth Number:"));
        philhealthNumberField = new JTextField();
        formPanel.add(philhealthNumberField);

        formPanel.add(new JLabel("TIN:"));
        tinNumberField = new JTextField();
        formPanel.add(tinNumberField);

        formPanel.add(new JLabel("Pag-IBIG Number:"));
        pagibigNumberField = new JTextField();
        formPanel.add(pagibigNumberField);

        formPanel.add(new JLabel("Rice Subsidy:"));
        riceSubsidyField = new JTextField();
        formPanel.add(riceSubsidyField);

        formPanel.add(new JLabel("Phone Allowance:"));
        phoneAllowanceField = new JTextField();
        formPanel.add(phoneAllowanceField);

        formPanel.add(new JLabel("Clothing Allowance:"));
        clothingAllowanceField = new JTextField();
        formPanel.add(clothingAllowanceField);

        // Add form panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Employee");
        saveButton.addActionListener(e -> saveEmployee());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        getContentPane().add(mainPanel);
    }

    private void saveEmployee() {
        try {
            // Validate input
            if (employeeNumberField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                    firstNameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee Number, Last Name, and First Name are required fields.");
                return;
            }

            // Parse numeric values
            double basicSalary = Double.parseDouble(basicSalaryField.getText());
            double riceSubsidy = Double.parseDouble(riceSubsidyField.getText());
            double phoneAllowance = Double.parseDouble(phoneAllowanceField.getText());
            double clothingAllowance = Double.parseDouble(clothingAllowanceField.getText());

            // Create new employee with TIN - match the order with Employee constructor
            Employee newEmployee = new Employee(
                    employeeNumberField.getText(),          // String employeeNumber
                    lastNameField.getText(),                // String lastName
                    firstNameField.getText(),               // String firstName
                    basicSalary,                            // double basicSalary
                    sssNumberField.getText(),               // String sssNumber
                    philhealthNumberField.getText(),        // String philhealthNumber
                    pagibigNumberField.getText(),           // String pagibigNumber
                    tinNumberField.getText(),               // String tinNumber - NOTE THIS POSITION
                    positionField.getText(),                // String position
                    riceSubsidy,                            // double riceSubsidy
                    phoneAllowance,                         // double phoneAllowance
                    clothingAllowance                       // double clothingAllowance
            );

            // Add to payroll system
            payrollSystem.addEmployee(newEmployee);

            // Save to CSV file
            EmployeeWriter writer = new EmployeeWriter();
            writer.appendEmployeeToFile(newEmployee);

            // Refresh employee list
            parentFrame.refreshEmployeeTable();

            // Close form
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for salary and allowances.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving employee to file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving employee: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
