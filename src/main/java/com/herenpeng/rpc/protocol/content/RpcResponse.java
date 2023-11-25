package com.herenpeng.rpc.protocol.content;

import com.herenpeng.rpc.kit.BitKit;
import com.herenpeng.rpc.kit.RpcKit;
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
public class RpcResponse extends RpcProtocol {

    private Object returnData;

    private byte[] returnBytes;

    private String exception;

    public RpcResponse(byte subType, int sequence, byte serialize) {
        super(TYPE_RESPONSE, subType, sequence, serialize);
    }


    @Override
    public void encode(ByteBuf out) {
        // 编码返回信息为字节
        this.returnBytes = getSerializer().serialize(this.returnData);
        if (returnBytes.length > 1024 * 100) {
            returnBytes = RpcKit.compress(this.returnBytes);
            this.status = (byte) BitKit.setBit(this.status, STATUS_COMPRESS);
        }
        super.encode(out);
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
    public void decode(ByteBuf in) {
        super.decode(in);
        int length = in.readInt();
        if (length > 0) {
            this.returnBytes = new byte[length];
            // 解码的时候只将其解析为字节，由后续根据类型反序列化
            in.readBytes(returnBytes);
            if (BitKit.getBit(this.status, STATUS_COMPRESS) == 1) {
                // 有压缩
                returnBytes = RpcKit.decompress(returnBytes);
            }
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
