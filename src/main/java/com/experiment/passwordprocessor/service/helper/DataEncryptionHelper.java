package com.experiment.passwordprocessor.service.helper;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class DataEncryptionHelper {

    private final String secretKey;

    private final Cipher encryptionCipher;

    private final Cipher decryptionCipher;

    public DataEncryptionHelper(String secretKey) {
        this.secretKey = secretKey;
        try {
            String initVector = secretKey;
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            decryptionCipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        } catch (Exception e) {
            throw new RuntimeException("could not initialized dataEncryptionService", e);
        }

    }


    public String encrypt(String value) {
        try {
            byte[] encrypted = encryptionCipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            throw new RuntimeException("could not encrypt given value", ex);
        }

    }

    public String decrypt(String value) {
        try {
            byte[] original = decryptionCipher.doFinal(Base64.decodeBase64(value));
            return new String(original);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            throw new RuntimeException("could not parse given value", ex);
        }

    }
}
