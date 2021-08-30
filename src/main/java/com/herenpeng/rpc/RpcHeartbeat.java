package com.herenpeng.rpc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author herenpeng
 */
public class RpcHeartbeat implements Serializable {

    private final long id;

    public RpcHeartbeat(AtomicLong heartbeatId) {
        this.id = heartbeatId.incrementAndGet();
    }

    public long id() {
        return this.id;
    }

}
