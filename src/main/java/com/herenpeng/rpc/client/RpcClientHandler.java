package com.herenpeng.rpc.client;

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
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private final RpcServerProxy rpcServerProxy;

    public RpcClientHandler(RpcServerProxy rpcServerProxy) {
        this.rpcServerProxy = rpcServerProxy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof RpcMsg) {
            // 处理逻辑
            RpcMsg msg = (RpcMsg) obj;
            switch (msg.getType()) {
                case RpcMsg.TYPE_EMPTY:
                    // 处理RPC心跳
                    rpcServerProxy.confirmHeartbeat(msg.getSequence());
                    break;
                case RpcMsg.TYPE_REQ:
                    break;
                case RpcMsg.TYPE_RSP:
                    // 处理RPC服务端响应
                    RpcRsp rpcRsp = JsonUtils.toObject(msg.getData(), RpcRsp.class);
                    rpcServerProxy.setRpcRsp(msg.getSequence(), rpcRsp);
                    break;
                case RpcMsg.TYPE_ERROR:
                    break;
                default:
                    logger.error("[RPC服务端]错误的请求类型：{}，请求序列号：{}", msg.getType(), msg.getSequence());
            }
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
