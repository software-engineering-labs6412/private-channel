package org.ssau.privatechannel.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ssau.privatechannel.exception.BadAlgorythmLengthException;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.utils.AESUtil;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

class AESKeyGeneratorTest {

    private static final String TEST_IP = "10.20.30.40";
    private final Random RANDOMIZER = new Random();

    @BeforeEach
    void setUp() {
    }

    @Test
    void givenString_whenEncrypt_thenSuccess()
            throws NoSuchAlgorithmException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            BadAlgorythmLengthException {

        String input = UUID.randomUUID().toString();
        SecretKey key = AESUtil.generateKey(128);
        IvParameterSpec ivParameterSpec = AESUtil.generateIv();
        String cipherText = AESUtil.encrypt(input, key, ivParameterSpec);
        String plainText = AESUtil.decrypt(cipherText, key, ivParameterSpec);
        Assertions.assertEquals(input, plainText);
    }

    @Test
    void givenObject_whenEncrypt_thenSuccess()
            throws NoSuchAlgorithmException,
            IllegalBlockSizeException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IOException,
            BadPaddingException,
            ClassNotFoundException,
            BadAlgorythmLengthException {

        ConfidentialInfo info = ConfidentialInfo.builder()
                .id(RANDOMIZER.nextLong())
                .receiverIP(TEST_IP)
                .data(generateRandomData())
                .build();
        SecretKey key = AESUtil.generateKey(128);
        IvParameterSpec ivParameterSpec = AESUtil.generateIv();
        SealedObject sealedObject = AESUtil.encryptObject(info, key, ivParameterSpec);
        ConfidentialInfo object = (ConfidentialInfo) AESUtil.decryptObject(sealedObject, key, ivParameterSpec);
        Assertions.assertSame(info, object);
    }

    private Map<String, Object> generateRandomData() {
        int MAX_DATA_ROWS_COUNT = 10;
        int rowsCount = 1 + (Math.abs(RANDOMIZER.nextInt()) % (MAX_DATA_ROWS_COUNT));

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < rowsCount; ++i) {
            result.put(UUID.randomUUID().toString(), UUID.randomUUID() + UUID.randomUUID().toString());
        }
        return result;
    }
}