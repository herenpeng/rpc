package com.herenpeng.rpc.client;

import com.herenpeng.rpc.proto.Protocol;
import com.herenpeng.rpc.proto.ProtocolProcessor;
import com.herenpeng.rpc.proto.RpcProto;
import com.herenpeng.rpc.proto.RpcRsp;
import com.herenpeng.rpc.kit.JsonUtils;
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
        Protocol protocol = (Protocol) obj;
        ProtocolProcessor processor = protocol.getProcessor();
        processor.handleClient(rpcServerProxy, ctx, obj);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        logger.info("[RPC客户端]发生异常，channelId:{}，", channelId);
        cause.printStackTrace();
    }


}
