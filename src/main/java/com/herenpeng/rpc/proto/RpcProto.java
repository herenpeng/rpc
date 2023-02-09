package com.herenpeng.rpc.proto;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author herenpeng
 * @since 2023-02-01 20:37
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class RpcProto implements Protocol {

    /**
     * 空消息
     */
    public static final byte TYPE_EMPTY = 0;
    /**
     * 请求消息
     */
    public static final byte TYPE_REQ = 1;
    /**
     * 响应消息
     */
    public static final byte TYPE_RSP = 2;

    private static final ProtocolProcessor processor = new RpcProtoProcessor();

    /**
     * 协议版本号，用于后期扩展协议，默认为1
     */
    private byte version = VERSION_1;
    /**
     * 消息类型
     */
    private byte type;
    /**
     * 序列号
     */
    private int sequence;
    /**
     * 序列化方式
     */
    private byte serialize;
    /**
     * 传输的消息数据
     */
    private byte[] data;

    public RpcProto() {

    }

    public RpcProto(byte type, int sequence) {
        this.type = type;
        this.sequence = sequence;
    }

    public RpcProto(byte type, int sequence, byte[] data) {
        this.type = type;
        this.sequence = sequence;
        this.data = data;
    }

    /**
     * 编码方法
     */
    @Override
    public void encode(ByteBuf out) {
        out.writeByte(type);
        out.writeInt(sequence);
        out.writeByte(serialize);
        if (data != null && data.length > 0) {
            out.writeBytes(data);
        }
    }

    /**
     * 解码方法
     */
    @Override
    public void decode(ByteBuf in) {
        type = in.readByte();
        sequence = in.readInt();
        serialize = in.readByte();
        // 所有数据总长度
        int length = in.readableBytes();
        if (length > 0) {
            data = new byte[length];
            in.readBytes(data);
        }
    }

    @Override
    public ProtocolProcessor getProcessor() {
        return processor;
    }

    @Override
    public byte getVersion() {
        return version;
    }


    @Override
    public ProtocolBuilder newBuilder() {
        return new Builder();
    }

    public static class Builder implements ProtocolBuilder {

        @Override
        public Protocol builder() {
            return new RpcProto();
        }
    }

}
