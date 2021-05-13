package com.nampt.socialnetworkproject.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonParserUtil {
    private static volatile JsonParserUtil instance;

    private Gson gson;

    private JsonParserUtil() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static JsonParserUtil getInstance() {
        if (instance == null) {
            synchronized (JsonParserUtil.class) {
                if (instance == null) {
                    instance = new JsonParserUtil();
                }
            }
        }
        return instance;
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
