package com.github.cris16228.library;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.github.cris16228.library.http.image_loader.interfaces.ConnectionErrors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileUtils {

    private ArrayList<String> folderName;
    private int position;
    private Context context;

    public FileUtils() {
    }

    public static FileUtils with(ArrayList<String> folderName, int position) {
        FileUtils fileUtils = new FileUtils();
        fileUtils.folderName = folderName;
        fileUtils.position = position;
        return fileUtils;
    }

    public static FileUtils with(Context context) {
        FileUtils fileUtils = new FileUtils();
        fileUtils.context = context;
        return fileUtils;
    }

    public boolean isFileValid(String path) {
        File tmp = new File(path);
        return tmp.exists() && tmp.length() > 0;
    }

    public boolean isFileValid(File tmp) {
        return tmp.exists() && tmp.length() > 0;
    }

    public String getMD5Hash(String input) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getIndex() throws LibraryException {
        if (folderName == null || folderName.isEmpty())
            throw new LibraryException(getClass(), "\"folderName\" is empty or null");
        return folderName.get(position).lastIndexOf("/");
    }

    public boolean isSD() throws LibraryException {
        if (folderName == null || folderName.isEmpty())
            throw new LibraryException(getClass(), "\"folderName\" is empty or null");
        return folderName.get(position).startsWith(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public void displaySDCard(ImageView sd_card) throws LibraryException {
        if (isSD())
            sd_card.setVisibility(View.VISIBLE);
        else
            sd_card.setVisibility(View.GONE);
    }

    public boolean isFileMimeType(String path, MimeTypes mimeType) {
        File tmp = new File(path);
        Uri uri = Uri.fromFile(tmp);
        return getMimeType(uri).startsWith(mimeType.getValue());
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public Bitmap decodeFile(File file) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            int final_width, final_height;
            final_width = width_tmp /= 2;
            final_height = height_tmp /= 2;
            while (width_tmp / 2 >= final_width && height_tmp / 2 >= final_height) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options _options = new BitmapFactory.Options();
            _options.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, _options);
        } catch (FileNotFoundException exception) {
            return null;
        }
    }

    public Bitmap getBitmap(String url, ConnectionErrors connectionErrors) {
        try {
            URL imageURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(3000);
            connection.setInstanceFollowRedirects(true);
            InputStream is = connection.getInputStream();
            return BitmapFactory.decodeStream(is);
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

    public Bitmap decodeFile(File file, int size) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            if (size <= 0)
                size = 512;
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            while (width_tmp / 2 >= size && height_tmp / 2 >= size) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options _options = new BitmapFactory.Options();
            _options.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, _options);
        } catch (FileNotFoundException exception) {
            return null;
        }
    }

    public void copyStream(InputStream is, OutputStream os) {
        try {
            byte[] data = new byte[16384];
            int count;
            while ((count = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum MimeTypes {
        VIDEO("video/"),
        IMAGE("image/"),
        AUDIO("audio/"),
        TEXT("text/");

        private final String value;

        MimeTypes(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.getValue();
        }
    }
}
