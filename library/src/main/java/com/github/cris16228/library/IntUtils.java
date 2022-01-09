package com.github.cris16228.library;

import android.content.Context;

import java.util.Locale;

public class IntUtils {

    Context context;

    public static IntUtils with(Context _context) {
        IntUtils intUtils = new IntUtils();
        intUtils.context = _context;
        return intUtils;
    }

    public String convertIntoTime(int ms) {
        String time;
        int x, seconds, minutes, hours;
        x = ms / 1000;
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        if (hours != 0)
            time = String.format(Locale.US, "%02d", hours) + ":" + String.format(Locale.US, "%02d", minutes) + ":" + String.format(Locale.US, "%02d", seconds);
        else
            time = String.format(Locale.US, "%02d", minutes) + ":" + String.format(Locale.US, "%02d", seconds);
        return time;
    }
}
