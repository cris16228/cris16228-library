package com.github.cris16228.library.http.image_loader;

import android.graphics.Bitmap;

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

    public boolean isCacheValid(String id, Bitmap bitmap) {
        try {
            if (!cache.containsKey(id))
                return false;
            if (cache.get(id) == null)
                return false;
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
