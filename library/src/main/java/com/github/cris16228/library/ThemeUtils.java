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

    public ThemeUtils with(Core _core) {
        core = _core;
        return this;
    }

    public ThemeUtils with(Context _context) {
        context = _context;
        return this;
    }

    public ThemeUtils with(Core _core, Context _context) {
        context = _context;
        core = _core;
        return this;
    }

    public Theme getTheme() {
        SharedPreferences pref = context.getSharedPreferences(core.PREF, MODE_PRIVATE);
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
        if (getTheme() == Theme.LIGHT)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (getTheme() == Theme.DARK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if (getTheme() == Theme.AUTO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}
