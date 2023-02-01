package com.herenpeng.rpc.server;

import com.herenpeng.rpc.RpcMsg;
import com.herenpeng.rpc.RpcReq;
import com.herenpeng.rpc.RpcRsp;
import com.herenpeng.rpc.util.JsonUtils;
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
        if (obj instanceof RpcMsg) {
            // 处理逻辑
            RpcMsg msg = (RpcMsg) obj;
            switch (msg.getType()) {
                case RpcMsg.TYPE_EMPTY:
                    ctx.writeAndFlush(msg);
                    logger.info("[RPC服务端]接收心跳消息，消息序列号：{}", msg.getSequence());
                    break;
                case RpcMsg.TYPE_REQ:
                    logger.info("[RPC服务端]接收RPC请求消息，消息序列号：{}", msg.getSequence());
                    RpcReq rpcReq = JsonUtils.toObject(msg.getData(), RpcReq.class);
                    RpcRsp rpcRsp = rpcServer.invoke(rpcReq);
                    if (rpcRsp.getReturnData() != null || rpcRsp.getException() != null) {
                        logger.info("[RPC服务端]响应RPC请求消息，消息序列号：{}", msg.getSequence());
                        byte[] data = JsonUtils.toBytes(rpcRsp);
                        RpcMsg rpcMsg = new RpcMsg(RpcMsg.TYPE_RSP, msg.getSequence(), data);
                        ctx.writeAndFlush(rpcMsg);
                    }
                    break;
                case RpcMsg.TYPE_RSP:
                    break;
                case RpcMsg.TYPE_ERROR:
                    break;
                default:
                    logger.error("[RPC服务端]错误的消息类型：{}，消息序列号：{}", msg.getType(), msg.getSequence());
            }
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
