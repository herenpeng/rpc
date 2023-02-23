package com.herenpeng.rpc.client;

import com.herenpeng.rpc.protocol.Protocol;
import com.herenpeng.rpc.protocol.ProtocolProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private final RpcServerProxy rpcServerProxy;

    public RpcClientHandler(RpcServerProxy rpcServerProxy) {
        this.rpcServerProxy = rpcServerProxy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        Protocol protocol = (Protocol) obj;
        ProtocolProcessor processor = protocol.getProcessor();
        processor.handleClient(rpcServerProxy, ctx, obj);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        log.info("[RPC客户端]发生异常，channelId:{}，", channelId);
        cause.printStackTrace();
    }


}
