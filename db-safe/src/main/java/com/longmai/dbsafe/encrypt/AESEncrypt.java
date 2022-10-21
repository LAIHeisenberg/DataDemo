package com.longmai.dbsafe.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class AESEncrypt implements IEncrypt{

    // 加密:
    public byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.getEncoder().encode(cipher.doFinal(input));
    }

    // 解密:
    public byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        input = Base64.getDecoder().decode(input);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }


    public static void main(String[] args) throws Exception {
        AESEncrypt aesEncrypt = new AESEncrypt();
        // 原文:
        String message = "Hello, world!";
        System.out.println("Message: " + message);
        // 256位密钥 = 32 bytes Key:
        byte[] key = "1234567890abcdef1234567890abcdef".getBytes("UTF-8");
        // 加密:
        byte[] data = message.getBytes("UTF-8");
        byte[] encrypted = aesEncrypt.encrypt(key, data);

//        System.out.println("Encrypted: " + Base64.getEncoder().encodeToString(encrypted));
        System.out.println("Encrypted: " + new String(encrypted, "UTF-8"));
        // 解密:
        byte[] decrypted = aesEncrypt.decrypt(key, encrypted);
        System.out.println("Decrypted: " + new String(decrypted, "UTF-8"));

        String decryptedStr = "aVPhOYevCcscrZrPeVFfnFnnJZhXAtq87NDYreBOrHo=";
        String key2 = "zdasdfw658412541";

        byte[] decrypted1 = aesEncrypt.decrypt(key2.getBytes(), decryptedStr.getBytes());
        System.out.println("Decrypted: " + new String(decrypted1, "UTF-8"));
    }

    /**
     * 以下为AES算法使用CBC模式加解密
     * 通过CBC模式，它需要一个随机数作为IV参数，这样对于同一份明文，每次生成的密文都不同
     *
     * */
    /**
    // 加密:
    public byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        // CBC模式需要生成一个16 bytes的initialization vector:
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] iv = sr.generateSeed(16);
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);
        byte[] data = cipher.doFinal(input);
        // IV不需要保密，把IV和密文一起返回:
        return Base64.getEncoder().encode(join(iv, data));
    }

    // 解密:
    public byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        input = Base64.getDecoder().decode(input);
        // 把input分割成IV和密文:
        byte[] iv = new byte[16];
        byte[] data = new byte[input.length - 16];
        System.arraycopy(input, 0, iv, 0, 16);
        System.arraycopy(input, 16, data, 0, data.length);
        // 解密:
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
        return cipher.doFinal(data);
    }

    private byte[] join(byte[] bs1, byte[] bs2) {
        byte[] r = new byte[bs1.length + bs2.length];
        System.arraycopy(bs1, 0, r, 0, bs1.length);
        System.arraycopy(bs2, 0, r, bs1.length, bs2.length);
        return r;
    }

     */

}
