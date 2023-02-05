package com.herenpeng.rpc.proto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.List;

/**
 * @author herenpeng
 * @since 2023-02-03 21:09
 */
public interface Protocol extends Serializable {
    /**
     * 协议版本1
     */
    byte VERSION_1 = 1;

    /**
     * 获取协议版本号
     *
     * @return
     */
    byte getVersion();

    /**
     * 编码方法
     *
     * @param out
     * @return
     */
    void encode(ByteBuf out);


    /**
     * 解码方法
     *
     * @param in
     */
    void decode(ByteBuf in);
}
