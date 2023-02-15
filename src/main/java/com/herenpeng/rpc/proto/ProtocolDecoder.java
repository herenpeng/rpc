package com.herenpeng.rpc.proto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author herenpeng
 */
public class ProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 解码
        byte version = in.readByte();
        Protocol protocol = ProtocolManager.getProtocol(version);
        protocol.decode(in);
        out.add(protocol);
        in.skipBytes(in.readableBytes());
    }
}
