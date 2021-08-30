package com.herenpeng.rpc.server;

import com.herenpeng.rpc.RpcHeartbeat;
import com.herenpeng.rpc.RpcReq;
import com.herenpeng.rpc.RpcRsp;
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcReq) {
            // 处理RPC客户端请求
            RpcReq rpcReq = (RpcReq) msg;
            logger.info("[RPC服务端]接受到RPC请求：" + rpcReq);
            RpcRsp rpcRsp = rpcServer.invoke(rpcReq);
            if (rpcRsp.getReturnData() != null || rpcRsp.getException() != null) {
                logger.info("R[RPC服务端]响应RPC请求：" + rpcRsp);
                ctx.writeAndFlush(rpcRsp);
            }
        } else if (msg instanceof RpcHeartbeat) {
            // 处理RPC心跳
            RpcHeartbeat rpcHeartbeat = (RpcHeartbeat) msg;
            ctx.writeAndFlush(rpcHeartbeat);
            logger.info("[RPC服务端]接收到心跳，心跳id：{}", rpcHeartbeat.id());
        }
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
