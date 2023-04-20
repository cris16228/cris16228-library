package com.github.cris16228.library.http.json_model;

import com.github.cris16228.library.AsyncUtils;
import com.github.cris16228.library.http.HttpUtils;
import com.google.gson.Gson;

public class GetStoreInfo {


    private App app = null;
    private int pos = -1;
    private Store store;
    private onGetApp onGetApp;

    public App getApp() {
        return app;
    }

    public int getPos() {
        return pos;
    }

    public Store getStore() {
        return store;
    }

    public App getApp(String store_url, String packageName, GetStoreInfo.onGetApp onGetApp) {
        AsyncUtils asyncUtils = AsyncUtils.get();
        asyncUtils.onExecuteListener(new AsyncUtils.onExecuteListener() {
            @Override
            public void preExecute() {

            }

            @Override
            public void doInBackground() {
                store = new Gson().fromJson(HttpUtils.getJSON(store_url, false), Store.class);
                for (int i = 0; i < store.getApps().size(); i++) {
                    if (store.getApps().get(i).getPackageName().equals(packageName)) {
                        pos = i;
                    }
                }
            }

            @Override
            public void postDelayed() {
                onGetApp.onComplete(pos, app);
                app = store.getApps().get(pos);
            }
        });
        asyncUtils.execute();
        return app;
    }

    public void getAppV2(String store_url, String packageName, GetStoreInfo.onGetApp onGetApp) {
        AsyncUtils asyncUtils = AsyncUtils.get();
        asyncUtils.onExecuteListener(new AsyncUtils.onExecuteListener() {
            @Override
            public void preExecute() {

            }

            @Override
            public void doInBackground() {
                store = new Gson().fromJson(HttpUtils.getJSON(store_url, false), Store.class);
                for (int i = 0; i < store.getApps().size(); i++) {
                    if (store.getApps().get(i).getPackageName().equals(packageName)) {
                        pos = i;
                    }
                }
            }

            @Override
            public void postDelayed() {
                onGetApp.onComplete(pos, app);
                app = store.getApps().get(pos);
            }
        });
        asyncUtils.execute();
    }

    public String getLink(int index) {
        if (getStore() == null || index == -1)
            return null;
        return getStore().getApps().get(getPos()).getLinks().get(index);
    }

    public interface onGetApp {
        void onComplete(Integer pos, App app);
    }
}
