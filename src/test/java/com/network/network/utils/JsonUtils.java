package com.network.network.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String serialize(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    public static  <T> T deserialize(String str, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(str, clazz);
    }

    public static <T> List<T> deserializeList(String obj, Class<T> clazz) throws JsonProcessingException {
        CollectionType javaType = MAPPER.getTypeFactory()
                .constructCollectionType(List.class, clazz);
        return MAPPER.readValue(obj, javaType);
    }
}
