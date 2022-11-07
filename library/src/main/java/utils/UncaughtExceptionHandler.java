package utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.cris16228.library.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler defaultUEH;
    private final boolean sendMail;
    private Activity app = null;

    public UncaughtExceptionHandler(Activity app, boolean sendMail) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
        this.sendMail = sendMail;
    }

    public UncaughtExceptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
        this.sendMail = false;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        StackTraceElement[] arr = e.getStackTrace();
        StringBuilder report = new StringBuilder();
        report.append(e).append("\n").append("\n");
        report.append("-------------------------------- Stack trace --------------------------------");
        for (StackTraceElement stackTraceElement : arr) {
            report.append(stackTraceElement.toString()).append("\n");
        }
        report.append("-----------------------------------------------------------------------------").append("\n");

        report.append("----------------------------------- Cause -----------------------------------").append("\n");
        Throwable cause = e.getCause();
        if (cause != null) {
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
        if (sendMail) {
            ApplicationInfo applicationInfo = app.getApplicationInfo();
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String subject = "Crash report";
            String body = "There was a problem with " + (applicationInfo.labelRes == 0 ?
                    applicationInfo.nonLocalizedLabel.toString() :
                    app.getString(applicationInfo.labelRes) + "(" + applicationInfo.packageName + ")");
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"cpiva16@gmail.com"});
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            Uri uri = Uri.fromFile(new File(FileUtils.with(app).getPersonalSpace(app) + fileName));
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            sendIntent.setType("message/rfc822");
        }
        defaultUEH.uncaughtException(t, e);
    }
}
