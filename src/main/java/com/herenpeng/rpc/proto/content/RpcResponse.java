package com.herenpeng.rpc.proto.content;

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
public class RpcResponse extends RpcProtocol {

    private Object returnData;

    private byte[] returnBytes;

    private String exception;

    public RpcResponse(int sequence) {
        super(TYPE_RSP, sequence);
    }


    @Override
    public void encode(ByteBuf out) {
        super.encode(out);
        // 编码返回信息为字节
        this.returnBytes = encode(this.returnData);
        if (this.returnBytes == null || this.returnBytes.length == 0) {
            out.writeInt(0);
        } else {
            out.writeInt(this.returnBytes.length);
            out.writeBytes(this.returnBytes);
        }
        // 编码异常信息
        byte[] exceptionBytes = encode(this.exception);
        if (exceptionBytes == null || exceptionBytes.length == 0) {
            out.writeInt(0);
        } else {
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
            in.readBytes(returnBytes);
            // 解码的时候只将其解析为字节，由后续根据类型反序列化
            // this.returnData = decode(returnBytes, Object.class);
        }
        // 所有数据总长度
        length = in.readInt();
        if (length > 0) {
            byte[] exceptionBytes = new byte[length];
            in.readBytes(exceptionBytes);
            this.exception = decode(exceptionBytes, String.class);
        }
    }

    public <T> T getReturnData(Class<T> returnType) {
        T data = decode(returnBytes, returnType);
        this.returnData = data;
        return data;
    }

}
