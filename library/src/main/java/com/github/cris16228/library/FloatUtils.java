package com.github.cris16228.library;

import android.content.Context;

import java.text.NumberFormat;

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
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        if (value instanceof Float || value instanceof Double)
            return format.format(value);
        else
            throw new LibraryException(getClass(), value + "is not valid! It must be a \"float\" or a \"double\"");
    }
}
