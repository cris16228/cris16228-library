package com.github.cris16228.library;

import android.content.Context;

public class PrefUtils {

    Core core;
    Context context;

    public static PrefUtils with(Core _core) {
        PrefUtils prefUtils = new PrefUtils();
        prefUtils.core = _core;
        return prefUtils;
    }

    public static PrefUtils with(Context _context) {
        PrefUtils prefUtils = new PrefUtils();
        prefUtils.context = _context;
        return prefUtils;
    }

    public static PrefUtils with(Core _core, Context _context) {
        PrefUtils prefUtils = new PrefUtils();
        prefUtils.core = _core;
        prefUtils.context = _context;
        return prefUtils;
    }

    public void setSharedPref(String pref) {
        core.PREF = pref;
    }

    public boolean isKeyPresent(String key) {
        return context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).contains(key);
    }

    public String getString(String key) {
        return context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).getString(key, "");
    }

    public Integer getInt(String key) {
        return context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).getInt(key, -1);
    }

    public Boolean getBoolean(String key) {
        return context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public Float getFloat(String key) {
        return context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).getFloat(key, -1.0F);
    }

    public Long getLong(String key) {
        return context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).getLong(key, -1L);
    }

    public PrefUtils setString(String key, String value) {
        context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).edit().putString(key, value).apply();
        return this;
    }

    public PrefUtils setInt(String key, int value) {
        context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
        return this;
    }

    public PrefUtils setBoolean(String key, boolean value) {
        context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
        return this;
    }

    public PrefUtils setFloat(String key, float value) {
        context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).edit().putFloat(key, value).apply();
        return this;
    }

    public PrefUtils setLong(String key, long value) {
        context.getSharedPreferences(core.PREF, Context.MODE_PRIVATE).edit().putLong(key, value).apply();
        return this;
    }
}
