package com.longmai.datademo.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.*;

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * @author https://www.cnblogs.com/nihaorz/p/10690643.html
 * @description Rsa 工具类，公钥私钥生成，加解密
 * @date 2020-05-18
 **/
public class RsaUtils {

    private static final String SRC = "123456";

    public static void main(String[] args) throws Exception {
        System.out.println("\n");
        RsaKeyPair keyPair = generateKeyPair();
        System.out.println("公钥：" + keyPair.getPublicKey());
        System.out.println("私钥：" + keyPair.getPrivateKey());
        System.out.println("\n");
        test1(keyPair);
        System.out.println("\n");
        test2(keyPair);
        System.out.println("\n");

        String cert = "MIIFCDCCA/CgAwIBAgIQVAEBCX2gC3W0fGohwqE2LzANBgkqhkiG9w0BAQsFADBgMQswCQYDVQQGEwJWTjE6MDgGA1UECgwxQ8OUTkcgVFkgQ+G7lCBQSOG6pk4gVknhu4ROIFRIw5RORyBORVdURUwtVEVMRUNPTTEVMBMGA1UEAxMMTkVXVEVMLUNBIHYyMB4XDTE5MDIxMzA4MDIzMFoXDTIwMDIwODA4MDIzMFowZjELMAkGA1UEBhMCVk4xNzA1BgNVBAMMLkPDlE5HIFRZIEPhu5QgUEjhuqZOIENI4buuIEvDnSBT4buQIE5FV0NBIFRFU1QxHjAcBgoJkiaJk/IsZAEBDA5NU1Q6MDEyMzQ1Njc4OTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKBILwikayZla7zQ4e5zB+aIhLdFxqnhOzQCDTdfp6ZOd2KELhA3Ws9RQBTuE8EcUpty+XYO9wNgWtZy6mVt+rAr9KMNaXUX0yoICpmtlFqGr8M+7MnMyMjHqh31n6A7FKTP6pjJE97L/Wyo+NNB7trCenfzUSas/qjOjH20Y6lbHVz2ra5IxWc5O+KmZKNlSvlXgjMyEfv6pBgaoMUloGJjAf+gCajrOmGv4DJlrcq6dxTBb/GORIJ23NMdk2szf5W7wxMeP7RnIt4uS6UJqG9qcLZD9m+w0hbkLlfV16XSC/rTXYvvB5GA9WLOsKiZ22YwFew1MmZ1MnUKF5s3CPUCAwEAAaOCAbYwggGyMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUgvDxIe/+Tlg0cW/5jEIPPyHdVZswagYIKwYBBQUHAQEEXjBcMC0GCCsGAQUFBzAChiFodHRwOi8vcHViLm5ld2NhLnZuL25ld3RlbC1jYS5jcnQwKwYIKwYBBQUHMAGGH2h0dHA6Ly9vY3NwMi5uZXdjYS52bi9yZXNwb25kZXIwGwYDVR0RBBQwEoEQc3VwcG9ydEBuZXdjYS52bjBeBgNVHSAEVzBVMFMGDCsGAQQBge0DAQkDATBDMCMGCCsGAQUFBwIBFhdodHRwOi8vcHViLm5ld2NhLnZuL3JwYTAcBggrBgEFBQcCAjAQDA5PU19OZXdfMU1fVGVzdDA0BgNVHSUELTArBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcKAwwGCSqGSIb3LwEBBTAzBgNVHR8ELDAqMCigJqAkhiJodHRwOi8vY3JsMi5uZXdjYS52bi9uZXd0ZWwtY2EuY3JsMB0GA1UdDgQWBBReXyV5OSTHxO6j8jWF4XmBEOes+jAOBgNVHQ8BAf8EBAMCBPAwDQYJKoZIhvcNAQELBQADggEBACNQZoD+UeTMXhi9HcWiZjVwTuwy2btHD3Z+LVUSxUBMo++Lvbqb/2hOvzt/RMO14QMo0gGoRD0cnTjfjy1+4cMPp1HliW52kjyE9B5flBjM+bF9Cw7b6sLIsN34Gb2zD15U35QQJKtnmNpSV1437CMkRJOmlTpZ7MxnIUFh5W6eH7j2ymQWPJSQh3/XC1yEiyfRTg9+QaVc6dCQMHhEeQJq1Kas38nCN3wXCpaJOmKN3fPUmakKiVOXs0clyfgD91BxrJILQLvNjRuC0ROY2ZTdzAIS8xLxDRh80lrtjDreYLOjeVWrbMl1wqgMdLBzF7iYFKMcXfkAzWDODU5bCNA=";

        //将内容转成流的方式
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(cert));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate certificate = cf.generateCertificate(bis);

        //取出公钥
        PublicKey publicKey = certificate.getPublicKey();
        System.out.println(publicKey.getFormat());


