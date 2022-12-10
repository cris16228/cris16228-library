package com.github.cris16228.library;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.github.cris16228.library.http.image_loader.interfaces.ConnectionErrors;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

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

    public String getPersonalSpace(Context _context) {
        return _context.getExternalFilesDir(null).getAbsolutePath();
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

    //Get saved base64 Image
    public Bitmap getBitmap(String base64String) {
        Base64Utils.Base64Decoder decoder = new Base64Utils.Base64Decoder();
        InputStream is = new ByteArrayInputStream(decoder.decryptV2(base64String));
        return BitmapFactory.decodeStream(is);
    }

    //Save image to base64
    public String saveBitmap(Bitmap bitmap) {
        Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return encoder.encrypt(bytes, Base64.DEFAULT);
    }

    private void saveImage(Bitmap bitmap, String folderName) throws LibraryException, IOException {
        if (context == null)
            throw new LibraryException(FileUtils.class, "context is null!! Please use a valid context");
        String name = "Wallpaper_" + System.currentTimeMillis() + ".png";
        File path = context.getExternalFilesDir(null);
        File dir = new File(path + "/" + folderName);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, name);
        OutputStream out = Files.newOutputStream(file.toPath());
        BufferedOutputStream bos = new BufferedOutputStream(out);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        bos.flush();
        bos.close();
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

    public String readJson(String file) {
        return readFile(file);
    }

    public String readFile(String file) {
        File f = new File(file);
        StringBuilder text = new StringBuilder();
        if (!f.exists()) {
            Log.e("readFiles", "The file doesn't exists");
            return "";
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof FileNotFoundException)
                try {
                    throw new LibraryException(getClass(), "File " + file + " not found!");
                } catch (LibraryException ex) {
                    ex.printStackTrace();
                }
            return "";
        }
        return text.toString();
    }

    public File getNewestFile(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        File fileChosen = null;
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() > lastModifiedTime) {
                    fileChosen = file;
                    lastModifiedTime = file.lastModified();

                }
            }
        }
        return fileChosen;
    }

    public String binaryFileToString(String path, boolean print) {
        File file = new File(path);
        String text = "";
        if (!file.exists()) return "";
        if (!StringUtils.isEmpty(StringUtils.binaryToString(readFile(file.getAbsolutePath()), print))) {
            text = StringUtils.binaryToString(readFile(file.getAbsolutePath()), print);
        }
        return text;
    }

    public void writeFile(String file, String text) {
        File f = new File(file);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.append(text);
            osw.flush();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String file, String text, boolean lineSeparator) {
        File f = new File(file);
        if (!f.exists()) {
            try {
                new File(f.getParent()).mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String separator = System.getProperty("line.separator");
            FileOutputStream fos = new FileOutputStream(f, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.append(text);
            if (lineSeparator)
                osw.append(separator);
            osw.flush();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void debugLog(String text) {
        String path = getPersonalSpace(context) + "/debug.log";
        Log(path, text);
    }

    public void debugLog(String text, String fileName) {
        String path = getPersonalSpace(context) + fileName;
        Log(path, text);
    }

    public void debugLog(String text, String fileName, String path) {
        path += fileName;
        Log(path, text);
    }

    public void Log(String path, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(new DateTimeUtils().getDateTime(new Date().getTime(), null)).append("]: ").append(text);
        writeFile(path, sb.toString(), true);
    }

    public void writeJson(String file, String json) {
        writeFile(file, json);
    }

    public String getFileName(File file) {
        return file.getName();
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
