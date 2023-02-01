package com.herenpeng.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * @author herenpeng
 */
public class RpcEncoder extends MessageToByteEncoder<RpcMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg msg, ByteBuf out) throws Exception {
        // 编码
        msg.encode(out);
    }

}
