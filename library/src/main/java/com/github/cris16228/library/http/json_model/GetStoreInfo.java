package com.github.cris16228.library.http.json_model;

import com.github.cris16228.library.AsyncUtils;
import com.github.cris16228.library.http.HttpUtils;
import com.google.gson.Gson;

public class GetStoreInfo {


    App app = null;
    int pos = -1;
    Store store;

    public App getApp() {
        return app;
    }

    public int getPos() {
        return pos;
    }

    public Store getStore() {
        return store;
    }

    public App getApp(String store_url, String packageName) {
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
                app = store.getApps().get(pos);
            }
        });
        asyncUtils.execute();
        return app;
    }
}
