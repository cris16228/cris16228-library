package com.github.cris16228.library;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

enum Theme {
    DARK,
    LIGHT,
    AUTO
}

public class ThemeUtils {

    //Theme
    public final String THEME = "theme";
    public final String LIGHT = "light";
    public final String DARK = "dark";
    public final String AUTO = "follow_system";
    public Theme theme;
    Context context;
    private PrefUtils prefUtils;

    public static ThemeUtils with(PrefUtils prefUtils) {
        ThemeUtils themeUtils = new ThemeUtils();
        themeUtils.prefUtils = prefUtils;
        return themeUtils;
    }

    public static ThemeUtils with(Context _context) {
        ThemeUtils themeUtils = new ThemeUtils();
        themeUtils.context = _context;
        if (themeUtils.prefUtils == null)
            themeUtils.prefUtils = new PrefUtils();
        return themeUtils;
    }

    public static ThemeUtils with(PrefUtils _prefUtils, Context _context) {
        ThemeUtils themeUtils = new ThemeUtils();
        themeUtils.prefUtils = _prefUtils;
        themeUtils.context = _context;
        return themeUtils;
    }

    public Theme getTheme() {
        SharedPreferences pref = context.getSharedPreferences(PrefUtils.PREF, MODE_PRIVATE);
        switch (pref.getString(THEME, LIGHT)) {
            case DARK:
                return Theme.DARK;
            case LIGHT:
                return Theme.LIGHT;
            case AUTO:
                return Theme.AUTO;
        }
        return Theme.LIGHT;
    }

    public void applyTheme() {
        if (getTheme() == Theme.LIGHT && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (getTheme() == Theme.DARK && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if (getTheme() == Theme.AUTO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}
