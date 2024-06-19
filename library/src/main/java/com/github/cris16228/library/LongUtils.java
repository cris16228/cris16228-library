package com.github.cris16228.library;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LongUtils {

    Context context;

    public static LongUtils with(Context _context) {
        LongUtils longUtils = new LongUtils();
        longUtils.context = _context;
        return longUtils;
    }

    private void prependTimeAndUnit(StringBuffer timeBuf, long time, String unit) {
        if (time < 1) {
            return;
        }

        if (timeBuf.length() > 0) {
            timeBuf.insert(0, " ");
        }

        timeBuf.insert(0, unit);
        timeBuf.insert(0, time);
    }

    public String getSize(long size) {
        long n = 1000;
        String s;
        double kb = size / (double) n;
        double mb = kb / n;
        double gb = mb / n;
        double tb = gb / n;

        if (size < n) {
            s = size + " Bytes";
        } else if (size < n * n) {
            s = String.format(Locale.US, "%.2f KB", kb);
        } else if (size < n * n * n) {
            s = String.format(Locale.US, "%.2f MB", mb);
        } else if (size < n * n * n * n) {
            s = String.format(Locale.US, "%.2f GB", gb);
        } else {
            s = String.format(Locale.US, "%.2f TB", tb);
        }

        return s;
    }

    public String longToDate(long date, String pattern) {
        if (StringUtils.isEmpty(pattern))
            pattern = "EEEE, dd MMMM yyyy HH:mm";
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    public String toReadableTime(long timeInMillis) {

        if (timeInMillis < 1) {
            return String.valueOf(timeInMillis);
        }

        StringBuffer timeBuf = new StringBuffer();

        // second (1000ms) & above
        long time = timeInMillis / 1000;
        if (time < 1) {
            return "now";
        }

        long seconds = time % 60;
        prependTimeAndUnit(timeBuf, seconds, "s");

        // minute(60s) & above
        time = time / 60;
        if (time < 1) {
            return timeBuf.toString();
        }

        long minutes = time % 60;
        prependTimeAndUnit(timeBuf, minutes, "m");

        // hour(60m) & above
        time = time / 60;
        if (time < 1) {
            return timeBuf.toString();
        }

        long hours = time % 24;
        prependTimeAndUnit(timeBuf, hours, "h");

        // day(24h) & above
        time = time / 24;
        if (time < 1) {
            return timeBuf.toString();
        }

        long day = time % 365;
        prependTimeAndUnit(timeBuf, day, "d");

        // year(365d) ...
        time = time / 365;
        if (time < 1) {
            return timeBuf.toString();
        }

        prependTimeAndUnit(timeBuf, time, "y");

        return timeBuf.toString();
    }

    public String toReadableTimeFull(long timeInMillis) {
        return toReadableTimeFull(timeInMillis, "");
    }

    public String toReadableTimeFull(long timeInMillis, String timeFormat) {
        boolean full = StringUtils.isEmpty(timeFormat);
        char[] units = {'s', 'm', 'h', 'd', 'w', 'M', 'y'};
        boolean[] addUnit = new boolean[units.length];

        for (int i = 0; i < units.length; i++) {
            char currentUnit = units[i];
            if (timeFormat.indexOf(currentUnit) != -1) {
                addUnit[i] = true;
            }
        }

        boolean past = timeInMillis < 0;
        if (past) timeInMillis = -timeInMillis;

        StringBuilder timeBuf = new StringBuilder();

        long[] divisor = {1000, 60, 60, 24, 7, 30, 365}; // Updated divisors
        String[] unitNames = {" seconds", " minutes", " hours", " days", "weeks", " months", " years"}; // Updated unit names

        for (int i = 0; i < divisor.length; i++) {
            long time = timeInMillis / divisor[i];
            if (time < 1) {
                break;
            }

            long currentValue;
            if (i == 3) { // Special handling for days (index 3)
                currentValue = time;
                timeInMillis = 0; // Reset for accurate remaining calculations
            } else {
                currentValue = time % (i == divisor.length - 1 ? Long.MAX_VALUE : divisor[i + 1]);
                timeInMillis -= currentValue * divisor[i];
            }

            if (addUnit[i] || full) {
                prependTimeAndUnit(timeBuf, currentValue, currentValue > 1 ? unitNames[i] : unitNames[i].substring(0, unitNames[i].length() - 1)); // Use substring for singular
            }
        }

        if (past) {
            timeBuf.append(" ago");
        }

        return timeBuf.toString().trim();
    }

    private void prependTimeAndUnit(StringBuilder buffer, long value, String unit) {
        buffer.insert(0, value + unit + " ");
    }
}