        boolean sha1withRSA = verify("2t6o9lprp3di9ya5", "OAtnvY6RyhDeZEZH/s1R8wI35QxOM6+nnY29oS2y5Plqx5NnYEJKs/gEUJdMhYDgzIlub0dH7fSnPb7Rt5ulPXOc0M00VW9fRWTc0ErckVsZfeFjjoGi9H9ImZbhwLHova0PBVzHndJN6Z6W1CxZ0iEx9Uz2op4nm+kzCMTCsZqkQdFTuWniRpm3BCl3ehw7ar9hrgmyH0tgceskdTRj4vTqmDRZzK3MyHPV7cZeH8ClcTNYGkFIhPGyxYZBJ537IkxLzF0trmaArn4PXUZ/up/yLJN9p/xVX/ERRbMopGwtY06LqLPnJxH4XTvrdWxR7MiEJvRmXSAqy2Mg7C/6Yw==", publicKey, "SHA1withRSA");
        System.out.println(sha1withRSA);

    }

    /**
     * 公钥加密私钥解密
     */
    private static void test1(RsaKeyPair keyPair) throws Exception {
        System.out.println("***************** 公钥加密私钥解密开始 *****************");
        String text1 = encryptByPublicKey(keyPair.getPublicKey(), RsaUtils.SRC);
        String text2 = decryptByPrivateKey(keyPair.getPrivateKey(), text1);
        System.out.println("加密前：" + RsaUtils.SRC);
        System.out.println("加密后：" + text1);
        System.out.println("解密后：" + text2);
        if (RsaUtils.SRC.equals(text2)) {
            System.out.println("解密字符串和原始字符串一致，解密成功");
        } else {
            System.out.println("解密字符串和原始字符串不一致，解密失败");
        }
        System.out.println("***************** 公钥加密私钥解密结束 *****************");
    }

    /**
     * 私钥加密公钥解密
     * @throws Exception /
     */
    private static void test2(RsaKeyPair keyPair) throws Exception {
        System.out.println("***************** 私钥加密公钥解密开始 *****************");
        String text1 = encryptByPrivateKey(keyPair.getPrivateKey(), RsaUtils.SRC);
        String text2 = decryptByPublicKey(keyPair.getPublicKey(), text1);
        System.out.println("加密前：" + RsaUtils.SRC);
        System.out.println("加密后：" + text1);
        System.out.println("解密后：" + text2);
        if (RsaUtils.SRC.equals(text2)) {
            System.out.println("解密字符串和原始字符串一致，解密成功");
        } else {
            System.out.println("解密字符串和原始字符串不一致，解密失败");
        }
        System.out.println("***************** 私钥加密公钥解密结束 *****************");
    }

    /**
     * 公钥解密
     *
     * @param publicKeyText 公钥
     * @param text 待解密的信息
     * @return /
     * @throws Exception /
     */
    public static String decryptByPublicKey(String publicKeyText, String text) throws Exception {

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, Base64.getDecoder().decode(text));
        return new String(result);
    }

    /**
     * 私钥加密
     *
     * @param privateKeyText 私钥
     * @param text 待加密的信息
     * @return /
     * @throws Exception /
     */
    public static String encryptByPrivateKey(String privateKeyText, String text) throws Exception {

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = doLongerCipherFinal(Cipher.ENCRYPT_MODE, cipher, text.getBytes());
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 私钥解密
     *
     * @param privateKeyText 私钥
     * @param text 待解密的文本
     * @return /
     * @throws Exception /
     */
    public static String decryptByPrivateKey(String privateKeyText, String text) throws Exception {

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, Base64.getDecoder().decode(text));
        return new String(result);
    }

    /**
     * 公钥加密
     *
     * @param publicKeyText 公钥
     * @param text 待加密的文本
     * @return /
     */
    public static String encryptByPublicKey(String publicKeyText, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec2);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = doLongerCipherFinal(Cipher.ENCRYPT_MODE, cipher, text.getBytes());
        return Base64.getEncoder().encodeToString(result);
    }

    private static byte[] doLongerCipherFinal(int opMode,Cipher cipher, byte[] source) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (opMode == Cipher.DECRYPT_MODE) {
            out.write(cipher.doFinal(source));
        } else {
            int offset = 0;
            int totalSize = source.length;
            while (totalSize - offset > 0) {
                int size = Math.min(cipher.getOutputSize(0) - 11, totalSize - offset);
                out.write(cipher.doFinal(source, offset, size));
                offset += size;
            }
        }
        out.close();
        return out.toByteArray();
    }

    /**
     * 构建RSA密钥对
     *
     * @return /
     * @throws NoSuchAlgorithmException /
     */
    public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
        return new RsaKeyPair(publicKeyString, privateKeyString);
    }


    /**
     * @Title: sign
     * @Description: RSA签名
     * @param signData 待签名数据
     * @param privateKey 私钥字符串
     * @return String 签名域
     * @throws
     */
    public static String sign(String signData, String privateKey, String SignAlgorithm) throws Exception {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyf.generatePrivate(priPKCS8);
        java.security.Signature signature = java.security.Signature.getInstance(SignAlgorithm);
        signature.initSign(priKey);
        signature.update(signData.getBytes());
        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * @Title: verify
     * @Description: RSA验签名检查
     * @param content   待签名数据
     * @param sign      签名域
     * @param publicKey base64后的公钥字符串
     * @return boolean 验签结果
     * @throws
     */
    public static boolean verify(String content, String sign, String publicKey, String signAlgorithm) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = Base64.getDecoder().decode(publicKey);
        if (encodedKey == null) {
            return false;
        }
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        // 用私钥对信息生成数字签名
        java.security.Signature signature = java.security.Signature.getInstance(signAlgorithm);
        signature.initVerify(pubKey);
        signature.update(content.getBytes());
        // 验证方法 返回true则为比对成功
        boolean bverify = signature.verify(Base64.getDecoder().decode(sign));
        return bverify;
    }

    public static boolean verify(String content, String sign, PublicKey publicKey, String signAlgorithm) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance(signAlgorithm);
        signature.initVerify(publicKey);
        signature.update(content.getBytes());
        // 验证方法 返回true则为比对成功
        boolean bverify = signature.verify(Base64.getDecoder().decode(sign));
        return bverify;
    }


    /**
     * RSA密钥对对象
     */
    public static class RsaKeyPair {

        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

    }
}
