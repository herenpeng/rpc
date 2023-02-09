package com.herenpeng.rpc.proto;

import com.herenpeng.rpc.proto.Protocol;
import com.herenpeng.rpc.proto.ProtocolManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author herenpeng
 */
public class RpcDecoder extends ByteToMessageDecoder {

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
