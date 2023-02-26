package com.herenpeng.rpc.kit;

import com.herenpeng.rpc.exception.RpcException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author herenpeng
 * @since 2023-02-26 21:41
 */
public abstract class ValueType<T> {

    private final Type valueType;

    public ValueType() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new RpcException("[RPC工具]引用类型错误");
        } else {
            valueType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        }
    }

    public Type get() {
        return valueType;
    }

}
