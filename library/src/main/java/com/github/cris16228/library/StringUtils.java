package com.github.cris16228.library;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.github.cris16228.library.models.UUID;
import com.github.cris16228.library.models.UUIDS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class StringUtils {

    Context context;

    public static StringUtils with(Context context) {
        StringUtils StringUtils = new StringUtils();
        StringUtils.context = context;
        return StringUtils;
    }

    static char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public static String randomUUID(int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int randIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randIndex];
        }
        return new String(result);
    }

    public String[] UUID(int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int randIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randIndex];
        }
        UUID uuid = new UUID();
        uuid.setUuid(new String(result));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UUIDS uuidList = gson.fromJson(FileUtils.with(context).readJson(FileUtils.with(context).getPersonalSpace(context) + "/uuids.json"), UUIDS.class);
        if (!uuidList.getUuids().contains(uuid)) {
            FileUtils.with(context).writeJson(FileUtils.with(context).getPersonalSpace(context) + "/uuids.json", gson.toJson(uuidList));
            return new String[]{new String(result), ""};
        } else
            return UUID(length, uuidList.getUuids());
    }

    public String[] UUID(int length, List<UUID> uuidList) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int randIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randIndex];
        }
        UUID uuid = new UUID();
        uuid.setUuid(new String(result));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!uuidList.contains(uuid)) {
            FileUtils.with(context).writeJson(FileUtils.with(context).getPersonalSpace(context) + "/uuids.json", gson.toJson(uuidList));
            return new String[]{new String(result), String.valueOf(uuidList)};
        } else
            return UUID(length, uuidList);
    }

    public static String stringToBinary(String text, boolean print) {
        StringBuilder bString = new StringBuilder();
        byte[] bytes = text.getBytes();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                bString.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            bString.append(" ");
        }
        if (print)
            Log.i("stringToBinary", bString.toString());
        return bString.toString();
    }


    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String binaryToString(String binaryCode, boolean print) {
        String[] code = binaryCode.split(" ");
        StringBuilder word = new StringBuilder();
        for (String s : code) {
            word.append((char) Integer.parseInt(s, 2));
        }
        if (print)
            Log.i("stringToBinary", word.toString());
        return word.toString();
    }


    public static String convertToFirstUpper(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
