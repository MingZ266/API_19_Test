package com.tai.api19test.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Arrays;

import android.util.Base64;

public class Local {
    //-----------------AES------------------
    private static Key aesKey = null;// 本地从服务器获取的AES密钥

    /**
     * 使用AES密钥加密数据.
     *
     * @param data 待加密的数据
     * @return 加密后的密文，使用Base64编码
     */
    public static String aesEncryptData(String data) {
        try {
            if (aesKey == null)
                throw new NullPointerException("AES密钥为空");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return Base64.encodeToString(cipher.doFinal(data.getBytes()), Base64.NO_WRAP);
        } catch (Exception e) {
            System.out.println("Local：AES加密数据失败，原因是：\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * 使用AES密钥解密数据.
     *
     * @param ciphertext 待解密的使用Base64编码的密文
     * @return 解密后的数据
     */
    public static String aesDecryptData(String ciphertext) {
        try {
            if (aesKey == null)
                throw new NullPointerException("AES密钥为空");
            if (ciphertext == null)
                throw new NullPointerException("密文为空");
            byte[] decodeCiphertextBytes = Base64.decode(ciphertext, Base64.NO_WRAP);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(decodeCiphertextBytes));
        } catch (Exception e) {
            System.out.println("Local：AES解密数据失败，原因是：\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    //-----------------RSA------------------
    private static final int rsaKeySize = 1024;// RSA密钥长度
    private static PrivateKey privateKey = null;// 本地RSA私钥
    private static byte[] publicKeyBytes = null;// 本地RSA公钥字节数组形式

    /**
     * 生成RSA密钥对.
     */
    private static void generateLocalKeyPair() {
        try {
            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(rsaKeySize);
            KeyPair keyPair = rsa.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKeyBytes = keyPair.getPublic().getEncoded();
        } catch (Exception e) {
            System.out.println("生成RSA密钥对失败：" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * 获得本地公钥的字符串形式，使用Base64编码.
     *
     * @return 本地公钥字符串
     */
    public static String getLocalPublicKeyStr() {
        generateLocalKeyPair();
        try {
            if (publicKeyBytes == null)
                throw new NullPointerException("公钥的字节数组形式为空，RSA密钥可能生成失败");
            return Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            System.out.println("获取公钥字符串失败，原因是：\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * 解密AES密钥字符串以构造AES密钥.
     *
     * @param aesKeyStr 以Base64编码的加密后的AES密钥字符串
     */
    public static void decryptAESKey(String aesKeyStr) {
        try {
            byte[] decodeAESKeyBytes = Base64.decode(aesKeyStr, Base64.NO_WRAP);
            int maxLength = rsaKeySize / 8;
            int mod = decodeAESKeyBytes.length % maxLength;
            int groupNum = decodeAESKeyBytes.length / maxLength;
            if (mod != 0)
                groupNum++;
            byte[][] dataSrc = new byte[groupNum][0];
            for (int i = 0, start = 0; i < groupNum; i++, start += maxLength) {
                if (i != groupNum - 1 || mod == 0) {
                    dataSrc[i] = Arrays.copyOfRange(decodeAESKeyBytes, start, start + maxLength);
                } else {
                    dataSrc[i] = Arrays.copyOfRange(decodeAESKeyBytes, start, start + mod);
                }
            }
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[][] cache = new byte[dataSrc.length][0];
            byte[] aesKeyBytes = new byte[0];
            for (int i = 0, start = 0; i < dataSrc.length; i++) {
                cache[i] = cipher.doFinal(dataSrc[i]);
                aesKeyBytes = Arrays.copyOf(aesKeyBytes, aesKeyBytes.length + cache[i].length);
                System.arraycopy(cache[i], 0, aesKeyBytes, start, cache[i].length);
                start = cache[i].length;
            }
            aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        } catch (Exception e) {
            System.out.println("Local：RSA解密AES密钥失败，原因是：\n" + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
