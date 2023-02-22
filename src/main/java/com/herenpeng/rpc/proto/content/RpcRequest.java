package com.herenpeng.rpc.proto.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.kit.RpcCallback;
import io.netty.buffer.ByteBuf;
import lombok.*;

/**
 * @author herenpeng
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest<T> extends RpcProtocol {

    private RpcMethodLocator methodLocator;

    private String methodPath;

    private Object[] params;

    @JsonIgnore
    private Class<T> returnType;

    /**
     * 用于标识请求是否异步，真正的异步判断字段
     */
    @JsonIgnore
    private boolean async;

    @JsonIgnore
    private RpcCallback<T> callable;

    public RpcRequest(byte subType) {
        super(RpcProtocol.TYPE_REQUEST, subType);
    }

    // 带方法定位符的构造，默认为消息类型
    public RpcRequest(RpcMethodLocator locator, Object[] params, Class<T> returnType,boolean async,  RpcCallback<T> callable) {
        super(RpcProtocol.TYPE_REQUEST, RpcProtocol.SUB_TYPE_MESSAGE);
        this.methodLocator = locator;
        this.params = params;
        this.returnType = returnType;
        this.async = async;
        this.callable = callable;
    }

    public RpcRequest(String path, Object[] params, Class<T> returnType,boolean async,  RpcCallback<T> callable) {
        super(RpcProtocol.TYPE_REQUEST, RpcProtocol.SUB_TYPE_MESSAGE);
        this.methodPath = path;
        this.params = params;
        this.returnType = returnType;
        this.async = async;
        this.callable = callable;
    }

    private boolean isPath() {
        return false;
    }


    @Override
    public void encode(ByteBuf out) {
        super.encode(out);
        if (isPath()) {
            byte[] methodPathBytes = serialize(this.methodPath);
            out.writeInt(methodPathBytes.length);
            out.writeBytes(methodPathBytes);
        } else {
            byte[] methodLocatorBytes = serialize(this.methodLocator);
            if (methodLocatorBytes == null || methodLocatorBytes.length == 0) {
                out.writeInt(0);
            } else {
                out.writeInt(methodLocatorBytes.length);
                out.writeBytes(methodLocatorBytes);
            }
        }
        byte[] paramsBytes = serialize(this.params);
        if (paramsBytes == null || paramsBytes.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(paramsBytes.length);
            out.writeBytes(paramsBytes);
        }
    }

    @Override
    public void decode(ByteBuf in) {
        super.decode(in);
        int length = in.readInt();
        if (length > 0) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            if (isPath()) {
                this.methodPath = deserialize(bytes, String.class);
            } else {
                this.methodLocator = deserialize(bytes, RpcMethodLocator.class);
            }
        }
        // 所有数据总长度
        length = in.readInt();
        if (length > 0) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            this.params = deserialize(bytes, Object[].class);
        }
    }
}
