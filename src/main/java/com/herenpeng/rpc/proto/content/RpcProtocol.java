package com.herenpeng.rpc.proto.content;

import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.JsonUtils;
import com.herenpeng.rpc.proto.Protocol;
import com.herenpeng.rpc.proto.ProtocolProcessor;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author herenpeng
 * @since 2023-02-01 20:37
 * <p>
 * 协议内容，两个'+'之间的长度为一个字节
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 * |   version   |    type     |   subType   |     sequence                                          |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 * |  serialize  |                            data                                                   |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 * |                                                                                                 |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RpcProtocol implements Protocol {

    private static final AtomicInteger protoSequence = new AtomicInteger();

    public static final byte TYPE_REQUEST = 0;
    public static final byte TYPE_RESPONSE = 1;

    /**
     * 空消息
     */
    public static final byte SUB_TYPE_EMPTY = 0;
    /**
     * 消息
     */
    public static final byte SUB_TYPE_MESSAGE = 1;

    /**
     * Json序列化方式
     */
    private static final byte SERIALIZE_JSON = 1;


    public static final ProtocolProcessor processor = new RpcProtocolProcessor();

    /**
     * 协议版本号，用于后期扩展协议，默认为1
     */
    private byte version = VERSION_1;
    /**
     * 消息类型
     */
    private byte type;

    private byte subType;
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

    public RpcProtocol(byte type, byte subType) {
        this.type = type;
        this.subType = subType;
        this.sequence = protoSequence.incrementAndGet();
    }

    public RpcProtocol(byte type, byte subType, int sequence) {
        this.type = type;
        this.subType = subType;
        this.sequence = sequence;
    }

    // 序列化协议具体内容的数据
    protected byte[] serialize(Object data) {
        if (this.serialize == SERIALIZE_JSON) {
            return JsonUtils.toBytes(data);
        }
        throw new RpcException("[RPC协议]暂不支持该序列化方式");
    }

    // 将协议具体内容转化为具体的对象
    public <T> T deserialize(byte[] bytes, Class<T> classObject) {
        if (this.serialize == SERIALIZE_JSON) {
            return JsonUtils.toObject(bytes, classObject);
        }
        throw new RpcException("[RPC协议]暂不支持该序列化方式");
    }

    /**
     * 编码方法
     */
    @Override
    public void encode(ByteBuf out) {
        out.writeByte(type);
        out.writeByte(subType);
        out.writeInt(sequence);
        out.writeByte(serialize);
    }

    /**
     * 解码方法
     */
    @Override
    public void decode(ByteBuf in) {
        subType = in.readByte();
        sequence = in.readInt();
        serialize = in.readByte();
    }

    @Override
    public ProtocolProcessor getProcessor() {
        return processor;
    }

    @Override
    public byte getVersion() {
        return version;
    }

}
