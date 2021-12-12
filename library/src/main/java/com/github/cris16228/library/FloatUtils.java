package com.github.cris16228.library;

import android.content.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class FloatUtils {

    Context context;

    public static FloatUtils with(Context _context) {
        FloatUtils floatUtils = new FloatUtils();
        floatUtils.context = _context;
        return floatUtils;
    }

    public String getNumberFormat(Object value, int limit) throws LibraryException {
        if (limit <= 0)
            throw new LibraryException(getClass(), limit + "is not valid! It must be higher than 0");
        float tempFloat;
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(limit);
        format.setMaximumFractionDigits(limit);
        if (value instanceof Float || value instanceof Double)
            return format.format(value);
        if (value instanceof String)
            try {
                tempFloat = Float.parseFloat((String) value);
                return format.format(tempFloat);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        else
            throw new LibraryException(getClass(), value + "is not valid! It must be a \"float\" or a \"double\"");
        return (String) value;
    }

    public String getNumberFormat(Object value, int limit, boolean round, RoundingMode roundingMode) throws LibraryException {
        if (limit <= 0)
            throw new LibraryException(getClass(), limit + "is not valid! It must be higher than 0");
        double tempDouble;
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMinimumFractionDigits(limit);
        format.setMaximumFractionDigits(limit);
        if (round)
            format.setRoundingMode(roundingMode);
        if (value instanceof Float || value instanceof Double) {
            BigDecimal bd = BigDecimal.valueOf((double) value);
            return format.format(bd);
        }
        if (value instanceof String)
            try {
                tempDouble = Double.parseDouble((String) value);
                BigDecimal bd = BigDecimal.valueOf((double) value);
                return format.format(bd);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        else
            throw new LibraryException(getClass(), value + "is not valid! It must be a \"float\" or a \"double\"");
        return (String) value;
    }
}
