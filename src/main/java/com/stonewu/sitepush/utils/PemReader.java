package com.stonewu.sitepush.utils;

import cn.hutool.core.codec.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author Erzbir
 * @Date 2023/10/21
 */
public class PemReader {
    private PemReader() {

    }

    public static PemReader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public PrivateKey getPrivateKeyFromPEM(String pem, String algorithm, String title)
        throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(pem))) {
            String line;
            StringBuilder keyContent = new StringBuilder();
            boolean inKeyBlock = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains(title)) {
                    inKeyBlock = true;
                    continue;
                }
                if (inKeyBlock) {
                    keyContent.append(line);
                }
            }

            byte[] keyBytes = Base64.decode(keyContent.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IOException("Unexpected exception reading pem", e);
        }
    }

    public PrivateKey getPrivateKeyFromPEM(String pem, String algorithm) throws IOException {
        String title = "PRIVATE KEY";
        return getPrivateKeyFromPEM(pem, algorithm, title);
    }


    private interface SingletonHolder {
        PemReader INSTANCE = new PemReader();
    }
}
