package com.stonewu.sitepush.utils;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 * @author Erzbir
 * @Date 2023/10/21
 */
public class SecurityUtil {

    private SecurityUtil() {

    }

    public static SecurityUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public byte[] sign(Signature signature, byte[] contentBytes, PrivateKey privateKey)
        throws InvalidKeyException, SignatureException {
        signature.initSign(privateKey);
        signature.update(contentBytes);
        return signature.sign();
    }

    private interface SingletonHolder {
        SecurityUtil INSTANCE = new SecurityUtil();
    }
}
