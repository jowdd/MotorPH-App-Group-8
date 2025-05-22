package model;

import com.group.motorphapp.calculator.DeductionsCalculator;
import com.group.motorphapp.calculator.BasicPayCalculator;
import java.util.List;

public class PayrollCalculator {
    private BasicPayCalculator basicPayCalculator;
    private DeductionsCalculator deductionsCalculator;

    public PayrollCalculator() {
        basicPayCalculator = new BasicPayCalculator();
        deductionsCalculator = new DeductionsCalculator();
    }

    public double calculateGrossPay(Employee employee, List<TimeLog> timeLogs) {
        return basicPayCalculator.calculateBasicPay(employee, timeLogs);
    }

    public double calculateSSSContribution(double grossPay) {
        return deductionsCalculator.calculateSSSContribution(grossPay);
    }

    public double calculatePhilhealthContribution(double grossPay) {
        return deductionsCalculator.calculatePhilhealthContribution(grossPay);
    }

    public double calculatePagibigContribution(double grossPay) {
        return deductionsCalculator.calculatePagibigContribution(grossPay);
    }

    public double calculateWithholdingTax(double taxableIncome) {
        return deductionsCalculator.calculateWithholdingTax(taxableIncome);
    }
}