package com.github.cris16228.library;

public class MathUtils {

    public static String toCalculate;
    public static String result;

    public static String SolveMath(String input) {
        toCalculate = input;
        if (toCalculate.split("\\+").length == 2) {
            String[] numbers = input.split("\\+");
            try {
                double d = Double.parseDouble(numbers[0]) + Double.parseDouble(numbers[1]);
                result = cutDecimal(Double.toString(d));
            } catch (Exception e) {
                result = "NaN";
            }
        }
        if (toCalculate.split("\\-").length == 2) {
            String[] numbers = input.split("\\-");
            try {
                double d = Double.parseDouble(numbers[0]) - Double.parseDouble(numbers[1]);
                result = cutDecimal(Double.toString(d));
            } catch (Exception e) {
                result = "NaN";
            }
        }
        if (toCalculate.split("\\*").length == 2) {
            String[] numbers = input.split("\\*");
            try {
                double d = Double.parseDouble(numbers[0]) * Double.parseDouble(numbers[1]);
                result = cutDecimal(Double.toString(d));
            } catch (Exception e) {
                result = "NaN";
            }
        }
        if (toCalculate.split("\\/").length == 2) {
            String[] numbers = input.split("\\/");
            try {
                double d = Double.parseDouble(numbers[0]) / Double.parseDouble(numbers[1]);
                result = cutDecimal(Double.toString(d));
            } catch (Exception e) {
                result = "NaN";
            }
        }
        return result;
    }

    public static String cutDecimal(String input) {
        String[] number = input.split("\\.");
        if (number.length > 1) {
            if (number[1].equals("0")) {
                input = number[0];
            }
        }
        return input;
    }
}
