package com.github.cris16228.library;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionsUtils {

    private Context context;
    private Activity activity;

    public static PermissionsUtils get(Context context, Activity activity) {
        PermissionsUtils permissionsUtils = new PermissionsUtils();
        permissionsUtils.context = context;
        permissionsUtils.activity = activity;
        return permissionsUtils;
    }

    public void checkPermission(String permission, int requestCode, permissionResult permissionResult) {
        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission) || Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
            List<String> permissions = new ArrayList<>();
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            checkPermissions(permissions, new ArrayList<>(), permissionResult);
        }
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        } else {
            permissionResult.OnSuccess();
        }
    }

    public void checkPermissions(List<String> permissions, List<Integer> requestCodes, permissionResult permissionResult) {
        if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            } else {
                permissionResult.OnSuccess();
            }
        }
        for (String permission : permissions) {
            for (int requestCode : requestCodes) {
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                } else {
                    permissionResult.OnSuccess();
                }
            }
        }
    }

    public interface permissionResult {
        void OnSuccess();
    }
}
