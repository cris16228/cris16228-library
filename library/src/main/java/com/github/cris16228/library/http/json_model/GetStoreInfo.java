package com.github.cris16228.library.http.json_model;

import com.github.cris16228.library.AsyncUtils;
import com.github.cris16228.library.http.HttpUtils;
import com.google.gson.Gson;

public class GetStoreInfo {


    App app = null;
    int pos = -1;
    Store store;

    public App getApp(String store_url, String packageName) {
        AsyncUtils asyncUtils = AsyncUtils.get();
        asyncUtils.onExecuteListener(new AsyncUtils.onExecuteListener() {
            @Override
            public void doInBackground() {
                store = new Gson().fromJson(HttpUtils.getJSON(store_url, true), Store.class);
                for (int i = 0; i < store.getApps().size(); i++) {
                    System.out.println(store.getApps().get(i).getPackageName() + "/" + packageName);
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
