package com.group.motorphapp.calculator;

public abstract class BaseCalculator {
    protected double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}