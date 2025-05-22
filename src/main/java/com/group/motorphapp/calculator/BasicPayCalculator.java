package com.group.motorphapp.calculator;

import model.Employee;
import model.TimeLog;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BasicPayCalculator {
    private static final double HOURLY_RATE = 107.14; // P15,000 / 140 hours monthly (Assuming 35 hours per week)
    private static final double OVERTIME_MULTIPLIER = 1.25;
    private static final double NIGHT_DIFF_MULTIPLIER = 1.1;
    private static final LocalTime NIGHT_DIFF_START = LocalTime.of(22, 0); // 10:00 PM
    private static final LocalTime NIGHT_DIFF_END = LocalTime.of(6, 0);    // 6:00 AM

    public double calculateBasicPay(Employee employee,
                                    List<TimeLog> timeLogs) {
        double totalPay = 0.0;

        for (TimeLog log : timeLogs) {
            if (log.getTimeIn() != null && log.getTimeOut() != null) {
                // Calculate regular hours and pay
                double hoursWorked = calculateHoursWorked(log);
                double regularPay = calculateRegularPay(hoursWorked);

                // Calculate overtime pay if applicable
                double overtimePay = calculateOvertimePay(hoursWorked);

                // Calculate night differential pay if applicable
                double nightDiffPay = calculateNightDifferentialPay(log);

                // Add all components for this day
                totalPay += regularPay + overtimePay + nightDiffPay;
            }
        }

        return totalPay;
    }

    private double calculateHoursWorked(TimeLog log) {
        // Convert LocalTime to LocalDateTime to calculate duration correctly
        LocalDate date = log.getDate();
        LocalDateTime timeIn = LocalDateTime.of(date, log.getTimeIn());

        // Handle case when time-out is on the next day
        LocalDateTime timeOut;
        if (log.getTimeOut().isBefore(log.getTimeIn())) {
            timeOut = LocalDateTime.of(date.plusDays(1), log.getTimeOut());
        } else {
            timeOut = LocalDateTime.of(date, log.getTimeOut());
        }

        Duration duration = Duration.between(timeIn, timeOut);
        return duration.toMinutes() / 60.0; // Convert minutes to hours
    }

    private double calculateRegularPay(double hoursWorked) {
        // Cap regular hours at 8
        double regularHours = Math.min(hoursWorked, 8.0);
        return regularHours * HOURLY_RATE;
    }

    private double calculateOvertimePay(double hoursWorked) {
        // Calculate overtime hours (anything beyond 8 hours)
        double overtimeHours = Math.max(0, hoursWorked - 8.0);
        return overtimeHours * HOURLY_RATE * OVERTIME_MULTIPLIER;
    }

    private double calculateNightDifferentialPay(TimeLog log) {
        double nightDiffPay = 0.0;

        // Convert to LocalDateTime for proper calculations
        LocalDate date = log.getDate();
        LocalDateTime timeIn = LocalDateTime.of(date, log.getTimeIn());

        // Handle case when time-out is on the next day
        LocalDateTime timeOut;
        if (log.getTimeOut().isBefore(log.getTimeIn())) {
            timeOut = LocalDateTime.of(date.plusDays(1), log.getTimeOut());
        } else {
            timeOut = LocalDateTime.of(date, log.getTimeOut());
        }

        // Create night differential start and end times
        LocalDateTime nightStart = LocalDateTime.of(date, NIGHT_DIFF_START);
        LocalDateTime nightEnd = LocalDateTime.of(date.plusDays(1), NIGHT_DIFF_END);

        // Check if work hours overlap with night differential hours
        if (timeIn.isBefore(nightEnd) && timeOut.isAfter(nightStart)) {
            // Calculate overlap
            LocalDateTime overlapStart = timeIn.isAfter(nightStart) ? timeIn : nightStart;
            LocalDateTime overlapEnd = timeOut.isBefore(nightEnd) ? timeOut : nightEnd;

            // Calculate hours during night differential period
            Duration nightDiffDuration = Duration.between(overlapStart, overlapEnd);
            double nightDiffHours = nightDiffDuration.toMinutes() / 60.0;

            // Calculate night differential pay
            nightDiffPay = nightDiffHours * HOURLY_RATE * (NIGHT_DIFF_MULTIPLIER - 1.0);
        }

        return nightDiffPay;
    }
}