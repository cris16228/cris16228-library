package com.github.cris16228.library.http.image_loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.github.cris16228.library.Base64Utils;
import com.github.cris16228.library.FileUtils;
import com.github.cris16228.library.QueueUtils;
import com.github.cris16228.library.http.image_loader.interfaces.ConnectionErrors;
import com.github.cris16228.library.http.image_loader.interfaces.LoadImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageLoader {

    private final Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<>());
    private static final int THREAD_POOL_SIZE = 3;
    private MemoryCache memoryCache;
    private FileCache fileCache;
    private ExecutorService executor;
    private FileUtils fileUtils;
    private Context context;
    private boolean asBitmap = false;
    private Handler handler;
    private final Map<Uri, Future<?>> loadingTasks = new HashMap<>();

    public static ImageLoader with(Context context, String path) {
        ImageLoader loader = new ImageLoader();
        loader.init(context, path);
        return loader;
    }

    public static ImageLoader get() {
        return new ImageLoader();
    }

    private void init(Context context, String path) {
        fileCache = new FileCache(path);
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        handler = new Handler(Looper.getMainLooper());
        fileUtils = new FileUtils();
        this.context = context;
        memoryCache = new MemoryCache(context);
    }

    public void fileCache(Context _context) {
        fileCache = new FileCache(_context);
    }

    public void fileCache(String path) {
        fileCache = new FileCache(path);
    }

    public ImageLoader with(Context _context) {
        fileCache = new FileCache(_context);
        executor = Executors.newFixedThreadPool(3);
        fileUtils = new FileUtils();
        context = _context;
        return this;
    }

    public ImageLoader asBitmap() {
        asBitmap = true;
        return this;
    }

    public void load(String url, ImageView imageView, LoadImage loadImage, ConnectionErrors connectionErrors, DownloadProgress downloadProgress) {
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        } else {
            imageViews.put(imageView, url);
            queuePhoto(url, imageView, loadImage, connectionErrors, downloadProgress);
        }
    }

    public void download(String url, LoadImage loadImage, ConnectionErrors connectionErrors, DownloadProgress downloadProgress) {
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap == null) {
            queuePhoto(url, loadImage, connectionErrors, downloadProgress);
        }
    }

    public void load(String url, ImageView imageView, LoadImage loadImage, ConnectionErrors connectionErrors, String offlineUrl, DownloadProgress downloadProgress) {
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        } else {
            imageViews.put(imageView, offlineUrl);
            queuePhoto(url, imageView, loadImage, connectionErrors, downloadProgress);
        }
    }

    public void load(List<Object> urls, ImageView imageView, LoadImage loadImage, ConnectionErrors connectionErrors) {
        QueueUtils queueUtils = new QueueUtils();
        queueUtils.setQueue(urls);
        String url = (String) queueUtils.dequeue();
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
            load(urls, imageView, loadImage, connectionErrors);
        } else {
            imageViews.put(imageView, url);
            queuePhoto(urls, url, imageView, loadImage, connectionErrors);
        }
    }


    public void load(byte[] bytes, ImageView imageView, LoadImage loadImage, ConnectionErrors connectionErrors, DownloadProgress downloadProgress) {
        imageView.setImageBitmap(null);
        imageView.setImageDrawable(null);
        Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
        String url = encoder.encrypt(Arrays.toString(bytes), Base64.NO_WRAP, null);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        } else {
            imageViews.put(imageView, url);
            queuePhoto(bytes, imageView, loadImage, connectionErrors, downloadProgress);
        }
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

    public void queuePhoto(String url, ImageView imageView, LoadImage loadImage, ConnectionErrors connectionErrors, DownloadProgress downloadProgress) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        executor.submit(new PhotoLoader(photoToLoad, loadImage, connectionErrors, downloadProgress));
    }

    public void queuePhoto(String path, ImageView imageView, LoadImage loadImage) {
        PhotoToLoad photoToLoad = new PhotoToLoad(path, imageView);
        executor.submit(new PhotoLoader(photoToLoad, loadImage, true));
    }

    public void queuePhoto(byte[] bytes, ImageView imageView, LoadImage loadImage, ConnectionErrors connectionErrors, DownloadProgress downloadProgress) {
        PhotoToLoad photoToLoad = new PhotoToLoad(bytes, imageView);
        executor.submit(new PhotoLoader(photoToLoad, loadImage, connectionErrors, downloadProgress));
    }

    private void queuePhoto(List<Object> urls, String url, ImageView imageView, LoadImage loadImage, ConnectionErrors connectionErrors) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        executor.submit(new PhotoLoader(urls, photoToLoad, loadImage, connectionErrors));
    }

    private void queuePhoto(String url, LoadImage loadImage, ConnectionErrors connectionErrors, DownloadProgress downloadProgress) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url);
        executor.submit(new PhotoLoader(photoToLoad, loadImage, connectionErrors, downloadProgress));
    }

    public void loadVideoThumbnail(Uri videoUri, ImageView imageView, LoadImage loadImage) {
        cancelLoadingTask(videoUri);
        try {
            imageView.setImageBitmap(null);
            imageView.setImageDrawable(null);
        } catch (Exception e) {
            Log.d("loadVideoThumbnail", e.toString());
        }
        Future<?> loadingTask = executor.submit(() -> {
            File file = fileCache.getFile(videoUri.getPath());

            Bitmap thumbnail = memoryCache.get(videoUri.getPath());
            if (thumbnail != null) {
                handler.post(() -> imageView.setImageBitmap(thumbnail));
            } else {
                imageViews.put(imageView, videoUri.getPath());
                queuePhoto(videoUri.getPath(), imageView, loadImage);
            }
        });
        loadingTasks.put(videoUri, loadingTask);
    }

    public void cancelAllLoadingTasks() {
        for (Future<?> loadingTask : loadingTasks.values()) {
            loadingTask.cancel(true);
        }
        loadingTasks.clear();
    }

    private void cancelLoadingTask(Uri videoUri) {
        Future<?> loadingTask = loadingTasks.get(videoUri);
        if (loadingTask != null) {
            loadingTask.cancel(true);
            loadingTasks.remove(videoUri);
        }
    }

    private InputStream bitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bitmapArray = outputStream.toByteArray();
        return new ByteArrayInputStream(bitmapArray);
    }

