package org.ssau.privatechannel.utils;

import lombok.extern.slf4j.Slf4j;
import org.ssau.privatechannel.exception.BadAlgorythmLengthException;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.model.ReceivedInformation;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
public class AESUtil {

    private static final List<Integer> VALID_KEY_LENGTHS = Arrays.asList(128, 192, 256);

    public static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException, BadAlgorythmLengthException {

        if (!VALID_KEY_LENGTHS.contains(n)) {
            String logMessage = String.format("Incorrect secret key length: %s. Must be one of %s",
                    n, VALID_KEY_LENGTHS);
            log.error(logMessage);
            throw new BadAlgorythmLengthException(logMessage);
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public static SealedObject encryptObject(Serializable object,
                                             SecretKey key,
                                             IvParameterSpec iv) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IOException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return new SealedObject(object, cipher);
    }

    public static Serializable decryptObject(SealedObject sealedObject,
                                             SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            ClassNotFoundException, BadPaddingException, IllegalBlockSizeException,
            IOException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return (Serializable) sealedObject.getObject(cipher);
    }

    public static ConfidentialInfo encryptConfInfo(ConfidentialInfo info, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        return ConfidentialInfo.builder()
                .id(info.getId())
                .senderIP(info.getSenderIP())
                .receiverIP(info.getReceiverIP())
                .data(encryptMap(info.getData(), key, iv))
                .build();
    }

    public static List<ConfidentialInfo> encryptBatchInfo(List<ConfidentialInfo> info,
                                                          SecretKey key,
                                                          IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {
        List<ConfidentialInfo> result = new ArrayList<>();
        for (ConfidentialInfo record : info) {
            result.add(encryptConfInfo(record, key, iv));
        }
        return result;
    }

    public static ReceivedInformation decryptConfInfo(ReceivedInformation info,
                                                   SecretKey key,
                                                   IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        return ReceivedInformation.builder()
                .id(info.getId())
                .senderIP(info.getSenderIP())
                .receiverIP(info.getReceiverIP())
                .data(decryptMap(info.getData(), key, iv))
                .build();
    }

    public static List<ReceivedInformation> decryptBatchInfo(List<ReceivedInformation> info,
                                                             SecretKey key,
                                                             IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {
        List<ReceivedInformation> result = new ArrayList<>();
        for (ReceivedInformation record : info) {
            result.add(decryptConfInfo(record, key, iv));
        }
        return result;
    }

    public static Map<String, Object> encryptMap(Map<String, Object> map,
                                                 SecretKey key,
                                                 IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> record : map.entrySet()) {
            result.put(
                    encrypt(record.getKey(), key, iv),
                    encrypt(record.getValue().toString(), key, iv)
            );
        }
        return result;
    }

    public static Map<String, Object> decryptMap(Map<String, Object> map,
                                                 SecretKey key,
                                                 IvParameterSpec iv)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> record : map.entrySet()) {
            result.put(
                    decrypt(record.getKey(), key, iv),
                    decrypt(record.getValue().toString(), key, iv)
            );
        }
        return result;
    }
}
