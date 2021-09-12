package com.github.cris16228.library;

import android.text.TextUtils;
import android.util.Base64;

public class Base64Utils {

    private static final String UNICODE_FORMAT = "UTF8";

    public static class Base64Encoder {

        public String encrypt(String unencryptedString, int flag, String unicode_format) {
            String encryptedString = null;
            try {
                byte[] plainText;
                if (TextUtils.isEmpty(unicode_format))
                    plainText = unencryptedString.getBytes();
                else
                    plainText = unencryptedString.getBytes(unicode_format);
                encryptedString = Base64.encodeToString(plainText, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return encryptedString;
        }
    }

    public static class Base64Decoder {

        public String decrypt(String encryptedString, int flag) {
            String unencryptedString = null;
            try {
                unencryptedString = new String(Base64.decode(encryptedString, flag));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return unencryptedString;
        }
    }
}
