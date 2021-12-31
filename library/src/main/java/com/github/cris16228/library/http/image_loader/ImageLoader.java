package com.github.cris16228.library.http.image_loader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
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
    ExecutorService executor;
    private String url;
    private ImageView imageView;
    private FileUtils fileUtils;
    private int timeout = 0;
    private Context context;

    public static ImageLoader with(Context _context) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.fileCache = new FileCache(_context);
        imageLoader.executor = Executors.newFixedThreadPool(3);
        imageLoader.fileUtils = new FileUtils();
        imageLoader.context = _context;
        return imageLoader;
    }

    public void into(ImageView _imageView) {
        imageViews.put(imageView, url);
        imageView = _imageView;
        Bitmap img = memoryCache.get(url);
        Log.i("", "Called into(): " + (img == null));
        if (img != null)
            _imageView.setImageDrawable(new BitmapDrawable(context.getResources(), img));
        else
            queuePhoto();
    }

    public ImageLoader timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ImageLoader load(String _url) {
        url = _url;
        Log.i("ImageLoader", "URL: " + _url);
        return this;
    }

    public void queuePhoto() {
        Log.i("", "Called queuePhoto()");
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        executor.submit(new PhotoLoader(photoToLoad));
    }

    private Bitmap getBitmap() {
        File file = fileCache.getFile(url);
        Log.i("ImageLoader", "File: " + file.getAbsolutePath());
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

            Log.i("", "imageViewReused(photoToLoad)): " + imageViewReused(photoToLoad));
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bitmap = getBitmap();
            memoryCache.put(url, bitmap);
            Log.i("", "imageViewReused(photoToLoad)): " + imageViewReused(photoToLoad));
            if (imageViewReused(photoToLoad))
                return;
            Displayer displayer = new Displayer(bitmap, photoToLoad);
            Activity activity = (Activity) photoToLoad.imageView.getContext();
            activity.runOnUiThread(displayer);
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
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
            /*else
                photoToLoad.imageView.setImageBitmap(null);*/
        }
    }
}
