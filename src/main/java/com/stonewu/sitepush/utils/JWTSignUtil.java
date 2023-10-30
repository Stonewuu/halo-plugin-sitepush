package com.stonewu.sitepush.utils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Map;
import run.halo.app.infra.utils.JsonUtils;

/**
 * @author Erzbir
 * @Date 2023/10/21
 */
public class JWTSignUtil {
    private JWTSignUtil() {

    }

    public static JWTSignUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String signUsingRsaSha256(
        PrivateKey privateKey,
        Map<String, ?> header,
        Map<String, ?> payload)
        throws GeneralSecurityException {
        String headerBase64 =
            Base64.getUrlEncoder()
                .encodeToString(JsonUtils.objectToJson(header).getBytes(StandardCharsets.UTF_8));
        String payloadBase64 =
            Base64.getUrlEncoder()
                .encodeToString(JsonUtils.objectToJson(payload).getBytes(StandardCharsets.UTF_8));
        String content = headerBase64 + "." + payloadBase64;
        byte[] contentBytes = content.getBytes();
        byte[] signature = SecurityUtil.getInstance()
            .sign(Signature.getInstance("SHA256withRSA"), contentBytes, privateKey);
        return content + "." + Base64.getUrlEncoder().encodeToString(signature);
    }


    private interface SingletonHolder {
        JWTSignUtil INSTANCE = new JWTSignUtil();
    }
}
