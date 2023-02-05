package com.herenpeng.rpc.proto;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author herenpeng
 * @since 2023-02-01 20:37
 */
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
    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public byte getSerialize() {
        return serialize;
    }

    public void setSerialize(byte serialize) {
        this.serialize = serialize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcProto rpcProto = (RpcProto) o;
        return version == rpcProto.version &&
                type == rpcProto.type &&
                sequence == rpcProto.sequence &&
                serialize == rpcProto.serialize &&
                Arrays.equals(data, rpcProto.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(version, type, sequence, serialize);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "RpcProto{" +
                "version=" + version +
                ", type=" + type +
                ", sequence=" + sequence +
                ", serialize=" + serialize +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
