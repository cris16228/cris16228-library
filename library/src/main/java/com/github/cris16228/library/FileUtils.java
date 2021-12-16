package com.github.cris16228.library;

import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

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

    private static String GetMD5Hash(String input) {
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
}
