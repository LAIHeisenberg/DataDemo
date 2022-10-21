package com.longmai.dbsafe.encrypt;

public class DBEncryptFactory {

    public static IEncrypt getEncryptInstance(String algor){
        switch (algor){
            case "AES":
                return new AESEncrypt();
            default:
                return null;
        }
    }
}
