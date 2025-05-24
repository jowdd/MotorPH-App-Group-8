package com.group.motorphapp.gui;

import com.group.motorphapp.model.Employee;
import com.group.motorphapp.model.PayrollSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class EmployeeDetailView extends JFrame {
    private Employee employee;
    private PayrollSystem payrollSystem;
    private JComboBox<String> monthSelector;
    private JComboBox<Integer> yearSelector;
    private JPanel employeeDetailsPanel;
    private JPanel salaryDetailsPanel;

    public EmployeeDetailView(Employee employee, PayrollSystem payrollSystem) {
        this.employee = employee;
        this.payrollSystem = payrollSystem;

        setTitle("Employee Details: " + employee.getFirstName() + " " + employee.getLastName());
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with scrolling capability
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create employee details panel
        employeeDetailsPanel = createEmployeeDetailsPanel();
        employeeDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create month selection panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBorder(BorderFactory.createTitledBorder("Select Period for Salary Computation"));
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add month label and dropdown
        datePanel.add(new JLabel("Month:"));

        // Create month dropdown
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Month.of(i + 1).getDisplayName(TextStyle.FULL, Locale.getDefault());
        }
        monthSelector = new JComboBox<>(months);
        datePanel.add(monthSelector);

        // Add year label and dropdown
        datePanel.add(new JLabel("Year:"));

        // Create year dropdown (current year and previous 5 years)
        Integer[] years = new Integer[6];
        int currentYear = YearMonth.now().getYear();
        for (int i = 0; i < years.length; i++) {
            years[i] = currentYear - i;
        }
        yearSelector = new JComboBox<>(years);
        datePanel.add(yearSelector);

        // Create compute button
        JButton computeButton = new JButton("Compute Salary");
        computeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                computeSalary();
            }
        });
        datePanel.add(computeButton);

        // Create salary details panel (initially empty)
        salaryDetailsPanel = new JPanel();
        salaryDetailsPanel.setLayout(new BoxLayout(salaryDetailsPanel, BoxLayout.Y_AXIS));
        salaryDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        salaryDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add panels to main panel
        mainPanel.add(employeeDetailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(datePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(salaryDetailsPanel);

        // Add main panel to scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add scroll pane to frame
        getContentPane().add(scrollPane);
    }

    private JPanel createEmployeeDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee Information"));

        // Add employee details
        panel.add(new JLabel("Employee Number:"));
        panel.add(new JLabel(employee.getEmployeeNumber()));

        panel.add(new JLabel("Name:"));
        panel.add(new JLabel(employee.getFirstName() + " " + employee.getLastName()));

        panel.add(new JLabel("Position:"));
        panel.add(new JLabel(employee.getPosition()));

        panel.add(new JLabel("Basic Salary:"));
        panel.add(new JLabel(String.format("₱%.2f", employee.getBasicSalary())));

        panel.add(new JLabel("SSS Number:"));
        panel.add(new JLabel(employee.getSssNumber()));

        panel.add(new JLabel("PhilHealth Number:"));
        panel.add(new JLabel(employee.getPhilhealthNumber()));

        panel.add(new JLabel("TIN:"));
        panel.add(new JLabel(employee.getTinNumber()));

        panel.add(new JLabel("Pag-IBIG Number:"));
        panel.add(new JLabel(employee.getPagibigNumber()));

        panel.add(new JLabel("Rice Subsidy:"));
        panel.add(new JLabel(String.format("₱%.2f", employee.getRiceSubsidy())));

        panel.add(new JLabel("Phone Allowance:"));
        panel.add(new JLabel(String.format("₱%.2f", employee.getPhoneAllowance())));

        panel.add(new JLabel("Clothing Allowance:"));
        panel.add(new JLabel(String.format("₱%.2f", employee.getClothingAllowance())));

        return panel;
    }

    private void computeSalary() {
        // Get selected month and year
        int selectedMonth = monthSelector.getSelectedIndex() + 1;
        int selectedYear = (Integer) yearSelector.getSelectedItem();

        // Check if there are time logs for this employee in the selected month/year
        java.time.LocalDate startDate = java.time.LocalDate.of(selectedYear, selectedMonth, 1);
        java.time.LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        java.util.List<com.group.motorphapp.model.TimeLog> logs = payrollSystem.getEmployeeTimeLogs(employee.getEmployeeNumber(), startDate, endDate);
        if (logs == null || logs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No records found for the selected month/year.", "No Records", JOptionPane.WARNING_MESSAGE);
            salaryDetailsPanel.removeAll();
            salaryDetailsPanel.revalidate();
            salaryDetailsPanel.repaint();
            return;
        }

        // Clear previous salary details
        salaryDetailsPanel.removeAll();
        salaryDetailsPanel.setBorder(BorderFactory.createTitledBorder(
                "Salary Details for " + monthSelector.getSelectedItem() + " " + selectedYear));

        // Create a panel with salary details using GridLayout
        JPanel detailsGrid = new JPanel(new GridLayout(0, 2, 10, 5));

        // Calculate gross salary
        double basicSalary = employee.getBasicSalary();
        double riceSubsidy = employee.getRiceSubsidy();
        double phoneAllowance = employee.getPhoneAllowance();
        double clothingAllowance = employee.getClothingAllowance();
        double grossSalary = basicSalary + riceSubsidy + phoneAllowance + clothingAllowance;

        // Add salary components
        detailsGrid.add(new JLabel("Basic Salary:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", basicSalary)));

        detailsGrid.add(new JLabel("Rice Subsidy:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", riceSubsidy)));

        detailsGrid.add(new JLabel("Phone Allowance:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", phoneAllowance)));

        detailsGrid.add(new JLabel("Clothing Allowance:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", clothingAllowance)));

        detailsGrid.add(new JLabel("Gross Monthly Salary:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", grossSalary)));

        // Calculate deductions
        // These calculations are simplified and should be replaced with your actual calculations
        double sssDeduction = calculateSssContribution(basicSalary);
        double philhealthDeduction = calculatePhilhealthContribution(basicSalary);
        double pagibigDeduction = calculatePagibigContribution(basicSalary);
        double withholdingTax = calculateWithholdingTax(basicSalary, sssDeduction, philhealthDeduction, pagibigDeduction);

        // Add deduction details
        detailsGrid.add(new JLabel("SSS Contribution:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", sssDeduction)));

        detailsGrid.add(new JLabel("PhilHealth Contribution:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", philhealthDeduction)));

        detailsGrid.add(new JLabel("Pag-IBIG Contribution:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", pagibigDeduction)));

        detailsGrid.add(new JLabel("Withholding Tax:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", withholdingTax)));

        // Calculate total deductions and net pay
        double totalDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + withholdingTax;
        double netPay = grossSalary - totalDeductions;

        // Add total deductions and net pay
        detailsGrid.add(new JLabel("Total Deductions:"));
        detailsGrid.add(new JLabel(String.format("₱%.2f", totalDeductions)));

        detailsGrid.add(new JLabel("Net Pay:"));
        JLabel netPayLabel = new JLabel(String.format("₱%.2f", netPay));
        netPayLabel.setFont(new Font(netPayLabel.getFont().getName(), Font.BOLD, netPayLabel.getFont().getSize()));
        detailsGrid.add(netPayLabel);

        // Add details grid to salary panel
        salaryDetailsPanel.add(detailsGrid);

        // Refresh the panel
        salaryDetailsPanel.revalidate();
        salaryDetailsPanel.repaint();
    }

    // Placeholder methods for calculating deductions
    // Replace these with your actual calculation logic

    private double calculateSssContribution(double basicSalary) {
        // Simplified SSS contribution calculation
        // Replace with actual formula
        return Math.min(basicSalary * 0.045, 1125.0);  // 4.5% up to maximum of 1,125
    }

    private double calculatePhilhealthContribution(double basicSalary) {
        // Simplified PhilHealth contribution calculation
        // Replace with actual formula
        return Math.min(basicSalary * 0.035 / 2, 1800.0);  // 3.5% divided by 2 (employee share)
    }

    private double calculatePagibigContribution(double basicSalary) {
        // Simplified Pag-IBIG contribution calculation
        // Replace with actual formula
        return Math.min(basicSalary * 0.02, 100.0);  // 2% up to maximum of 100
    }

    private double calculateWithholdingTax(double basicSalary, double sss, double philhealth, double pagibig) {
        // Simplified withholding tax calculation
        // Replace with actual formula
        double taxableIncome = basicSalary - sss - philhealth - pagibig;

        // Progressive tax rates (simplified)
        if (taxableIncome <= 20833) {
            return 0;
        } else if (taxableIncome <= 33332) {
            return (taxableIncome - 20833) * 0.15;
        } else if (taxableIncome <= 66666) {
            return 1875 + (taxableIncome - 33333) * 0.20;
        } else if (taxableIncome <= 166666) {
            return 8541.80 + (taxableIncome - 66667) * 0.25;
        } else if (taxableIncome <= 666666) {
            return 33541.80 + (taxableIncome - 166667) * 0.30;
        } else {
            return 183541.80 + (taxableIncome - 666667) * 0.35;
        }
    }
}