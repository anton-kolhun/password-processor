package com.experiment.passwordprocessor.service;

import com.experiment.passwordprocessor.service.helper.DataEncryptionHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DataEncryptionHelperTest {

    private DataEncryptionHelper dataEncryptionHelper;

    @Before
    public void setup() {
        String secretKey = "the-length-is-16"; //length of the string should be 16
        dataEncryptionHelper = new DataEncryptionHelper(secretKey);

    }

    @Test
    public void shouldEncryptAndDecrypt() {
        String valueToencrypt = "encryptMe";
        String encryptedResult = dataEncryptionHelper.encrypt(valueToencrypt);
        String decryptedValue = dataEncryptionHelper.decrypt(encryptedResult);
        Assert.assertNotEquals(valueToencrypt, encryptedResult);
        Assert.assertEquals(valueToencrypt, decryptedValue);

    }
}