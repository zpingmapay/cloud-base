package com.xyz.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RSAUtils {
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String CHARSET = "UTF-8";
    public static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";

    /**
     * * 生成密钥对 *
     *
     * @param keySize key size
     * @return KeyPair 密钥对
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(keySize, new SecureRandom());
        return keyPairGen.generateKeyPair();

    }

    /**
     * map中的公钥和私钥都经过base64编码
     *
     * @param keySize size of key
     * @return map
     */
    public static Map<String, String> createRSAKeys(int keySize) {
        try {
            KeyPair keyPair = generateRSAKeyPair(keySize);
            Key publicKey = keyPair.getPublic();
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            Key privateKey = keyPair.getPrivate();
            String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            Map<String, String> keyPairMap = new HashMap<>();
            keyPairMap.put(PUBLIC_KEY, publicKeyStr);
            keyPairMap.put(PRIVATE_KEY, privateKeyStr);
            return keyPairMap;
        } catch (Exception e) {
            throw new RuntimeException("Create ras keys failed", e);
        }
    }

    /**
     * 根据秘钥对KeyPair获取公钥
     *
     * @param keyPair KeyPair
     * @return String
     */
    public static String getPublicKey(KeyPair keyPair) {
        Key publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 根据秘钥对KeyPair获取私钥
     *
     * @param keyPair KeyPair
     * @return String
     */
    public static String getPrivateKey(KeyPair keyPair) {
        Key privateKey = keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 从文件中获取密钥对
     *
     * @param filePath 文件路径
     * @return KeyPair KeyPair
     * @throws Exception Exception
     */
    public static KeyPair loadKeyPair(String filePath) throws Exception {
        try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(filePath))) {
            return (KeyPair) oos.readObject();
        }
    }

    /**
     * 保存秘钥到文件中
     *
     * @param kp       秘钥对
     * @param filePath 保存秘钥的文件路径
     * @throws Exception Exception
     */
    public static void saveKeyPair(KeyPair kp, String filePath) throws Exception {
        File file = new File(filePath);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(kp);
        }
    }

    /**
     * 公钥加密
     *
     * @param plaintext 待加密的字符串
     * @param publicKeyTxt Base64编码的公钥字符串
     * @return String
     */
    public static String encrypt(String plaintext, String publicKeyTxt) {
        try {
            RSAPublicKey publicKey = getPublicKey(publicKeyTxt);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, plaintext.getBytes(CHARSET),
                    publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while encrypting the string [" + plaintext + "]", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param plaintext  待加密的字符串
     * @param privateKeyTxt Base64编码的私钥字符串
     * @return String
     */
    public static String decrypt(String plaintext, String privateKeyTxt) {
        try {
            RSAPrivateKey privateKey = getPrivateKey(privateKeyTxt);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(plaintext),
                    privateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while decrypting the string [" + plaintext + "]", e);
        }
    }

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @return RSAPublicKey
     */
    private static RSAPublicKey getPublicKey(String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
            return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @return RSAPrivateKey
     */
    private static RSAPrivateKey getPrivateKey(String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred when the encryption and decryption threshold was [" + maxBlock + "]", e);
        }

    }
}
