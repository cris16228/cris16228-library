package utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.cris16228.library.AsyncUtils;
import com.github.cris16228.library.FileUtils;
import com.github.cris16228.library.NetworkUtils;
import com.github.cris16228.library.StringUtils;
import com.github.cris16228.library.deviceutils.PackageUtils;
import com.github.cris16228.library.http.HttpUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler defaultUEH;
    private final Activity app;
    private final String bearer;

    public UncaughtExceptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
        this.bearer = "";
    }

    public UncaughtExceptionHandler(Activity app, String bearer) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
        this.bearer = bearer;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        AsyncUtils uploadCrash = AsyncUtils.get();
        uploadCrash.onExecuteListener(new AsyncUtils.onExecuteListener() {
            @Override
            public void preExecute() {

            }

            @Override
            public void doInBackground() {
                String crashPath = FileUtils.with(app).getPersonalSpace(app) + "/crash-reports/";
                if (FileUtils.with(app).getNewestFile(crashPath) != null) {
                    if (NetworkUtils.with(app).isConnectedTo(app)) {
                        File crashFile = FileUtils.with(app).getNewestFile(crashPath);
                        HttpUtils httpUtils = HttpUtils.get();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("app", app.getPackageName());
                        params.put("action", "crash");
                        if (!StringUtils.isEmpty(bearer)) {
                            String result = String.valueOf(httpUtils.uploadFile("https://analytics.cris16228.com/upload.php", params, httpUtils.defaultFileParams(crashFile.getAbsolutePath()), bearer));
                            Log.d("Response", result);
                        } else {
                            String result = String.valueOf(httpUtils.uploadFile("https://analytics.cris16228.com/upload.php", params, httpUtils.defaultFileParams(crashFile.getAbsolutePath())));
                            Log.d("Response", result);
                        }
                    }
                }
            }

            @Override
            public void postDelayed() {

            }
        });
        uploadCrash.execute();
        StackTraceElement[] arr = e.getStackTrace();
        StringBuilder report = new StringBuilder();
        PackageManager packageManager = app.getPackageManager();
        report.append("App: ").append(PackageUtils.with(app).getAppName(app.getPackageName())).append("\n");
        report.append("Version: ").append(PackageUtils.with(app).appFromPackage(app.getPackageName()).getLongVersionCode()).append("\n");
        report.append("Package: ").append(app.getPackageName()).append("\n");
        report.append("VersionCode: ").append(PackageUtils.with(app).appFromPackage(app.getPackageName()).versionName).append("\n");
        report.append(e).append("\n").append("\n");
        report.append("-------------------------------- Stack trace --------------------------------").append("\n");
        for (StackTraceElement stackTraceElement : arr) {
            report.append(stackTraceElement.toString()).append("\n");
        }
        report.append("-----------------------------------------------------------------------------").append("\n").append("\n");

        Throwable cause = e.getCause();
        if (cause != null) {
            report.append("----------------------------------- Cause -----------------------------------").append("\n");
            report.append(cause).append("\n");
            arr = cause.getStackTrace();
            for (StackTraceElement stackTraceElement : arr) {
                report.append(stackTraceElement.toString()).append("\n");
            }
            report.append("-----------------------------------------------------------------------------").append("\n").append("\n");
        }
        String dateTime = new SimpleDateFormat("dd-MM-yyyy_hh.mm.ss", Locale.getDefault()).format(new Date());
        String fileName = "/crash-reports/crash_" + dateTime + ".log";
        FileUtils.with(app).debugLog(report.toString(), fileName);
        defaultUEH.uncaughtException(t, e);
    }
}
