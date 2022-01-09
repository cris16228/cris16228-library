package com.github.cris16228.library;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresPermission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileUtils {

    private ArrayList<String> folderName;
    private int position;

    public static FileUtils with(ArrayList<String> folderName, int position) {
        FileUtils fileUtils = new FileUtils();
        fileUtils.folderName = folderName;
        fileUtils.position = position;
        return fileUtils;
    }

    public boolean isFileValid(String path) {
        File tmp = new File(path);
        return tmp.length() > 0 && tmp.exists();
    }

    public boolean isFileValid(File tmp) {
        return tmp.length() > 0 && tmp.exists();
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

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
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

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
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
}
