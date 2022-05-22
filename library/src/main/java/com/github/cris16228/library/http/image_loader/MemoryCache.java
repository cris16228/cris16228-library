package com.github.cris16228.library.http.image_loader;

import android.graphics.Bitmap;

import com.github.cris16228.library.AsyncUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache {

    private final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));
    private long size = 0;
    private long limit = Long.MAX_VALUE;

    public MemoryCache() {
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void loadCache(ImageLoader imageLoader, FileCache fileCache) {
        File[] files = fileCache.getCacheDir().listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                try {
                    cache.put(file.getAbsolutePath(), imageLoader.getBitmap(Files.readAllBytes(file.toPath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        for (String path : cache.keySet()) {
            System.out.println(path);
        }
    }

    public void setLimit(long _limit) {
        limit = _limit;
    }


    public Bitmap get(String id) {
        try {
            if (!cache.containsKey(id))
                return null;
            AsyncUtils checkSize = AsyncUtils.get();
            checkSize.onExecuteListener(new AsyncUtils.onExecuteListener() {
                @Override
                public void preExecute() {

                }

                @Override
                public void doInBackground() {
                    try {
                        URL imageURL = new URL(id);
                        HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
                        connection.setConnectTimeout(120000);
                        connection.setReadTimeout(120000);
                        connection.setInstanceFollowRedirects(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void postDelayed() {
                    sizeInBytes(cache.get(id));
                }
            });
            checkSize.execute();
            return cache.get(id);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void remove(String id) {
        try {
            if (cache.containsKey(id)) {
                size -= sizeInBytes(cache.get(id));
                cache.remove(id);
                checkSize();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public boolean isCacheValid(String id, Bitmap bitmap) {
        System.out.println(id == null ? "id null" : id);
        System.out.println(cache.get(id) == null ? "cache.get(id) null" : cache.get(id));
        try {
            if (!cache.containsKey(id))
                return false;
            if (cache.get(id) == null)
                return false;
            System.out.println("isCacheValid: " + (sizeInBytes(cache.get(id)) == sizeInBytes(bitmap)));
            return sizeInBytes(cache.get(id)) == sizeInBytes(bitmap);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id))
                size -= sizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += sizeInBytes(bitmap);
            checkSize();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private void checkSize() {
        if (size > limit) {
            Iterator<Map.Entry<String, Bitmap>> iterator = cache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Bitmap> entry = iterator.next();
                size -= sizeInBytes(entry.getValue());
                iterator.remove();
                if (size <= limit)
                    break;
            }
        }
    }

    public void clear() {
        try {
            cache.clear();
            size = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    long sizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return (long) bitmap.getRowBytes() * bitmap.getHeight();
    }
}
