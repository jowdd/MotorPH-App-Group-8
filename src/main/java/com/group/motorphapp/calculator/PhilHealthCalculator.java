package com.group.motorphapp.calculator;

public class PhilHealthCalculator extends BaseCalculator {
    private static final double CONTRIBUTION_RATE = 0.03; // 3%

    public double calculateContribution(double monthlyBasic) {
        double contribution = monthlyBasic * CONTRIBUTION_RATE;
        // Return employee's share (half of total contribution)
        return roundToTwoDecimals(contribution / 2);
    }
}