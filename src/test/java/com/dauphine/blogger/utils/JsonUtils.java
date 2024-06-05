package com.dauphine.blogger.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    public static String stringify(Object obj) {
        try {
            return new ObjectMapper()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
