package com.herenpeng.rpc.kit.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

/**
 * @author herenpeng
 * @since 2023-02-23 19:57
 */
@Slf4j
public class JsonSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        objectMapper = new ObjectMapper();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(format);
    }

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
    public <T> T deserialize(byte[] bytes, Type valueType) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType javaType = typeFactory.constructType(valueType);
            return objectMapper.readValue(bytes, javaType);
        } catch (IOException e) {
            log.error("[RPC工具]Json反序列化错误：{}，反序列化类型：{}", bytes, valueType);
            e.printStackTrace();
        }
        return null;
    }
}
