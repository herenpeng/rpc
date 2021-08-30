package com.herenpeng.rpc.client;

import com.herenpeng.rpc.RpcHeartbeat;
import com.herenpeng.rpc.RpcRsp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author herenpeng
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private final RpcServerProxy rpcServerProxy;

    public RpcClientHandler(RpcServerProxy rpcServerProxy) {
        this.rpcServerProxy = rpcServerProxy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcRsp) {
            // 处理RPC服务端响应
            RpcRsp rpcRsp = (RpcRsp) msg;
            rpcServerProxy.setRpcRsp(rpcRsp);
        } else if (msg instanceof RpcHeartbeat) {
            // 处理RPC心跳
            RpcHeartbeat rpcHeartbeat = (RpcHeartbeat) msg;
            rpcServerProxy.confirmHeartbeat(rpcHeartbeat);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        logger.info("[RPC客户端]发生异常，channelId:{}，", channelId);
        cause.printStackTrace();
    }


}
