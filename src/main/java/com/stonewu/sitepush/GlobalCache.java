package com.stonewu.sitepush;

import com.stonewu.sitepush.scheme.PushUnique;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalCache {

    public static Map<String, PushUnique> PUSH_CACHE = new ConcurrentHashMap<>();

}
