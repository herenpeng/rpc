package com.herenpeng.rpc;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author herenpeng
 * @since 2023-02-01 20:37
 */
public class RpcMsg implements Serializable {

    public static final byte TYPE_EMPTY = 0;
    public static final byte TYPE_REQ = 1;
    public static final byte TYPE_RSP = 2;
    public static final byte TYPE_ERROR = 3;

    /**
     * 消息类型
     */
    private byte type;
    /**
     * 序列号
     */
    private long sequence;
    /**
     * 传输的消息数据
     */
    private byte[] data;

    public RpcMsg() {

    }

    public RpcMsg(byte type, long seq) {
        this.type = type;
        this.sequence = seq;
    }

    public RpcMsg(byte type, long sequence, byte[] data) {
        this.type = type;
        this.sequence = sequence;
        this.data = data;
    }

    /**
     * 编码方法
     */
    public void encode(ByteBuf out) {
        out.writeByte(type);
        out.writeLong(sequence);
        if (data != null) {
            out.writeBytes(data);
        }
    }

    /**
     * 解码方法
     */
    public void decode(ByteBuf in) {
        // 读取头消息长度
        type = in.readByte();
        sequence = in.readLong();
        // 所有数据总长度
        int length = in.readableBytes();
        if (length > 0) {
            data = new byte[length];
            in.readBytes(data);
        }
    }


    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
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
        RpcMsg rpcMsg = (RpcMsg) o;
        return type == rpcMsg.type &&
                sequence == rpcMsg.sequence &&
                Arrays.equals(data, rpcMsg.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type, sequence);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "RpcMsg{" +
                "type=" + type +
                ", sequence=" + sequence +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
