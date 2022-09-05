package com.longmai.datademo.utils;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;

public class CertUtils {

    public static Certificate base64ToCert(String base64Cert){
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(base64Cert));
        CertificateFactory cf;
        Certificate certificate = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            certificate = cf.generateCertificate(bis);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return certificate;
    }

    public static PublicKey getPublicKey(String base64Cert){
        Certificate certificate = base64ToCert(base64Cert);
        return certificate == null ? null : certificate.getPublicKey();
    }

}
