package com.netease.gather.common.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JsonUtil{

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);
    }

    public static <T> T fromJson(final String json, Class<T> t) throws IOException {
        return mapper.readValue(json,t);
    }

    public static String toJsonStr(Object obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }
}

