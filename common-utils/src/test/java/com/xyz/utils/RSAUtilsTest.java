package com.xyz.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class RSAUtilsTest {
    @Test
    public void testGenerateKeyPair() {
        Map<String, String> rsaKeys = RSAUtils.createRSAKeys(RSAUtils.KEY_SIZE);
        Assert.isTrue(rsaKeys.size() == 2, "size not 2");
    }

    @Test
    public void testSaveAndLoad() throws Exception {
        KeyPair keyPair = RSAUtils.generateRSAKeyPair(RSAUtils.KEY_SIZE);
        String path = "/test.rsa";
        RSAUtils.saveKeyPair(keyPair, path);

        keyPair = RSAUtils.loadKeyPair(path);
        Assert.notNull(keyPair, "save and load failed");
    }

    @Test
    public void testEncryptAndDecrypt() throws NoSuchAlgorithmException {
        String plainTxt = "这是一段没有加密的信息";

        Map<String, String> keys = RSAUtils.createRSAKeys(RSAUtils.KEY_SIZE);
        String publicKey = keys.get(RSAUtils.PUBLIC_KEY);
        String privateKey = keys.get(RSAUtils.PRIVATE_KEY);

        String encrypted = RSAUtils.encrypt(plainTxt, publicKey);
        String decrypted = RSAUtils.decrypt(encrypted, privateKey);
        Assert.isTrue(decrypted.equals(plainTxt), "Encrypt and decrypt failed");
    }
}
