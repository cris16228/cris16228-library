package com.github.cris16228.library;

import android.util.Log;

import androidx.annotation.Nullable;

import java.security.SecureRandom;
import java.util.Random;

public class StringUtils {

    static char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public static String UUID(int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int randIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randIndex];
        }
        return new String(result);
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

    public String convertToFirstUpper(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toUpperCase();
    }
}
