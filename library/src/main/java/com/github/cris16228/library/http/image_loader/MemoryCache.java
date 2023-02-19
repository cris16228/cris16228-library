package com.github.cris16228.library.http.image_loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.github.cris16228.library.AsyncUtils;
import com.github.cris16228.library.Base64Utils;
import com.github.cris16228.library.FileUtils;
import com.github.cris16228.library.http.image_loader.models.CacheModel;
import com.github.cris16228.library.http.image_loader.models.ImageModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache {

    private final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));
    private long size = 0;
    private long imageSize;
    private long limit = 1000000;

    private CacheModel imageCache;
    private FileCache fileCache;
    private Context context;

    public MemoryCache() {
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void loadCache(String path) {
        AsyncUtils loadCache = AsyncUtils.get();
        loadCache.onExecuteListener(new AsyncUtils.onExecuteListener() {
            @Override
            public void preExecute() {
                cache.clear();
            }

            @Override
            public void doInBackground() {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileUtils fileUtils = new FileUtils();
                imageCache = gson.fromJson(fileUtils.readJson(fileCache.getCacheDir() + "/images.json"), CacheModel.class);
                if (imageCache == null) {
                    imageCache = new CacheModel();
                    imageCache.imageModelList = new ArrayList<>();
                }
            }

            @Override
            public void postDelayed() {

            }
        });
        loadCache.execute();
    }

    public Bitmap getBitmap(byte[] bytes) {
        Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
        FileUtils fileUtils = new FileUtils();
        File file = new File(encoder.encrypt(Arrays.toString(bytes), Base64.NO_WRAP, null));
        Bitmap _image = fileUtils.decodeFile(file);
        if (_image != null)
            return _image;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void setLimit(long _limit) {
        limit = _limit;
    }


    public Bitmap get(String id) {
        try {
            if (!cache.containsKey(id))
                return null;
            return cache.get(id);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id))
                size -= sizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += sizeInBytes(bitmap);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileUtils fileUtils = new FileUtils();
            imageCache.imageModelList.add(new ImageModel(id));
            fileUtils.writeJson(fileCache.getCacheDir() + "/images.json", gson.toJson(imageCache.getImageModelList()));
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
