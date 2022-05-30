package com.github.cris16228.library;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.math.RoundingMode;

public class DeviceUtils {

    Context context;
    DisplayMetrics dm;

    public static DeviceUtils with(Context _context) {
        DeviceUtils deviceUtils = new DeviceUtils();
        deviceUtils.context = _context;
        deviceUtils.dm = new DisplayMetrics();
        return deviceUtils;
    }

    public static boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    public String getDisplaySize(int limit) throws LibraryException {
        double x = 0, y = 0;
        int mWidthPixels, mHeightPixels;
        try {
            WindowManager windowManager = ((Activity) context).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            mWidthPixels = realSize.x;
            mHeightPixels = realSize.y;
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            x = Math.pow(mWidthPixels / dm.xdpi, 2);
            y = Math.pow(mHeightPixels / dm.ydpi, 2);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return FloatUtils.with(context).getNumberFormat(Math.sqrt(x + y), limit, true, RoundingMode.HALF_UP) + "\"";
    }

    public PackageInfo appFromPackage(String packageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, 0);
    }

    public PackageInfo getPermissions(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS);
    }

    public PackageInfo getActivities(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES);
    }

    public PackageInfo getReceivers(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_RECEIVERS);
    }

    public PackageInfo getProviders(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_PROVIDERS);
    }

    public PackageInfo getServices(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_SERVICES);
    }

    public boolean isSystemApp(PackageInfo p) {
        return ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM);
    }

    public int getWidth() {
        dm = context.getResources().getDisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public int getHeight() {
        dm = context.getResources().getDisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public String getResolution() {
        return getWidth() + "x" + getHeight();
    }

    public String getOrientation(int orientation) {
        switch (orientation) {
            case 0:
                return "Undefined";
            case 1:
                return "Portrait";
            case 2:
                return "Landscape";
        }
        return "Undefined";
    }
}
