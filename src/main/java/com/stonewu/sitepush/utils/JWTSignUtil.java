package com.stonewu.sitepush.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

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
        Dict header,
        Dict payload)
        throws GeneralSecurityException {
        String headerBase64 =
            Base64.encodeUrlSafe(JSONUtil.toJsonStr(header).getBytes(StandardCharsets.UTF_8));
        String payloadBase64 =
            Base64.encodeUrlSafe(JSONUtil.toJsonStr(payload).getBytes(StandardCharsets.UTF_8));
        String content = headerBase64 + "." + payloadBase64;
        byte[] contentBytes = content.getBytes();
        byte[] signature = SecurityUtil.getInstance()
            .sign(Signature.getInstance("SHA256withRSA"), contentBytes, privateKey);
        return content + "." + Base64.encodeUrlSafe(signature);
    }


    private interface SingletonHolder {
        JWTSignUtil INSTANCE = new JWTSignUtil();
    }
}
