package com.herenpeng.rpc.protocol.content;

import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.serialize.Serializer;
import com.herenpeng.rpc.kit.serialize.SerializerManager;
import com.herenpeng.rpc.protocol.Protocol;
import com.herenpeng.rpc.protocol.ProtocolProcessor;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author herenpeng
 * @since 2023-02-01 20:37
 * <p>
 * 协议内容，两个'+'之间的长度为一个字节
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 * |   version   |    type     |   subType   |                  sequence                             |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 * |  serialize  |                            data                                                   |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 * |                                                                                                 |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 */
@Slf4j
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
    private byte serialize;
    /**
     * 传输的消息数据
     */
    private byte[] data;

    public RpcProtocol(byte type, byte subType, byte serialize) {
        this.type = type;
        this.subType = subType;
        this.sequence = protoSequence.incrementAndGet();
        this.serialize = serialize;
    }

    public RpcProtocol(byte type, byte subType, int sequence, byte serialize) {
        this.type = type;
        this.subType = subType;
        this.sequence = sequence;
        this.serialize = serialize;
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

    protected Serializer getSerializer() {
        Serializer serializer = SerializerManager.getSerializer(serialize);
        if (serializer == null) {
            log.error("[RPC协议]序列化工具不存在，序列化Id：{}", serialize);
            throw new RpcException("[RPC协议]序列化工具不存在，序列化Id：" + serialize);
        }
        return serializer;
    }

}