//    public Bitmap getVideoThumbnail(Uri videoUri, boolean randomFrames) {
//        return getVideoThumbnail(videoUri);
//    }
//    public Bitmap getVideoImage(Uri videoUri, boolean randomFrames) {
//        return getVideoThumbnail(videoUri);
//    }

    public Bitmap getVideoThumbnail(Uri videoUri) {
        File file = fileCache.getFile(videoUri.getPath());
        Bitmap thumbnail;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, videoUri);

            Bitmap _image = fileUtils.decodeFile(file);
            if (_image != null)
                return _image;
            thumbnail = retriever.getFrameAtTime((long) new Random().nextInt(15000) + 5000);
            assert thumbnail != null;
            InputStream is = bitmapToInputStream(thumbnail);
            OutputStream os = Files.newOutputStream(file.toPath());
            fileUtils.copyStream(is, os, is.available());
            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    retriever.close();
                }
            } catch (RuntimeException | IOException e) {
                e.printStackTrace();
            }
        }
        return fileUtils.decodeFile(file);
    }

    private Bitmap getBitmap(String url, ConnectionErrors connectionErrors, DownloadProgress downloadProgress) {
        File file = fileCache.getFile(url);
        Bitmap _image = fileUtils.decodeFile(file);
        if (_image != null)
            return _image;
        try {
            Bitmap _webImage;
            URL imageURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setConnectTimeout(0);
            connection.setReadTimeout(0);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept-Encoding", "identity");
            InputStream is = connection.getInputStream();
            OutputStream os = new FileOutputStream(file);
            if (downloadProgress != null) {
                fileUtils.copyStream(is, os, connection.getContentLength(), downloadProgress);
            } else {
                fileUtils.copyStream(is, os, connection.getContentLength());
            }
            connection.disconnect();
            os.close();
            is.close();
            _webImage = fileUtils.decodeFile(file);
            return _webImage;
        } catch (OutOfMemoryError outOfMemoryError) {
            if (connectionErrors != null)
                connectionErrors.OutOfMemory(memoryCache);
            else
                memoryCache.clear();
            return null;
        } catch (FileNotFoundException fileNotFoundException) {
            if (connectionErrors != null)
                connectionErrors.FileNotFound(url);
            return null;
        } catch (IOException ioException) {
            if (connectionErrors != null)
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
        executor.shutdown();
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

        public PhotoToLoad(String _url) {
            url = _url;
        }
    }

    class PhotoLoader implements Runnable {

        public List<Object> urls;
        PhotoToLoad photoToLoad;
        LoadImage loadImage;
        ConnectionErrors connectionErrors;
        DownloadProgress downloadProgress;
        private Bitmap bitmap;
        private boolean local;


        PhotoLoader(PhotoToLoad _photoToLoad, LoadImage _loadImage, ConnectionErrors _connectionErrors, DownloadProgress _downloadProgress) {
            photoToLoad = _photoToLoad;
            loadImage = _loadImage;
            connectionErrors = _connectionErrors;
            downloadProgress = _downloadProgress;
        }

        PhotoLoader(PhotoToLoad _photoToLoad, LoadImage _loadImage, boolean _local) {
            photoToLoad = _photoToLoad;
            loadImage = _loadImage;
            local = _local;
            bitmap = getVideoThumbnail(Uri.parse(_photoToLoad.url));
        }

        PhotoLoader(List<Object> _urls, PhotoToLoad _photoToLoad, LoadImage _loadImage, ConnectionErrors _connectionErrors) {
            photoToLoad = _photoToLoad;
            loadImage = _loadImage;
            connectionErrors = _connectionErrors;
            urls = _urls;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (asBitmap) {
                bitmap = getBitmap(photoToLoad.bytes);
                Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
                String bytes = encoder.encrypt(Arrays.toString(photoToLoad.bytes), Base64.NO_WRAP, null);
                memoryCache.put(bytes, bitmap);
            } else {
                if (local) {
                    memoryCache.put(photoToLoad.url, bitmap);
                } else {
                    bitmap = getBitmap(photoToLoad.url, connectionErrors, downloadProgress);
                    memoryCache.put(photoToLoad.url, bitmap);
                }
            }
            if (imageViewReused(photoToLoad))
                return;
            Displacer displacer;
            if (urls != null && !urls.isEmpty()) {
                displacer = new Displacer(urls, bitmap, photoToLoad, loadImage, connectionErrors);
            } else {
                displacer = new Displacer(bitmap, photoToLoad, loadImage);
            }
            executor.execute(displacer);
            photoToLoad.imageView.invalidate();
        }
    }

    public class Displacer implements Runnable {

        public List<Object> urls;
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        LoadImage loadImage;
        ConnectionErrors connectionErrors;

        public Displacer(Bitmap bitmap, PhotoToLoad photoToLoad, LoadImage _loadImage) {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
            this.loadImage = _loadImage;
        }

        public Displacer(List<Object> _urls, Bitmap bitmap, PhotoToLoad photoToLoad, LoadImage _loadImage, ConnectionErrors _connectionErrors) {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
            this.loadImage = _loadImage;
            connectionErrors = _connectionErrors;
            urls = _urls;
        }

        @Override
        public void run() {
            handler.post(() -> {
                if (imageViewReused(photoToLoad))
                    return;
                if (bitmap != null) {
                    if (loadImage != null)
                        loadImage.onSuccess(bitmap);
                    if (photoToLoad.imageView != null) {
                        photoToLoad.imageView.setImageBitmap(bitmap);
                        photoToLoad.imageView.invalidate();
                        if (urls != null && !urls.isEmpty()) {
                            load(urls, photoToLoad.imageView, loadImage, connectionErrors);
                        }
                    }
                } else {
                    if (loadImage != null)
                        loadImage.onFail();
                }
            });
        }
    }

    public interface DownloadProgress {

        void downloadInProgress(Long progress, long total);

        void downloadComplete();
    }
}
