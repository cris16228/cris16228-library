package com.github.cris16228.library.http;

import android.text.TextUtils;
import android.util.Log;

import com.github.cris16228.library.FileUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

    private int readTimeout = 10000;
    private int connectionTimeout = 15000;
    String uploadServerUri = "http://192.168.1.11/upload.php";
    int serverResponseCode = 0;

    public static HttpUtils get() {
        return new HttpUtils();
    }

    public int uploadFile(String _uploadServerUri, String filePath) {
        if (!TextUtils.isEmpty(_uploadServerUri))
            uploadServerUri = _uploadServerUri;
        HttpURLConnection connection = null;
        DataOutputStream dos = null;
        String endLine = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        FileUtils fileUtils = new FileUtils();
        byte[] buffer;
        int maxBuffer = 1024 * 1024;
        File sourceFile = new File(filePath);
        String fileName = fileUtils.getFileName(sourceFile);
        if (!sourceFile.isFile()) {
            return 0;
        } else {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                URL url = new URL(uploadServerUri);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", filePath);

                dos = new DataOutputStream(connection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + endLine);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + endLine);
                dos.writeBytes(endLine);

                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBuffer);
                buffer = new byte[bufferSize];

                bytesRead = fis.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable, maxBuffer);
                    bytesRead = fis.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(endLine);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + endLine);
                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                Log.i(getClass().getSimpleName(), "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {
                    Log.i(getClass().getSimpleName(), "File Upload Complete.");
                }
                fis.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;
        }
    }


    public static String getJSON(String urlString, boolean printJSON) {
        HttpURLConnection urlConnection;
        StringBuilder sb = new StringBuilder();
        String jsonString = null;
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpUtils httpUtils = new HttpUtils();
        try {
            if (url != null) {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(httpUtils.readTimeout /* milliseconds */);
                urlConnection.setConnectTimeout(httpUtils.connectionTimeout /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                jsonString = sb.toString();
                if (printJSON)
                    System.out.println("JSON: " + jsonString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HttpUtils setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public HttpUtils setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public boolean isOnline(String site) throws MalformedURLException {
        URL url = new URL(site);
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Android Application: 1.0");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1000 * 5); // mTimeout is in seconds
            urlConnection.connect();
            return urlConnection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
