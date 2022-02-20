package com.github.cris16228.library.http.image_loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.github.cris16228.library.Base64Utils;
import com.github.cris16228.library.FileUtils;
import com.github.cris16228.library.http.image_loader.interfaces.ConnectionErrors;
import com.github.cris16228.library.http.image_loader.interfaces.LoadImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
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
    private boolean asBitmap = false;
    private ConnectionErrors connectionErrors;

    public static ImageLoader with(Context _context) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.fileCache = new FileCache(_context);
        imageLoader.executor = Executors.newFixedThreadPool(3);
        imageLoader.fileUtils = new FileUtils();
        imageLoader.context = _context;
        return imageLoader;
    }

    public static ImageLoader with(Context _context, String path) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.fileCache = new FileCache(path);
        imageLoader.executor = Executors.newFixedThreadPool(3);
        imageLoader.fileUtils = new FileUtils();
        imageLoader.context = _context;
        return imageLoader;
    }

    public void onConnectionErrors(ConnectionErrors _connectionErrors) {
        connectionErrors = _connectionErrors;
    }

    public ImageLoader asBitmap() {
        asBitmap = true;
        return this;
    }

    public ImageLoader saveToCache(String url, Bitmap bitmap) {
        memoryCache.put(url, bitmap);
        return this;
    }

    public ImageLoader timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public void load(String url, ImageView imageView, LoadImage loadImage) {
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        } else {
            queuePhoto(url, imageView, loadImage);
        }
    }

    public void load(byte[] bytes, ImageView imageView, LoadImage loadImage) {
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
        String url = encoder.encrypt(Arrays.toString(bytes), Base64.NO_WRAP, null);
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        } else
            queuePhoto(bytes, imageView, loadImage);
    }


    public void load(Bitmap bitmap, ImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }
    }

    public void load(@RawRes @DrawableRes @NonNull Integer resourceId, ImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }
    }

    public void queuePhoto(String url, ImageView imageView, LoadImage loadImage) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        executor.submit(new PhotoLoader(photoToLoad, loadImage));
    }

    public void queuePhoto(byte[] bytes, ImageView imageView, LoadImage loadImage) {
        PhotoToLoad photoToLoad = new PhotoToLoad(bytes, imageView);
        executor.submit(new PhotoLoader(photoToLoad, loadImage));
    }

    private Bitmap getBitmap(String url) {
        File file = fileCache.getFile(url);
        Bitmap _image = fileUtils.decodeFile(file);
        if (!memoryCache.isCacheValid(url, _image)) {
            memoryCache.remove(url);
            _image = null;
        }
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
        } catch (OutOfMemoryError outOfMemoryError) {
            connectionErrors.OutOfMemory(memoryCache);
            return null;
        } catch (FileNotFoundException fileNotFoundException) {
            connectionErrors.FileNotFound();
            return null;
        } catch (IOException ioException) {
            connectionErrors.NormalError();
            return null;
        }
    }

    public Bitmap getBitmap(byte[] bytes) {
        Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
        File file = fileCache.getFile(encoder.encrypt(Arrays.toString(bytes), Base64.NO_WRAP, null));
        Bitmap _image = fileUtils.decodeFile(file);
        if (_image != null)
            return _image;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
        public byte[] bytes;

        public PhotoToLoad(String _url, ImageView _imageView) {
            url = _url;
            imageView = _imageView;
        }

        public PhotoToLoad(byte[] _bytes, ImageView _imageView) {
            bytes = _bytes;
            imageView = _imageView;
        }
    }

    class PhotoLoader implements Runnable {

        PhotoToLoad photoToLoad;
        LoadImage loadImage;

        PhotoLoader(PhotoToLoad _photoToLoad, LoadImage _loadImage) {
            photoToLoad = _photoToLoad;
            loadImage = _loadImage;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bitmap;
            if (asBitmap) {
                bitmap = getBitmap(photoToLoad.bytes);
                Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
                String bytes = encoder.encrypt(Arrays.toString(photoToLoad.bytes), Base64.NO_WRAP, null);
                memoryCache.put(bytes, bitmap);
            } else {
                bitmap = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bitmap);
            }
            if (imageViewReused(photoToLoad))
                return;
            Displacer displayer = new Displacer(bitmap, photoToLoad, loadImage);
            executor.execute(displayer);
            photoToLoad.imageView.invalidate();
        }
    }

    public class Displacer implements Runnable {

        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        LoadImage loadImage;

        public Displacer(Bitmap bitmap, PhotoToLoad photoToLoad, LoadImage _loadImage) {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
            this.loadImage = _loadImage;
        }

        @Override
        public void run() {
            handler.post(() -> {
                if (imageViewReused(photoToLoad))
                    return;
                if (bitmap != null) {
                    loadImage.onSuccess();
                    photoToLoad.imageView.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
                } else {
                    loadImage.onFail();
                }
            });
        }
    }

}
