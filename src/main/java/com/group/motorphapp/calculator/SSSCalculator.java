package com.group.motorphapp.calculator;

public class SSSCalculator extends BaseCalculator {
    public double calculateContribution(double monthlyBasic) {
        if (monthlyBasic <= 3250) {
            return 135.00;
        } else if (monthlyBasic <= 3750) {
            return 157.50;
        } else if (monthlyBasic <= 4250) {
            return 180.00;
        } else if (monthlyBasic <= 4750) {
            return 202.50;
        } else if (monthlyBasic <= 5250) {
            return 225.00;
        } else if (monthlyBasic <= 5750) {
            return 247.50;
        } else if (monthlyBasic <= 6250) {
            return 270.00;
        } else if (monthlyBasic <= 6750) {
            return 292.50;
        } else if (monthlyBasic <= 7250) {
            return 315.00;
        } else if (monthlyBasic <= 7750) {
            return 337.50;
        }
        // Add more ranges as needed

        return 1125.00; // Maximum contribution
    }
}