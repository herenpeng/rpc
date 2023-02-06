package com.herenpeng.rpc.server;

import com.herenpeng.rpc.proto.*;
import com.herenpeng.rpc.kit.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author herenpeng
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

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
        logger.info("[RPC服务端]已建立链接，channel.isActive():{}，channelId:{}，", channel.isActive(), channelId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        logger.info("[RPC服务端]已断开链接，channel.isActive():{}，channelId:{}，", channel.isActive(), channelId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        logger.info("[RPC服务端]发生异常，channel.isActive():{}，channelId:{}", channel.isActive(), channelId);
        cause.printStackTrace();
    }


}
