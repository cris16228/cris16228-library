package utils;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.github.cris16228.library.FileUtils;
import com.github.cris16228.library.NetworkUtils;
import com.github.cris16228.library.http.HttpUtils;
import com.github.cris16228.library.http.ServerUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler defaultUEH;
    private Activity app = null;

    public UncaughtExceptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        String crashPath = FileUtils.with(app).getPersonalSpace(app) + "/crash-reports/";
        if (FileUtils.with(app).getNewestFile(crashPath) != null) {
            if (NetworkUtils.with(app).isConnectedTo(app)) {
                File crashFile = FileUtils.with(app).getNewestFile(crashPath);
                HttpUtils httpUtils = HttpUtils.get();
                httpUtils.uploadFile(ServerUtils.get(app).getValidURL("/upload.php"), httpUtils.defaultParams(), httpUtils.defaultFileParams(crashFile.getAbsolutePath()));
            }
        }
        StackTraceElement[] arr = e.getStackTrace();
        StringBuilder report = new StringBuilder();
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
