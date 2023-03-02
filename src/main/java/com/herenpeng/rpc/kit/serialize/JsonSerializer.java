package com.herenpeng.rpc.kit.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.herenpeng.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author herenpeng
 * @since 2023-02-23 19:57
 */
@Slf4j
public class JsonSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public byte getId() {
        return JSON;
    }

    @Override
    public byte[] serialize(final Object data) throws RpcException {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.error("[RPC工具]Json序列化错误：{}，错误信息：{}", data, e.getMessage());
            throw new RpcException("[RPC工具]Json序列化错误：" + data);
        }
    }

    @Override
    public <T> T deserialize(final byte[] bytes, Type valueType) throws RpcException {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType javaType = typeFactory.constructType(valueType);
            return objectMapper.readValue(bytes, javaType);
        } catch (IOException e) {
            log.error("[RPC工具]Json反序列化错误：{}，反序列化类型：{}，错误信息：{}", bytes, valueType, e.getMessage());
            throw new RpcException("[RPC工具]Json反序列化错误：" + Arrays.toString(bytes) + "，反序列化类型：" + valueType);
        }
    }
}
