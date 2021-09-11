package com.github.cris16228.library;

import android.util.Base64;

public class Base64Util {

    private static final String UNICODE_FORMAT = "UTF8";

    public static class Base64Encoder {

        public String encrypt(String unencryptedString) {
            String encryptedString = null;
            try {
                byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
                encryptedString = new String(Base64.encode(plainText, Base64.DEFAULT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return encryptedString;
        }
    }

    public static class Base64Decoder {

        public String decrypt(String encryptedString) {
            String unencryptedString = null;
            try {
                unencryptedString = new String(Base64.decode(encryptedString, Base64.DEFAULT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return unencryptedString;
        }
    }
}
