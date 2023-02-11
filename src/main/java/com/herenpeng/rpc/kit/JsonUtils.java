package com.herenpeng.rpc.kit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author herenpeng
 * @since 2023-02-01 21:38
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("json序列化错误：{}", object);
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] toBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("json序列化错误：{}", object);
            e.printStackTrace();
        }
        return null;
    }


    public static <T> T toObject(byte[] bytes, Class<T> classObject) {
        try {
            return objectMapper.readValue(bytes, classObject);
        } catch (IOException e) {
            logger.error("json反序列化错误：{}，反序列化类型：{}", bytes, classObject);
            e.printStackTrace();
        }
        return null;
    }

}