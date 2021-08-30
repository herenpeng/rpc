package com.herenpeng.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author herenpeng
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            // 编码，将 rpc信息 转化为字节
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(msg);
            byte[] bytes = bos.toByteArray();
            //将数组写入到ByteBuf中
            out.writeBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
