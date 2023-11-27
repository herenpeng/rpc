package com.herenpeng.rpc.protocol.content;

import com.herenpeng.rpc.kit.BitKit;
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
public class RpcResponse extends RpcProtocol {

    private Object returnData;

    private byte[] returnBytes;

    private String exception;

    public RpcResponse(byte subType, int sequence, byte serialize) {
        super(TYPE_RESPONSE, subType, sequence, serialize);
    }


    @Override
    public void encodeData(ByteBuf out) {
        // 编码返回信息为字节
        this.returnBytes = getSerializer().serialize(this.returnData);
        if (this.returnBytes == null || this.returnBytes.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(this.returnBytes.length);
            out.writeBytes(this.returnBytes);
        }
        // 编码异常信息
        if (this.exception == null) {
            out.writeInt(0);
        } else {
            byte[] exceptionBytes = getSerializer().serialize(this.exception);
            out.writeInt(exceptionBytes.length);
            out.writeBytes(exceptionBytes);
        }
    }

    @Override
    public void decodeData(ByteBuf in) {
        // 解码
        int length = in.readInt();
        if (length > 0) {
            this.returnBytes = new byte[length];
            // 解码的时候只将其解析为字节，由后续根据类型反序列化
            in.readBytes(returnBytes);
        }
        // 异常数据总长度
        length = in.readInt();
        if (length > 0) {
            byte[] exceptionBytes = new byte[length];
            in.readBytes(exceptionBytes);
            this.exception = getSerializer().deserialize(exceptionBytes, String.class);
        }
    }

    public <T> T getReturnData(Type valueType) {
        T data = getSerializer().deserialize(returnBytes, valueType);
        this.returnData = data;
        return data;
    }

}
