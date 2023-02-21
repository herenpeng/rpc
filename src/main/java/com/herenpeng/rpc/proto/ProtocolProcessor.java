package com.herenpeng.rpc.proto;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.server.RpcServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author herenpeng
 * @since 2023-02-06 20:35
 */
public interface ProtocolProcessor {

    /**
     * 解码处理
     *
     * @return
     */
    Protocol decode(ByteBuf in);

    /**
     * 客户端受到消息的处理方法
     *
     * @param serverProxy 客户端的服务端代理类
     * @param ctx         通道
     * @param obj         协议对象
     */
    void handleClient(RpcServerProxy serverProxy, ChannelHandlerContext ctx, Object obj);

    /**
     * 服务端受到消息的处理方法
     *
     * @param rpcServer 服务端
     * @param ctx       通道
     * @param obj       协议对象
     */
    void handleServer(RpcServer rpcServer, ChannelHandlerContext ctx, Object obj);

}
