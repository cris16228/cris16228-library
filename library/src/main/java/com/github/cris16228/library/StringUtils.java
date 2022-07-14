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

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String stringToBinary(String text) {
        StringBuilder bString = new StringBuilder();
        String tmp = "";
        if (text.trim().length() == 0) {
            Log.e("stringToBinary", "The text cannot be empty");
            return "";
        }
        for (int i = 0; i < text.length(); i++) {
            tmp = Integer.toBinaryString(text.charAt(i));
            for (int j = tmp.length(); j < 8; j++) {
                tmp = "0" + tmp;
            }
            bString.append(tmp).append(" ");
        }
        Log.i("stringToBinary", bString.toString());
        return bString.toString();
    }

    public static String binaryToString(String binaryCode) {
        String[] code = binaryCode.split(" ");
        StringBuilder word = new StringBuilder();
        for (String s : code) {
            word.append((char) Integer.parseInt(s, 2));
        }
        Log.i("stringToBinary", word.toString());
        return word.toString();
    }
}
