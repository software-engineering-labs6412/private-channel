package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class Sha512EncoderService {

    public  String getHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        catch (NoSuchAlgorithmException e) {
            log.error("Data encryption failed", e);
            throw new RuntimeException(e);
        }

    }
}