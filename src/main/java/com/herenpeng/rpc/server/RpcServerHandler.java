package com.herenpeng.rpc.server;

import com.herenpeng.rpc.protocol.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcServer rpcServer;

    public RpcServerHandler(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        Protocol protocol = (Protocol) obj;
        ProtocolProcessor processor = protocol.getProcessor();
        processor.handleServer(rpcServer, ctx, obj);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        log.info("[RPC服务端]已建立链接，channel.isActive():{}，channelId:{}，", channel.isActive(), channelId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        log.info("[RPC服务端]已断开链接，channel.isActive():{}，channelId:{}，", channel.isActive(), channelId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        log.info("[RPC服务端]发生异常，channel.isActive():{}，channelId:{}", channel.isActive(), channelId);
        cause.printStackTrace();
    }


}
