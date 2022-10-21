package com.longmai.dbsafe.encrypt;

import java.security.GeneralSecurityException;

public interface IEncrypt {

    byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException;
    byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException;
}
