package com.herenpeng.rpc.protocol.content;

import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.BitKit;
import com.herenpeng.rpc.kit.RpcKit;
import com.herenpeng.rpc.kit.serialize.Serializer;
import com.herenpeng.rpc.kit.serialize.SerializerManager;
import com.herenpeng.rpc.protocol.Protocol;
import com.herenpeng.rpc.protocol.ProtocolProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
 * |  serialize  |   status    |               data                                                  |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 * |                                                                                                 |
 * +-------------+-------------+-------------+-------------+-------------+-------------+-------------+
 */
@Slf4j
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public abstract class RpcProtocol implements Protocol {

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
     * 内部使用的一些消息，比如：rpc请求列表，监控功能
     */
    public static final byte SUB_TYPE_INTERNAL = 2;

    /**
     * 压缩标识位
     */
    protected static final int STATUS_COMPRESS = 0;

    public static final ProtocolProcessor processor = new RpcProtocolProcessor();

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
     * 标识位，用来表示一些状态
     */
    protected byte status;
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
        ByteBuf buffer = Unpooled.buffer();
        encodeData(buffer);
        byte[] data = RpcKit.getBytes(buffer);
        if (data.length > 1024 * 100) {
            data = RpcKit.compress(data);
            this.status = (byte) BitKit.setBit(this.status, STATUS_COMPRESS);
        }
        out.writeByte(type);
        out.writeByte(subType);
        out.writeInt(sequence);
        out.writeByte(serialize);
        out.writeByte(status);
        out.writeBytes(data);
    }

    protected abstract void encodeData(ByteBuf buffer);

    /**
     * 解码方法
     */
    @Override
    public void decode(ByteBuf in) {
        subType = in.readByte();
        sequence = in.readInt();
        serialize = in.readByte();
        status = in.readByte();
        byte[] data = RpcKit.getBytes(in);
        if (BitKit.getBit(this.status, STATUS_COMPRESS) == 1) {
            // 有压缩，执行解压缩
            data = RpcKit.decompress(data);
        }
        decodeData(Unpooled.wrappedBuffer(data));
    }


    protected abstract void decodeData(ByteBuf in);


    @Override
    public ProtocolProcessor getProcessor() {
        return processor;
    }

    /**
     * 协议版本号，用于后期扩展协议，默认为1
     */
    @Override
    public byte getVersion() {
        return VERSION_1;
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
