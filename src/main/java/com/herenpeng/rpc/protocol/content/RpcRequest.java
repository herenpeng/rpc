package com.herenpeng.rpc.protocol.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.kit.BitKit;
import com.herenpeng.rpc.kit.RpcCallback;
import io.netty.buffer.ByteBuf;
import lombok.*;

import java.lang.reflect.Type;

/**
 * @author herenpeng
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest<T> extends RpcProtocol {

    /**
     * 第一位bit位，标识使用的是路径方式
     */
    public static final byte STATUS_METHOD_PATH = 1;

    /**
     * 标识位，用来表示一些状态
     */
    protected byte status;

    private RpcMethodLocator methodLocator;

    private String methodPath;

    /**
     * 全部参数，如果最后一个参数为rpc回调，则置为null值
     */
    private Object[] params;
    /**
     * 全部参数对应的字节数组数据
     */
    private byte[][] paramsBytes;

    @JsonIgnore
    private Type returnType;

    /**
     * 用于标识请求是否异步，真正的异步判断字段
     */
    @JsonIgnore
    private boolean async;

    @JsonIgnore
    private RpcCallback<T> callable;


    public RpcRequest(byte subType, byte serialize) {
        super(RpcProtocol.TYPE_REQUEST, subType, serialize);
    }

    /**
     * 带方法定位符的构造，默认为消息类型
     *
     * @param locator    请求方法定位符号对象
     * @param params     请求参数
     * @param returnType 返回类型
     * @param async      是否异步
     * @param callable   回调函数，同步为null
     */
    public RpcRequest(RpcMethodLocator locator, Object[] params, Type returnType, boolean async,
                      RpcCallback<T> callable, byte serialize) {
        super(RpcProtocol.TYPE_REQUEST, RpcProtocol.SUB_TYPE_MESSAGE, serialize);
        this.methodLocator = locator;
        this.params = params;
        this.returnType = returnType;
        this.async = async;
        this.callable = callable;
    }

    /**
     * 带请求路径的构造，默认为消息类型
     *
     * @param path       请求路径
     * @param params     请求参数
     * @param returnType 返回类型
     * @param async      是否异步
     * @param callable   回调函数，同步为null
     */
    public RpcRequest(String path, Object[] params, Type returnType, boolean async, RpcCallback<T> callable, byte serialize) {
        super(RpcProtocol.TYPE_REQUEST, RpcProtocol.SUB_TYPE_MESSAGE, serialize);
        this.status = (byte) BitKit.setBit(this.status, STATUS_METHOD_PATH);
        this.methodPath = path;
        this.params = params;
        this.returnType = returnType;
        this.async = async;
        this.callable = callable;
    }

    private boolean isPath() {
        return BitKit.getBit(this.status, STATUS_METHOD_PATH) == 1;
    }


    @Override
    public void encode(ByteBuf out) {
        // 编码
        super.encode(out);
        out.writeByte(this.status);
        byte[] methodBytes = getSerializer().serialize(isPath() ? this.methodPath : this.methodLocator);
        if (methodBytes == null || methodBytes.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(methodBytes.length);
            out.writeBytes(methodBytes);
        }
        if (this.params == null || this.params.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(this.params.length);
            for (Object param : this.params) {
                byte[] paramBytes = getSerializer().serialize(param);
                out.writeInt(paramBytes.length);
                out.writeBytes(paramBytes);
            }
        }
    }

    @Override
    public void decode(ByteBuf in) {
        // 解码
        super.decode(in);
        this.status = in.readByte();
        int length = in.readInt();
        if (length > 0) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            if (isPath()) {
                this.methodPath = getSerializer().deserialize(bytes, String.class);
            } else {
                this.methodLocator = getSerializer().deserialize(bytes, RpcMethodLocator.class);
            }
        }
        // 所有数据总长度
        int paramsLength = in.readInt();
        if (paramsLength > 0) {
            this.paramsBytes = new byte[paramsLength][];
            for (int i = 0; i < paramsLength; i++) {
                int paramLength = in.readInt();
                byte[] bytes = new byte[paramLength];
                in.readBytes(bytes);
                // 解码的时候只将其解析为字节，由后续根据类型反序列化
                this.paramsBytes[i] = bytes;
            }
        }
    }

    public Object[] getParams(Type[] valueTypes) {
        if (this.params == null) {
            this.params = new Object[valueTypes.length];
        }
        for (int i = 0; i < valueTypes.length; i++) {
            Type valueType = valueTypes[i];
            byte[] bytes = this.paramsBytes[i];
            this.params[i] = getSerializer().deserialize(bytes, valueType);
        }
        return this.params;
    }

}
