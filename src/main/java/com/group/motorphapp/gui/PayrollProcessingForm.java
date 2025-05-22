package com.group.motorphapp.gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// Import your existing classes
import model.Employee;
import model.TimeLog;
import model.PayrollSystem;
import model.PayrollCalculator;

public class PayrollProcessingForm extends JFrame {
    private JTextField employeeNumberField;
    private JTextField payPeriodStartField;
    private JTextField payPeriodEndField;
    private JTextArea resultArea;
    private JButton processButton;
    private JButton clearButton;

    private PayrollSystem payrollSystem;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    public PayrollProcessingForm() {
        setTitle("MotorPH Payroll Processing System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
        addListeners();
    }

    public void setPayrollSystem(PayrollSystem payrollSystem) {
        this.payrollSystem = payrollSystem;
    }

    private void initializeComponents() {
        employeeNumberField = new JTextField(10);
        payPeriodStartField = new JTextField(10);
        payPeriodEndField = new JTextField(10);
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        processButton = new JButton("Process Payslip");
        clearButton = new JButton("Clear");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Employee Number
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Employee Number:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(employeeNumberField, gbc);

        // Pay Period Start
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Pay Period Start (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(payPeriodStartField, gbc);

        // Pay Period End
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Pay Period End (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(payPeriodEndField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(processButton);
        buttonPanel.add(clearButton);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        // Result Area
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add panels to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        processButton.addActionListener(e -> processPayroll());
        clearButton.addActionListener(e -> clearFields());
    }

    private void processPayroll() {
        try {
            // Validate employee number
            String empNumber = employeeNumberField.getText().trim();
            if (empNumber.isEmpty()) {
                throw new IllegalArgumentException("Employee number is required.");
            }

            // Validate dates
            LocalDate startDate = validateDate(payPeriodStartField.getText().trim());
            LocalDate endDate = validateDate(payPeriodEndField.getText().trim());

            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date cannot be before start date.");
            }

            // Get employee
            Employee employee = payrollSystem.findEmployee(empNumber);
            if (employee == null) {
                throw new IllegalArgumentException("Employee not found.");
            }

            // Process payroll and generate payslip
            generatePayslip(employee, startDate, endDate);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD format.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate validateDate(String dateStr) throws DateTimeParseException {
        if (dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date fields are required.");
        }
        return LocalDate.parse(dateStr, dateFormatter);
    }

    private void generatePayslip(Employee employee, LocalDate startDate, LocalDate endDate) {
        PayrollCalculator calculator = new PayrollCalculator();
        List<TimeLog> timeLogs = payrollSystem.getEmployeeTimeLogs(employee.getEmployeeNumber(), startDate, endDate);

        // Calculate pay and deductions
        double grossPay = calculator.calculateGrossPay(employee, timeLogs);

        // Calculate allowances - assuming you have these methods or attributes
        double riceSubsidy = employee.getRiceSubsidy();
        double phoneAllowance = employee.getPhoneAllowance();
        double clothingAllowance = employee.getClothingAllowance();
        double totalAllowances = riceSubsidy + phoneAllowance + clothingAllowance;

        // Add allowances to gross pay
        double totalGrossPay = grossPay + totalAllowances;

        // Calculate deductions
        double sssDeduction = calculator.calculateSSSContribution(grossPay);
        double philHealthDeduction = calculator.calculatePhilhealthContribution(grossPay);
        double pagIbigDeduction = calculator.calculatePagibigContribution(grossPay);
        double totalDeductions = sssDeduction + philHealthDeduction + pagIbigDeduction;
        double taxableIncome = grossPay - totalDeductions;
        double withholdingTax = calculator.calculateWithholdingTax(taxableIncome);
        double netPay = totalGrossPay - (totalDeductions + withholdingTax);

        // Calculate weekly values (divide by ~4.33 weeks in a month)
        double weeklyGrossPay = grossPay / 4.33;
        double weeklyAllowances = totalAllowances / 4.33;
        double weeklyDeductions = totalDeductions / 4.33;
        double weeklyTax = withholdingTax / 4.33;
        double weeklyNetPay = netPay / 4.33;

        // Format the payslip with proper spacing
        StringBuilder payslip = new StringBuilder();
        payslip.append("=====================================\n");
        payslip.append("           MOTORPH PAYSLIP          \n");
        payslip.append("=====================================\n\n");

        // Employee Information
        payslip.append(String.format("Employee Number: %s\n", employee.getEmployeeNumber()));
        payslip.append(String.format("Employee Name: %s, %s\n",
                employee.getLastName(), employee.getFirstName()));
        payslip.append(String.format("Position: %s\n", employee.getPosition()));
        payslip.append(String.format("SSS Number: %s\n", employee.getSssNumber()));
        payslip.append(String.format("PhilHealth Number: %s\n", employee.getPhilhealthNumber()));
        payslip.append(String.format("Pag-IBIG Number: %s\n", employee.getPagibigNumber()));
        payslip.append(String.format("Pay Period: %s to %s\n\n",
                startDate.format(dateFormatter),
                endDate.format(dateFormatter)));

        // Monthly Earnings Section
        payslip.append("MONTHLY EARNINGS:\n");
        payslip.append(String.format("Basic Pay:          %s\n", formatCurrency(grossPay)));
        payslip.append("Allowances:\n");
        payslip.append(String.format("  Rice Subsidy:     %s\n", formatCurrency(riceSubsidy)));
        payslip.append(String.format("  Phone Allowance:  %s\n", formatCurrency(phoneAllowance)));
        payslip.append(String.format("  Clothing Allow.:  %s\n", formatCurrency(clothingAllowance)));
        payslip.append(String.format("Total Allowances:   %s\n", formatCurrency(totalAllowances)));
        payslip.append(String.format("Gross Pay:          %s\n\n", formatCurrency(totalGrossPay)));

        // Weekly Earnings Section
        payslip.append("WEEKLY EARNINGS:\n");
        payslip.append(String.format("Basic Pay:          %s\n", formatCurrency(weeklyGrossPay)));
        payslip.append(String.format("Total Allowances:   %s\n", formatCurrency(weeklyAllowances)));
        payslip.append(String.format("Gross Pay:          %s\n\n", formatCurrency(weeklyGrossPay + weeklyAllowances)));

        // Monthly Deductions Section
        payslip.append("MONTHLY DEDUCTIONS:\n");
        payslip.append(String.format("SSS:                %s\n", formatCurrency(sssDeduction)));
        payslip.append(String.format("PhilHealth:         %s\n", formatCurrency(philHealthDeduction)));
        payslip.append(String.format("Pag-IBIG:           %s\n", formatCurrency(pagIbigDeduction)));
        payslip.append(String.format("Withholding Tax:    %s\n", formatCurrency(withholdingTax)));
        payslip.append(String.format("Total Deductions:   %s\n\n",
                formatCurrency(totalDeductions + withholdingTax)));

        // Weekly Deductions Section
        payslip.append("WEEKLY DEDUCTIONS:\n");
        payslip.append(String.format("Total Deductions:   %s\n\n", formatCurrency(weeklyDeductions + weeklyTax)));

        // Net Pay Section
        payslip.append("=====================================\n");
        payslip.append(String.format("MONTHLY NET PAY:    %s\n", formatCurrency(netPay)));
        payslip.append(String.format("WEEKLY NET PAY:     %s\n", formatCurrency(weeklyNetPay)));
        payslip.append("=====================================\n");

        // Display the payslip
        resultArea.setText(payslip.toString());
    }
    private String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    private void clearFields() {
        employeeNumberField.setText("");
        payPeriodStartField.setText("");
        payPeriodEndField.setText("");
        resultArea.setText("");
    }
}