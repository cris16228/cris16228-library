package com.github.cris16228.library.http.json_model;

import com.github.cris16228.library.AsyncUtils;
import com.github.cris16228.library.http.HttpUtils;
import com.google.gson.Gson;

public class GetStoreInfo {


    App app = new App();
    int pos = -1;
    Store store;

    public App getApp(String store_url, String packageName) {
        AsyncUtils.get().onExecuteListener(new AsyncUtils.onExecuteListener() {
            @Override
            public void doInBackground() {
                store = new Gson().fromJson(HttpUtils.getJSON(store_url, false), Store.class);
                for (int i = 0; i < store.getApps().size(); i++) {
                    if (store.getApps().get(i).getPackageName().equals(packageName)) {
                        pos = i;
                        break;
                    }
                }
            }

            @Override
            public void postDelayed() {
                app = store.getApps().get(pos);
            }
        });
        return null;
    }
}
