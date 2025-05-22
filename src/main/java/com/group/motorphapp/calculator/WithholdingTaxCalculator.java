package com.group.motorphapp.calculator;

public class WithholdingTaxCalculator extends BaseCalculator {
    public double calculateTax(double taxableIncome) {
        if (taxableIncome <= 20833) {
            return 0.0;
        } else if (taxableIncome <= 33333) {
            return roundToTwoDecimals((taxableIncome - 20833) * 0.20);
        } else if (taxableIncome <= 66667) {
            return roundToTwoDecimals(2500 + ((taxableIncome - 33333) * 0.25));
        } else if (taxableIncome <= 166667) {
            return roundToTwoDecimals(10833.33 + ((taxableIncome - 66667) * 0.30));
        } else if (taxableIncome <= 666667) {
            return roundToTwoDecimals(40833.33 + ((taxableIncome - 166667) * 0.32));
        } else {
            return roundToTwoDecimals(200833.33 + ((taxableIncome - 666667) * 0.35));
        }
    }
}