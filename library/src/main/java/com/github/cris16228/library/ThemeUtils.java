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
    Core core;
    Context context;

    public static ThemeUtils with(Core _core) {
        ThemeUtils themeUtils = new ThemeUtils();
        themeUtils.core = _core;
        return themeUtils;
    }

    public static ThemeUtils with(Context _context) {
        ThemeUtils themeUtils = new ThemeUtils();
        themeUtils.context = _context;
        return themeUtils;
    }

    public static ThemeUtils with(Core _core, Context _context) {
        ThemeUtils themeUtils = new ThemeUtils();
        themeUtils.core = _core;
        themeUtils.context = _context;
        return themeUtils;
    }

    public ThemeUtils getTheme() {
        SharedPreferences pref = context.getSharedPreferences(core.PREF, MODE_PRIVATE);
        System.out.println(pref.getString(THEME, LIGHT));
        switch (pref.getString(THEME, LIGHT)) {
            case DARK:
                theme = Theme.DARK;
            case LIGHT:
                theme = Theme.LIGHT;
            case AUTO:
                theme = Theme.AUTO;
        }
        return this;
    }

    public void applyTheme() {
        System.out.println(theme.getClass().getSimpleName());
        if (theme == Theme.LIGHT)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (theme == Theme.DARK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if (theme == Theme.AUTO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}
