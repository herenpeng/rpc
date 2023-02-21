package com.herenpeng.rpc.proto.content;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.proto.Protocol;
import com.herenpeng.rpc.proto.ProtocolProcessor;
import com.herenpeng.rpc.server.RpcServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author herenpeng
 * @since 2023-02-06 20:37
 */
@Slf4j
public class RpcProtocolProcessor implements ProtocolProcessor {

    @Override
    public Protocol decode(ByteBuf in) {
        int type = in.readByte();
        Protocol protocol;
        switch (type) {
            case RpcProtocol.TYPE_EMPTY:
                protocol = new RpcProtocol();
                break;
            case RpcProtocol.TYPE_REQ:
                protocol = new RpcRequest(RpcProtocol.TYPE_REQ);
                break;
            case RpcProtocol.TYPE_RSP:
                protocol = new RpcResponse(RpcProtocol.TYPE_RSP);
                break;
            default:
                throw new RpcException("[RPC协议]错误的消息类型：" + type);
        }
        protocol.decode(in);
        return protocol;
    }

    @Override
    public void handleClient(RpcServerProxy rpcServerProxy, ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof RpcResponse) {
            // 处理逻辑
            RpcResponse response = (RpcResponse) obj;
            switch (response.getType()) {
                case RpcProtocol.TYPE_EMPTY:
                    // 处理RPC心跳
                    rpcServerProxy.confirmHeartbeat(response.getSequence());
                    break;
                case RpcProtocol.TYPE_RSP:
                    // 处理RPC服务端响应
                    rpcServerProxy.setRpcResponse(response.getSequence(), response);
                    break;
                default:
                    log.error("[RPC客户端]错误的消息类型：{}，消息序列号：{}", response.getType(), response.getSequence());
            }
        }
    }

    @Override
    public void handleServer(RpcServer rpcServer, ChannelHandlerContext ctx, Object obj) {
        if (obj instanceof RpcRequest) {
            // 处理逻辑
            RpcRequest request = (RpcRequest) obj;
            switch (request.getType()) {
                case RpcProtocol.TYPE_EMPTY:
                    rpcServer.handleHeartbeat(request, ctx);
                    break;
                case RpcProtocol.TYPE_REQ:
                    log.info("[RPC服务端]接收RPC请求消息，消息序列号：{}", request.getSequence());
                    RpcResponse response = rpcServer.invoke(request);
                    ctx.writeAndFlush(response);
                    log.info("[RPC服务端]响应RPC请求消息，消息序列号：{}", request.getSequence());
                    break;
                default:
                    log.error("[RPC服务端]错误的消息类型：{}，消息序列号：{}", request.getType(), request.getSequence());
            }
        }
    }
}
