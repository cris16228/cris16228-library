package com.github.cris16228.library;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleUtils {

    //Locale
    public final String LOCALE = "locale";
    public final String EN = "en";
    public final String IT = "it";

    Core core;
    Context context;

    public LocaleUtils with(Core _core) {
        core = _core;
        return this;
    }

    public LocaleUtils with(Context _context) {
        context = _context;
        return this;
    }

    public LocaleUtils with(Core _core, Context _context) {
        context = _context;
        core = _core;
        return this;
    }

    public void onStartSetLocale() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(core.PREF, MODE_PRIVATE);
        String language = sharedPreferences.getString(LOCALE, EN);
        setLocale(language);
    }

    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
