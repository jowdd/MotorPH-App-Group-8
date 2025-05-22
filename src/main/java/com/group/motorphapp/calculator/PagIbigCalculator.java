package com.group.motorphapp.calculator;

public class PagIbigCalculator extends BaseCalculator {
    private static final double LOWER_RATE = 0.01; // 1%
    private static final double HIGHER_RATE = 0.02; // 2%
    private static final double SALARY_THRESHOLD = 1500.0;

    public double calculateContribution(double monthlyBasic) {
        double rate = monthlyBasic <= SALARY_THRESHOLD ? LOWER_RATE : HIGHER_RATE;
        return roundToTwoDecimals(monthlyBasic * rate);
    }
}