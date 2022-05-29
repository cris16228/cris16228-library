package com.github.cris16228.library.http.image_loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.github.cris16228.library.Base64Utils;
import com.github.cris16228.library.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache {

    private final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));
    private long size = 0;
    private long imageSize;
    private long limit = Long.MAX_VALUE;

    public MemoryCache() {
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void loadCache(FileCache fileCache) {
        File[] files = fileCache.getCacheDir().listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {

                if (!cache.containsKey(file.getAbsolutePath()))
                    try {
                        System.out.println(file.getAbsolutePath());
                        cache.put(file.getAbsolutePath(), getBitmap(Files.readAllBytes(file.toPath())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        for (String path : cache.keySet()) {
            System.out.println(path);
        }
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

    public boolean isCacheValid(String id, int size) {
        return sizeInBytes(cache.get(id)) == size;
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        return imageInByte.length;
    }
}
