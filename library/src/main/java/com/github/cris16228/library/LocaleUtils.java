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

    public static LocaleUtils with(Core _core) {
        LocaleUtils localeUtils = new LocaleUtils();
        localeUtils.core = _core;
        return localeUtils;
    }

    public LocaleUtils with(Context _context) {
        LocaleUtils localeUtils = new LocaleUtils();
        localeUtils.context = _context;
        return localeUtils;
    }

    public LocaleUtils with(Core _core, Context _context) {
        LocaleUtils localeUtils = new LocaleUtils();
        localeUtils.core = _core;
        localeUtils.context = _context;
        return localeUtils;
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
