package com.herenpeng.rpc.kit.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author herenpeng
 * @since 2023-02-23 19:57
 */
@Slf4j
public class JsonSerializer implements Serializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte getId() {
        return JSON;
    }

    @Override
    public byte[] serialize(Object data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.error("[RPC工具]Json序列化错误：{}", data);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classObject) {
        try {
            return objectMapper.readValue(bytes, classObject);
        } catch (IOException e) {
            log.error("[RPC工具]Json反序列化错误：{}，反序列化类型：{}", bytes, classObject);
            e.printStackTrace();
        }
        return null;
    }
}
