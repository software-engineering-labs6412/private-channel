package org.ssau.privatechannel.utils;

public abstract class KeyHolder {
    private static String key;

    public static synchronized String getKey() {
        return key;
    }

    public static synchronized void holdKey(String newKey) {
        key = newKey;
    }

    public static synchronized void dropKey() {
        key = null;
    }
}
