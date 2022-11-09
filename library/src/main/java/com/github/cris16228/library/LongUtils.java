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

    public String toReadableTimeFull(long timeInMillis) {
        return toReadableTimeFull(timeInMillis, "");
    }

    public String toReadableTimeFull(long timeInMillis, String timeFormat) {
        boolean full = StringUtils.isEmpty(timeFormat);
        char[] chars = timeFormat.toCharArray();
        boolean add_seconds = false, add_minutes = false, add_hours = false, add_days = false, add_years = false;
        System.out.println(chars);
        for (char _char : chars) {
            if (_char == 's')
                add_seconds = true;
            if (_char == 'm')
                add_minutes = true;
            if (_char == 'h')
                add_hours = true;
            if (_char == 'd')
                add_days = true;
            if (_char == 'y')
                add_years = true;
        }
        boolean past = timeInMillis < 1;
        timeInMillis *= -1;

        StringBuffer timeBuf = new StringBuffer();

        // second (1000ms) & above
        long time = timeInMillis / 1000;
        if (time < 1) {
            return "now";
        }

        long seconds = time % 60;
        if (add_seconds || full)
            prependTimeAndUnit(timeBuf, seconds, seconds > 0 ? appendPast(" seconds", past) : appendPast(" second", past));

        // minute(60s) & above
        time = time / 60;
        if (time < 1) {
            return timeBuf.toString();
        }

        long minutes = time % 60;
        if (add_minutes || full)
            prependTimeAndUnit(timeBuf, minutes, minutes > 0 ? appendPast(" minutes", past) : appendPast(" minute", past));

        // hour(60m) & above
        time = time / 60;
        if (time < 1) {
            return timeBuf.toString();
        }

        long hours = time % 24;
        if (add_hours || full)
            prependTimeAndUnit(timeBuf, hours, hours > 0 ? appendPast(" hours", past) : appendPast(" hour", past));

        // day(24h) & above
        time = time / 24;
        if (time < 1) {
            return timeBuf.toString();
        }

        long day = time % 365;
        if (add_days || full)
            prependTimeAndUnit(timeBuf, day, day > 0 ? appendPast(" days", past) : appendPast(" day", past));

        // year(365d) ...
        time = time / 365;
        if (time < 1) {
            return timeBuf.toString();
        }
        if (add_years || full)
            prependTimeAndUnit(timeBuf, time, time > 1 ? appendPast(" years", past) : appendPast(" year", past));

        return timeBuf.toString();
    }

    private String appendPast(String text, boolean past) {
        return past ? text + " ago" : text;
    }
}
