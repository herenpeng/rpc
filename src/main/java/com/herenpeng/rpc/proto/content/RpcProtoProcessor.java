package com.herenpeng.rpc.proto.content;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.proto.ProtocolProcessor;
import com.herenpeng.rpc.server.RpcServer;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author herenpeng
 * @since 2023-02-06 20:37
 */
@Slf4j
public class RpcProtoProcessor implements ProtocolProcessor {

    @Override
    public void handleClient(RpcServerProxy rpcServerProxy, ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof RpcProto) {
            // 处理逻辑
            RpcProto proto = (RpcProto) obj;
            switch (proto.getType()) {
                case RpcProto.TYPE_EMPTY:
                    // 处理RPC心跳
                    rpcServerProxy.confirmHeartbeat(proto.getSequence());
                    break;
                case RpcProto.TYPE_REQ:
                    break;
                case RpcProto.TYPE_RSP:
                    // 处理RPC服务端响应
                    RpcRsp rpcRsp = proto.getData(RpcRsp.class);
                    rpcServerProxy.setRpcRsp(proto.getSequence(), rpcRsp);
                    break;
                default:
                    log.error("[RPC客户端]错误的请求类型：{}，请求序列号：{}", proto.getType(), proto.getSequence());
            }
        }
    }

    @Override
    public void handleServer(RpcServer rpcServer, ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof RpcProto) {
            // 处理逻辑
            RpcProto proto = (RpcProto) obj;
            switch (proto.getType()) {
                case RpcProto.TYPE_EMPTY:
                    rpcServer.handleHeartbeat(proto, ctx);
                    break;
                case RpcProto.TYPE_REQ:
                    log.info("[RPC服务端]接收RPC请求消息，消息序列号：{}", proto.getSequence());
                    RpcReq rpcReq = proto.getData(RpcReq.class);
                    RpcRsp rpcRsp = rpcServer.invoke(rpcReq);
                    RpcProto rpcProto = new RpcProto(RpcProto.TYPE_RSP, proto.getSequence(), rpcRsp);
                    ctx.writeAndFlush(rpcProto);
                    log.info("[RPC服务端]响应RPC请求消息，消息序列号：{}", proto.getSequence());
                    break;
                case RpcProto.TYPE_RSP:
                    break;
                default:
                    log.error("[RPC服务端]错误的消息类型：{}，消息序列号：{}", proto.getType(), proto.getSequence());
            }
        }
    }
}
