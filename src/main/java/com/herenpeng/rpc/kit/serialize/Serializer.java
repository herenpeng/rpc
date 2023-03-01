package com.herenpeng.rpc.kit.serialize;

import com.herenpeng.rpc.exception.RpcException;

import java.lang.reflect.Type;

/**
 * @author herenpeng
 * @since 2023-02-23 19:54
 */
public interface Serializer {

    byte JSON = 1;
    byte HESSIAN = 2;

    /**
     * 序列化方法的唯一ID
     *
     * @return
     */
    byte getId();

    /**
     * 序列化对象为字节数组
     *
     * @param data 序列化对象
     * @return 字节数组
     * @throws RpcException RPC
     */
    byte[] serialize(final Object data) throws RpcException;

    /**
     * 根据具体的对象类型反序列化
     *
     * @param bytes     字节数组
     * @param valueType 反序列化类型
     * @param <T>       泛型
     * @return 反序列的对象
     * @throws RpcException RPC异常
     */
    <T> T deserialize(final byte[] bytes, Type valueType) throws RpcException;

}
