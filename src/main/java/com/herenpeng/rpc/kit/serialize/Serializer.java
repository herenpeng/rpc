package com.herenpeng.rpc.kit.serialize;

import java.lang.reflect.Type;

/**
 * @author herenpeng
 * @since 2023-02-23 19:54
 */
public interface Serializer {

    byte JSON = 1;

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
     */
    byte[] serialize(Object data);

    /**
     * 根据具体的对象类型反序列化
     *
     * @param bytes     字节数组
     * @param valueType 反序列化类型
     * @param <T>       泛型
     * @return 反序列的对象
     */
    <T> T deserialize(byte[] bytes, Type valueType);

}
