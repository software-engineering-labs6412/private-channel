package org.ssau.privatechannel.utils;

public class Base64 {
    public static String encode(String data) {
        byte[] bytes = org.apache.tomcat.util.codec.binary.Base64.encodeBase64(data.getBytes());
        return new String(bytes);
    }

    public static String decode(String data) {
        byte[] bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(data.getBytes());
        return new String(bytes);
    }
}
