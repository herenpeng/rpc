package com.herenpeng.rpc.protocol.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.kit.BitKit;
import com.herenpeng.rpc.kit.RpcCallback;
import com.herenpeng.rpc.kit.RpcKit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

    private int cmd;
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


    public RpcRequest(byte subType, byte serialize, int compressEnableSize) {
        super(RpcProtocol.TYPE_REQUEST, subType, serialize);
        setCompressEnableSize(compressEnableSize);
    }

    /**
     * 带方法定位符的构造，默认为消息类型
     *
     * @param cmd        cmd
     * @param params     请求参数
     * @param returnType 返回类型
     * @param async      是否异步
     * @param callable   回调函数，同步为null
     */
    public RpcRequest(int cmd, Object[] params, Type returnType, boolean async,
                      RpcCallback<T> callable, byte serialize, int compressEnableSize) {
        super(RpcProtocol.TYPE_REQUEST, RpcProtocol.SUB_TYPE_MESSAGE, serialize);
        this.cmd = cmd;
        this.params = params;
        this.returnType = returnType;
        this.async = async;
        this.callable = callable;
        setCompressEnableSize(compressEnableSize);
    }

    public RpcRequest(int cmd, Object[] params, Type returnType, RpcCallback<T> callable, byte serialize, int compressEnableSize) {
        super(RpcProtocol.TYPE_REQUEST, RpcProtocol.SUB_TYPE_MESSAGE, serialize);
        this.cmd = cmd;
        this.params = params;
        this.returnType = returnType;
        this.async = callable != null;
        this.callable = callable;
        setCompressEnableSize(compressEnableSize);
    }


    @Override
    public void encodeData(ByteBuf out) {
        // 编码
        out.writeInt(this.cmd);
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
    public void decodeData(ByteBuf in) {
        // 解码
        this.cmd = in.readInt();
        // 参数个数
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
