package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcCallback;
import com.herenpeng.rpc.client.RpcClient;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author herenpeng
 */
public class RpcReq implements Serializable {

    private final long id;

    private final String className;

    private final String methodName;

    private String[] paramTypeNames;

    private Object[] params;

    // 使用 transient 关键字修饰，不进行序列化
    private transient RpcCallback callback;

    public long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getParamTypeNames() {
        return paramTypeNames;
    }

    public void setParamTypeNames(String[] paramTypeNames) {
        this.paramTypeNames = paramTypeNames;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public RpcCallback getCallback() {
        return callback;
    }

    public void setCallback(RpcCallback callback) {
        this.callback = callback;
    }

    public RpcReq(AtomicLong rpcReqId, String className, String methodName) {
        this.id = rpcReqId.incrementAndGet();
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "RpcReq{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramClassNames=" + Arrays.toString(paramTypeNames) +
                ", params=" + Arrays.toString(params) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcReq rpcReq = (RpcReq) o;
        return id == rpcReq.id && Objects.equals(className, rpcReq.className) && Objects.equals(methodName, rpcReq.methodName) && Arrays.equals(paramTypeNames, rpcReq.paramTypeNames) && Arrays.equals(params, rpcReq.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, className, methodName);
        result = 31 * result + Arrays.hashCode(paramTypeNames);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }
}
