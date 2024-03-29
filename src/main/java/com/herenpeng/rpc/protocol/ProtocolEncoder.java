package com.herenpeng.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author herenpeng
 */
public class ProtocolEncoder extends MessageToByteEncoder<Protocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol protocol, ByteBuf out) throws Exception {
        // 编码，版本号在这里写入，是为了保持实现方法的一致性
        out.writeByte(protocol.getVersion());
        protocol.encode(out);
    }

}
