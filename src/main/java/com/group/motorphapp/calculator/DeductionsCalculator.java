package com.group.motorphapp.calculator;

public class DeductionsCalculator extends BaseCalculator {
    public double calculateTotalDeductions(double grossPay) {
        double sss = calculateSSSContribution(grossPay);
        double philHealth = calculatePhilhealthContribution(grossPay);
        double pagIbig = calculatePagibigContribution(grossPay);

        // Calculate taxable income (gross - all contributions)
        double taxableIncome = grossPay - (sss + philHealth + pagIbig);
        double withholdingTax = calculateWithholdingTax(taxableIncome);

        return sss + philHealth + pagIbig + withholdingTax;
    }

    public double calculateSSSContribution(double grossPay) {
        // SSS contribution table implementation
        if (grossPay <= 3250) {
            return 135.00;
        } else if (grossPay <= 3750) {
            return 157.50;
        } else if (grossPay <= 4250) {
            return 180.00;
        } else if (grossPay <= 4750) {
            return 202.50;
        } else if (grossPay <= 5250) {
            return 225.00;
        } else if (grossPay <= 5750) {
            return 247.50;
        } else if (grossPay <= 6250) {
            return 270.00;
        } else if (grossPay <= 6750) {
            return 292.50;
        } else if (grossPay <= 7250) {
            return 315.00;
        } else if (grossPay <= 7750) {
            return 337.50;
        } else if (grossPay <= 8250) {
            return 360.00;
        } else if (grossPay <= 8750) {
            return 382.50;
        } else if (grossPay <= 9250) {
            return 405.00;
        } else if (grossPay <= 9750) {
            return 427.50;
        } else if (grossPay <= 10250) {
            return 450.00;
        } else {
            return 472.50; // Maximum contribution
        }
    }

    public double calculatePhilhealthContribution(double grossPay) {
        // PhilHealth contribution calculation (3% of monthly basic salary)
        double rate = 0.03;
        double contribution = grossPay * rate;
        double maxContribution = 1800.00; // Maximum monthly contribution
        return Math.min(contribution, maxContribution);
    }

    public double calculatePagibigContribution(double grossPay) {
        // Pag-IBIG contribution calculation
        double rate = (grossPay > 1500) ? 0.02 : 0.01;
        double contribution = grossPay * rate;
        double maxContribution = 100.00; // Maximum monthly contribution
        return Math.min(contribution, maxContribution);
    }

    public double calculateWithholdingTax(double taxableIncome) {
        // Monthly withholding tax calculation
        if (taxableIncome <= 20833) {
            return 0;
        } else if (taxableIncome <= 33333) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66667) {
            return 2500 + ((taxableIncome - 33333) * 0.25);
        } else if (taxableIncome <= 166667) {
            return 10833.33 + ((taxableIncome - 66667) * 0.30);
        } else if (taxableIncome <= 666667) {
            return 40833.33 + ((taxableIncome - 166667) * 0.32);
        } else {
            return 200833.33 + ((taxableIncome - 666667) * 0.35);
        }
    }
}