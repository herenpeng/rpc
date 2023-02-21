package com.herenpeng.rpc.proto.content;

import com.herenpeng.rpc.common.RpcMethodLocator;
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
public class RpcRequest extends RpcProtocol {

    private RpcMethodLocator methodLocator;

    private String methodPath;

    private Object[] params;

    public RpcRequest(byte type) {
        super(type);
    }


    public RpcRequest(RpcMethodLocator locator, Object[] params) {
        super(TYPE_REQ);
        this.methodLocator = locator;
        this.params = params;
    }


    public RpcRequest(String path, Object[] params) {
        this.type = TYPE_REQ;
        this.methodPath = path;
        this.params = params;
    }

    private boolean isPath() {
        return false;
    }


    @Override
    public void encode(ByteBuf out) {
        super.encode(out);
        if (isPath()) {
            byte[] methodPathBytes = encode(this.methodPath);
            out.writeInt(methodPathBytes.length);
            out.writeBytes(methodPathBytes);
        } else {
            byte[] methodLocatorBytes = encode(this.methodLocator);
            if (methodLocatorBytes == null || methodLocatorBytes.length == 0) {
                out.writeInt(0);
            } else {
                out.writeInt(methodLocatorBytes.length);
                out.writeBytes(methodLocatorBytes);
            }
        }
        byte[] paramsBytes = encode(this.params);
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
                this.methodPath = decode(bytes, String.class);
            } else {
                this.methodLocator = decode(bytes, RpcMethodLocator.class);
            }
        }
        // 所有数据总长度
        length = in.readInt();
        if (length > 0) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            this.params = decode(bytes, Object[].class);
        }
    }
}
