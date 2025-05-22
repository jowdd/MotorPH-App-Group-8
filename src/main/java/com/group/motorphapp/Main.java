/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.group.motorphapp;


import com.group.motorphapp.model.PayrollSystem;
import com.group.motorphapp.gui.EmployeeListView;
import com.group.motorphapp.gui.EmployeeManagementPanel;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create PayrollSystem which will load data in its constructor
        PayrollSystem payrollSystem = new PayrollSystem();

        // Check if data was loaded
        System.out.println("Loaded " + payrollSystem.getAllEmployees().size() + " employees");
        System.out.println("Loaded " + payrollSystem.getTimeLogs().size() + " time logs");

        // Initialize and show the GUI using SwingUtilities
        SwingUtilities.invokeLater(() -> {
            EmployeeListView employeeListView = new EmployeeListView();
            employeeListView.setPayrollSystem(payrollSystem);
            employeeListView.setVisible(true);
        });
    }

    // Change to include PayrollSystem parameter
    public static void showEmployeeManagementPanel(PayrollSystem payrollSystem) {
        EmployeeManagementPanel panel = new EmployeeManagementPanel();
        panel.setPayrollSystem(payrollSystem);  // Add this line to pass the payrollSystem

        // Create a new frame for the panel
        JFrame frame = new JFrame("Employee Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(panel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    // This method should be static or removed since it belongs in EmployeeListView
    public static void openEmployeeManagementPanel(PayrollSystem payrollSystem) {
        showEmployeeManagementPanel(payrollSystem);  // Call the static method directly
    }
}