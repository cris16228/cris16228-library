package com.github.cris16228.library.http.image_loader;

import android.content.Context;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileCache {

    private final File cacheDir;

    public FileCache(Context context) {
        cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public FileCache(String path) {
        cacheDir = new File(path);
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        String file_name = null;
        try {
            file_name = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (file_name != null) {
            return new File(cacheDir, file_name);
        }
        return null;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public long lenght() {
        long size = 0;
        size += cacheDir.length();
        for (File file : cacheDir.listFiles()) {
            if (file != null && file.isFile())
                size += file.length();
        }
        return size;
    }

    public int size() {
        File[] files = cacheDir.listFiles();
        if (files != null)
            return files.length;
        return 0;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File file : files)
            file.delete();
    }
}
