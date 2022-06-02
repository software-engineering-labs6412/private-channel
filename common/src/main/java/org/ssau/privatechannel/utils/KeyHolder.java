package org.ssau.privatechannel.utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyHolder {

    private static SecretKey key;
    private static IvParameterSpec iv;

    public static SecretKey getKey() {
        return key;
    }

    public static void setKey(SecretKey key) {
        KeyHolder.key = key;
    }

    public static void setKey(byte[] key) {
        KeyHolder.key = new SecretKeySpec(key, "AES");
    }

    public static IvParameterSpec getIv() {
        return iv;
    }

    public static void setIv(IvParameterSpec iv) {
        KeyHolder.iv = iv;
    }
}
