package com.github.cris16228.library.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

    private int readTimeout = 10000;
    private int connectionTimeout = 15000;

    public static HttpUtils get() {
        return new HttpUtils();
    }


    public static String getJSON(String urlString, boolean printJSON) throws IOException {
        HttpURLConnection urlConnection;
        URL url = new URL(urlString);
        HttpUtils httpUtils = new HttpUtils();
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(httpUtils.readTimeout /* milliseconds */);
        urlConnection.setConnectTimeout(httpUtils.connectionTimeout /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();

        String jsonString = sb.toString();
        if (printJSON)
            System.out.println("JSON: " + jsonString);

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
