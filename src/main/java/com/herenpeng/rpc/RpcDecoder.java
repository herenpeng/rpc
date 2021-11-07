package com.herenpeng.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * @author herenpeng
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 解码，将字节转化为 RpcReq 或者 RpcRsp 信息对象
        final byte[] bytes;
        final int length = in.readableBytes();
        bytes = new byte[length];
        in.getBytes(in.readerIndex(), bytes, 0, length);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object object = ois.readObject();

        out.add(object);
        in.skipBytes(in.readableBytes());
    }
}
