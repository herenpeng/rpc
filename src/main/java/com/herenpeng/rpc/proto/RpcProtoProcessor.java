package com.herenpeng.rpc.proto;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.kit.JsonUtils;
import com.herenpeng.rpc.server.RpcServer;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author herenpeng
 * @since 2023-02-06 20:37
 */
public class RpcProtoProcessor implements ProtocolProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RpcProtoProcessor.class);

    @Override
    public void handleClient(RpcServerProxy rpcServerProxy, ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof RpcProto) {
            // 处理逻辑
            RpcProto msg = (RpcProto) obj;
            switch (msg.getType()) {
                case RpcProto.TYPE_EMPTY:
                    // 处理RPC心跳
                    rpcServerProxy.confirmHeartbeat(msg.getSequence());
                    break;
                case RpcProto.TYPE_REQ:
                    break;
                case RpcProto.TYPE_RSP:
                    // 处理RPC服务端响应
                    RpcRsp rpcRsp = JsonUtils.toObject(msg.getData(), RpcRsp.class);
                    rpcServerProxy.setRpcRsp(msg.getSequence(), rpcRsp);
                    break;
                default:
                    logger.error("[RPC客户端]错误的请求类型：{}，请求序列号：{}", msg.getType(), msg.getSequence());
            }
        }
    }

    @Override
    public void handleServer(RpcServer rpcServer, ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof RpcProto) {
            // 处理逻辑
            RpcProto msg = (RpcProto) obj;
            switch (msg.getType()) {
                case RpcProto.TYPE_EMPTY:
                    ctx.writeAndFlush(msg);
                    logger.info("[RPC服务端]接收心跳消息，消息序列号：{}", msg.getSequence());
                    break;
                case RpcProto.TYPE_REQ:
                    logger.info("[RPC服务端]接收RPC请求消息，消息序列号：{}", msg.getSequence());
                    RpcReq rpcReq = JsonUtils.toObject(msg.getData(), RpcReq.class);
                    RpcRsp rpcRsp = rpcServer.invoke(rpcReq);
                    if (rpcRsp.getReturnData() != null || rpcRsp.getException() != null) {
                        logger.info("[RPC服务端]响应RPC请求消息，消息序列号：{}", msg.getSequence());
                        byte[] data = JsonUtils.toBytes(rpcRsp);
                        RpcProto rpcMsg = new RpcProto(RpcProto.TYPE_RSP, msg.getSequence(), data);
                        ctx.writeAndFlush(rpcMsg);
                    }
                    break;
                case RpcProto.TYPE_RSP:
                    break;
                default:
                    logger.error("[RPC服务端]错误的消息类型：{}，消息序列号：{}", msg.getType(), msg.getSequence());
            }
        }
    }
}
