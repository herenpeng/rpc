package com.herenpeng.rpc.kit.serialize;

import com.herenpeng.rpc.exception.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 * @since 2023-02-23 21:23
 */
public class SerializerManager {

    private static final Map<Byte, Serializer> serializers = new ConcurrentHashMap<>();

    static {
        // 初始化该管理类的时候注册所有协议版本，这些都是内置的版本号
        serializers.put(Serializer.JSON, new JsonSerializer());
        serializers.put(Serializer.HESSIAN, new HessianSerializer());
    }

    /**
     * 注册序列化工具，默认同id后注册会覆盖先注册的序列化工具
     */
    public static void registerSerializer(Serializer serializer) {
        if (serializer == null) {
            throw new RpcException("[RPC工具]注册的序列化工具对象不能为空");
        }
        serializers.put(serializer.getId(), serializer);
    }


    public static Serializer getSerializer(byte id) {
        return serializers.get(id);
    }


}
