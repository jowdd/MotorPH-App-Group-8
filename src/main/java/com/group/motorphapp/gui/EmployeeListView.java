package com.group.motorphapp.gui;

import com.group.motorphapp.model.Employee;
import com.group.motorphapp.model.PayrollSystem;
import com.group.motorphapp.Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class EmployeeListView extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private PayrollSystem payrollSystem;
    private JButton viewEmployeeButton;
    private JButton newEmployeeButton;
    private JButton manageEmployeesButton; // New button for managing employees

    public EmployeeListView() {
        setTitle("MotorPH Employee Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model with column names
        String[] columnNames = {"Employee Number", "Last Name", "First Name",
                "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setReorderingAllowed(false);

        // Auto-adjust column widths
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Create buttons
        viewEmployeeButton = new JButton("View Employee");
        newEmployeeButton = new JButton("New Employee");
        manageEmployeesButton = new JButton("Manage Employees"); // New button

        // Set up button actions
        viewEmployeeButton.addActionListener(e -> viewSelectedEmployee());
        newEmployeeButton.addActionListener(e -> openNewEmployeeForm());
        manageEmployeesButton.addActionListener(e -> openEmployeeManagementPanel()); // New action

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewEmployeeButton);
        buttonPanel.add(newEmployeeButton);
        buttonPanel.add(manageEmployeesButton); // Add the new button

        // Add header label
        JLabel headerLabel = new JLabel("MotorPH Employee Records", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Add components to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerLabel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    // New method to open the employee management panel
    private void openEmployeeManagementPanel() {
        // Call static method directly
        Main.showEmployeeManagementPanel(payrollSystem);
    }

    // Rest of your class remains the same...

    public void setPayrollSystem(PayrollSystem payrollSystem) {
        this.payrollSystem = payrollSystem;
        refreshEmployeeTable();
    }

    public void refreshEmployeeTable() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add employee data to the table
        List<Employee> employees = payrollSystem.getAllEmployees();

        for (Employee employee : employees) {
            Object[] rowData = {
                    employee.getEmployeeNumber(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    employee.getSssNumber(),
                    employee.getPhilhealthNumber(),
                    employee.getTinNumber(),
                    employee.getPagibigNumber()
            };
            tableModel.addRow(rowData);
        }

        // Sort by employee number
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);
    }

    private void viewSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the selected employee number (adjust index for sorted table)
            selectedRow = employeeTable.convertRowIndexToModel(selectedRow);
            String employeeNumber = (String) tableModel.getValueAt(selectedRow, 0);
            Employee selectedEmployee = findEmployeeByNumber(employeeNumber);

            if (selectedEmployee != null) {
                EmployeeDetailView detailView = new EmployeeDetailView(selectedEmployee, payrollSystem);
                detailView.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee from the table.");
        }
    }

    private void openNewEmployeeForm() {
        NewEmployeeForm newEmployeeForm = new NewEmployeeForm(this, payrollSystem);
        newEmployeeForm.setVisible(true);
    }

    private Employee findEmployeeByNumber(String employeeNumber) {
        for (Employee emp : payrollSystem.getAllEmployees()) {
            if (emp.getEmployeeNumber().equals(employeeNumber)) {
                return emp;
            }
        }
        return null;
    }
}