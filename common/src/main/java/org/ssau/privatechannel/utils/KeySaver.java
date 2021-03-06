package org.ssau.privatechannel.utils;

import org.apache.commons.io.FileUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KeySaver {
    public static void writeToFile(String filename, SecretKey key) throws IOException {
        FileUtils.writeByteArrayToFile(new File(filename), key.getEncoded());
    }

    public static void writeToFileIv(String filename, IvParameterSpec ivps) throws IOException {
        FileUtils.writeByteArrayToFile(new File(filename), ivps.getIV());
    }

    public static SecretKey readKey(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        return new SecretKeySpec(bytes, "AES");
    }

    public static IvParameterSpec readIv(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        return new IvParameterSpec(bytes);
    }
}
