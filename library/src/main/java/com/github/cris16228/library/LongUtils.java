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
        double kb = size / n;
        double mb = kb / n;
        double gb = mb / n;
        double tb = gb / n;
        if (size < n) {
            s = size + " Bytes";
        } else if (size < n * n) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size < n * n * n) {
            s = String.format("%.2f", mb) + " MB";
        } else if (size < n * n * n * n) {
            s = String.format("%.2f", gb) + " GB";
        } else {
            s = String.format("%.2f", tb) + " TB";
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
}
