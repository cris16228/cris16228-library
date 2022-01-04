package com.github.cris16228.library.http.image_loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.github.cris16228.library.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private final Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private FileUtils fileUtils;
    private int timeout = 75000;
    private Context context;

    public static ImageLoader with(Context _context, boolean _compress) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.fileCache = new FileCache(_context);
        imageLoader.executor = Executors.newFixedThreadPool(3);
        imageLoader.fileUtils = new FileUtils();
        imageLoader.context = _context;
        return imageLoader;
    }

    public static ImageLoader with(Context _context, String path, boolean _compress) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.fileCache = new FileCache(path);
        imageLoader.executor = Executors.newFixedThreadPool(3);
        imageLoader.fileUtils = new FileUtils();
        imageLoader.context = _context;
        return imageLoader;
    }

    public ImageLoader timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public void load(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        } else {
            queuePhoto(url, imageView);
        }
    }

    public void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        executor.submit(new PhotoLoader(photoToLoad));
    }

    private Bitmap getBitmap(String url) {
        File file = fileCache.getFile(url);
        Bitmap _image = fileUtils.decodeFile(file);
        if (_image != null)
            return _image;
        try {
            Bitmap _webImage;
            URL imageURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(3000);
            connection.setInstanceFollowRedirects(true);
            InputStream is = connection.getInputStream();
            OutputStream os = new FileOutputStream(file);
            fileUtils.copyStream(is, os);
            os.close();
            _webImage = fileUtils.decodeFile(file);
            return _webImage;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (throwable instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    boolean imageViewReused(PhotoToLoad _photoToLoad) {
        String tag = imageViews.get(_photoToLoad.imageView);
        return tag == null || !tag.equals(_photoToLoad.url);
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    static class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String _url, ImageView _imageView) {
            url = _url;
            imageView = _imageView;
        }
    }

    class PhotoLoader implements Runnable {

        PhotoToLoad photoToLoad;

        PhotoLoader(PhotoToLoad _photoToLoad) {
            photoToLoad = _photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bitmap = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bitmap);
            if (imageViewReused(photoToLoad))
                return;
            Displayer displayer = new Displayer(bitmap, photoToLoad);
            executor.execute(displayer);
            photoToLoad.imageView.invalidate();
        }
    }

    public class Displayer implements Runnable {

        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public Displayer(Bitmap bitmap, PhotoToLoad photoToLoad) {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            handler.post(() -> {
                if (imageViewReused(photoToLoad))
                    return;
                if (bitmap != null)
                    photoToLoad.imageView.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
            /*else
                photoToLoad.imageView.setImageBitmap(null);*/
            });
        }
    }
}
