package com.herenpeng.rpc.proto.content;

import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.JsonUtils;
import com.herenpeng.rpc.proto.Protocol;
import com.herenpeng.rpc.proto.ProtocolBuilder;
import com.herenpeng.rpc.proto.ProtocolProcessor;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author herenpeng
 * @since 2023-02-01 20:37
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class RpcProto implements Protocol {

    public static final AtomicInteger protoSequence = new AtomicInteger();

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
     * Json序列化方式
     */
    public static final byte SERIALIZE_JSON = 1;


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
    private byte serialize = SERIALIZE_JSON;
    /**
     * 传输的消息数据
     */
    private byte[] data;

    public RpcProto() {

    }

    public RpcProto(byte type) {
        this.type = type;
        this.sequence = protoSequence.incrementAndGet();
    }

    public RpcProto(byte type, Object data) {
        this.type = type;
        this.sequence = protoSequence.incrementAndGet();
        this.data = serialize(data);
    }

    public RpcProto(byte type, int sequence, Object data) {
        this.type = type;
        this.sequence = sequence;
        this.data = serialize(data);
    }

    // 序列化协议具体内容的数据
    private byte[] serialize(Object data) {
        if (this.serialize == SERIALIZE_JSON) {
            return JsonUtils.toBytes(data);
        }
        throw new RpcException("[RPC协议]暂不支持该序列化方式");
    }

    // 将协议具体内容转化为具体的对象
    public <T> T getData(Class<T> classObject) {
        if (this.serialize == SERIALIZE_JSON) {
            return JsonUtils.toObject(this.data, classObject);
        }
        throw new RpcException("[RPC协议]暂不支持该序列化方式");
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
