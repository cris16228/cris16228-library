package com.github.cris16228.library.http.updater;

import android.Manifest;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.github.cris16228.library.AsyncUtils;
import com.github.cris16228.library.DownloadController;
import com.github.cris16228.library.NetworkUtils;
import com.github.cris16228.library.R;
import com.github.cris16228.library.http.HttpUtils;
import com.github.cris16228.library.http.json_model.Store;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateChecker {

    private final WeakReference<Activity> weakActivity;
    private final Download download;
    private final int app_patch;
    private final String app_version;
    private final String app_name;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    String json_link;
    String download_link;
    DownloadController downloadController;
    NetworkUtils networkUtils;
    private String packageName;
    private String store_url;
    private int patch = -1;
    private String version = "";

    public UpdateChecker(Activity activity, Download download, int _patch, String _version, String _app_name, String packageName, String store_url) {
        this.weakActivity = new WeakReference<>(activity);
        this.download = download;
        this.app_patch = _patch;
        this.app_version = _version;
        this.app_name = _app_name;
        this.packageName = packageName;
        this.store_url = store_url;
        if (networkUtils == null)
            networkUtils = new NetworkUtils();
    }

    public UpdateChecker(Activity activity, String _json_link, String _download_link, Download download, int _patch, String _version, String _app_name,
                         NetworkUtils _networkUtils) {
        this.weakActivity = new WeakReference<>(activity);
        this.json_link = _json_link;
        this.download_link = _download_link;
        this.download = download;
        this.app_patch = _patch;
        this.app_version = _version;
        this.app_name = _app_name;
        this.networkUtils = _networkUtils;
    }

    public void check() {
        executor.execute(() -> {
            if (TextUtils.isEmpty(json_link)) {
                Log.e(UpdateChecker.class.getSimpleName(), weakActivity.get().getResources().getString(R.string.json_link_invalid));
                return;
            }
            if (TextUtils.isEmpty(download_link)) {
                Log.e(UpdateChecker.class.getSimpleName(), weakActivity.get().getResources().getString(R.string.download_link_invalid));
                return;
            }
            if (download == Download.JSON) {
                AsyncUtils asyncUtils = AsyncUtils.get();
                asyncUtils.onExecuteListener(new AsyncUtils.onExecuteListener() {
                    @Override
                    public void doInBackground() {
                        Store store = new Gson().fromJson(HttpUtils.getJSON(store_url, false), Store.class);
                        for (int i = 0; i < store.getApps().size(); i++) {
                            if (store.getApps().get(i).getPackageName().equals(packageName)) {
                                patch = store.getApps().get(i).getVersion();
                                version = store.getApps().get(i).getVersionCode();
                                break;
                            }
                        }
                    }

                    @Override
                    public void postDelayed() {

                    }
                });
                asyncUtils.execute();
            } else if (download == Download.UPDATE) {
                downloadController = new DownloadController(weakActivity.get(), download_link, app_name);
                downloadController.enqueueDownload();
            }
            handler.post(() -> {
                Activity activity = weakActivity.get();
                if (activity == null
                        || activity.isFinishing()
                        || activity.isDestroyed()) {
                    // activity is no longer valid, don't do anything!
                    return;
                }
               /* if (TextUtils.isEmpty(PrefUtils.PREF))
                    PrefUtils.with(weakActivity.get()).setSharedPref("updater");
                version = PrefUtils.with(weakActivity.get()).getString("version", version);
                patch = PrefUtils.with(weakActivity.get()).getInt("patch", patch);
                System.out.println("Version " + version + " (" + patch + ")");*/
                if (patch == -1) {
                    Snackbar.make(activity.findViewById(android.R.id.content).getRootView(), "Error occurred while trying to check for updates", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.isEmpty(version) && !version.equals(app_version) && download == Download.JSON) {
                    MaterialAlertDialogBuilder new_update = new MaterialAlertDialogBuilder(activity);
                    new_update.setTitle(activity.getApplicationContext().getResources().getString(R.string.new_update_title_app, version));
                    new_update.setMessage(activity.getApplicationContext().getResources().getString(R.string.new_update_message,
                            activity.getApplicationContext().getResources().getString(R.string.app_update)));
                    new_update.setPositiveButton(R.string.ok, (dialog, which) -> Dexter.withContext(weakActivity.get()).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                new_update.create().dismiss();
                                if (networkUtils.no_internet == null)
                                    networkUtils.showNoInternet(activity);
                                if (!networkUtils.isConnectedTo(activity.getApplicationContext()))
                                    networkUtils.no_internet.show();
                                else {
                                    UpdateChecker checker = new UpdateChecker(activity, Download.UPDATE, app_patch, app_version, app_name,
                                            packageName, store_url);
                                    checker.check();
                                }
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check());
                    new_update.setNegativeButton(R.string.cancel, (dialog, which) -> {
                    });
                    new_update.create().show();
                    return;
                }
                if (patch > 0 && patch > app_patch && download == Download.JSON) {
                    MaterialAlertDialogBuilder new_update = new MaterialAlertDialogBuilder(activity);
                    new_update.setTitle(activity.getApplicationContext().getResources().getString(R.string.new_update_title_patch, patch));
                    new_update.setMessage(activity.getApplicationContext().getResources().getString(R.string.new_update_message,
                            activity.getApplicationContext().getResources().getString(R.string.patch)));
                    new_update.setPositiveButton(R.string.ok, (dialog, which) -> Dexter.withContext(weakActivity.get()).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                new_update.create().dismiss();
                                if (networkUtils.no_internet == null)
                                    networkUtils.showNoInternet(activity);
                                if (!networkUtils.isConnectedTo(activity.getApplicationContext()))
                                    networkUtils.no_internet.show();
                                else {
                                    UpdateChecker checker = new UpdateChecker(activity, Download.UPDATE, app_patch, app_version, app_name,
                                            packageName, store_url);
                                    checker.check();
                                }
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check());
                    new_update.setNegativeButton(R.string.cancel, (dialog, which) -> {
                    }).create();

                    new_update.create().show();
                }
            });
        });
    }

    public enum Download {
        JSON,
        UPDATE
    }
}
