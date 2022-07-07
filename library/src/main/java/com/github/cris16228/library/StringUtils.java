package com.github.cris16228.library;

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
}
