package com.herenpeng.rpc.proto;

import com.herenpeng.rpc.exception.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 * @since 2023-02-03 21:43
 */
public class ProtocolManager {

    private static final Map<Byte, Protocol> protocols = new ConcurrentHashMap<>();

    static {
        // 初始化该管理类的时候注册所有协议版本，这些都是内置的版本号
        protocols.put(Protocol.VERSION_1, new RpcProto());
    }

    /**
     * 如果已有的版本协议无法满足用户自己的需求，可以用户自己实现相关的版本协议
     * 且该版本的协议号将会覆盖内置的协议
     */
    public static void registerProtocol(Protocol protocol) {
        if (protocol == null) {
            throw new RpcException("[RPC协议]注册的RPC协议对象不能为空");
        }
        protocols.put(protocol.getVersion(), protocol);
    }


    public static Protocol getProtocol(byte version) {
        return protocols.get(version).newBuilder().builder();
    }


}
