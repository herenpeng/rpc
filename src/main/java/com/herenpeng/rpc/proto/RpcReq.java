package com.herenpeng.rpc.proto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.RpcCallback;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author herenpeng
 */
public class RpcReq implements Serializable {

    private String className;

    private String methodName;

    private String[] paramTypeNames;

    private Object[] params;

    /**
     * 使用 transient 关键字修饰，不进行序列化
     */
    @JsonIgnore
    private transient RpcCallback<?> callback;


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

    public RpcCallback<?> getCallback() {
        return callback;
    }

    public void setCallback(RpcCallback<?> callback) {
        this.callback = callback;
    }

    public RpcReq() {

    }

    public RpcReq(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcReq rpcReq = (RpcReq) o;
        return Objects.equals(className, rpcReq.className) &&
                Objects.equals(methodName, rpcReq.methodName) &&
                Arrays.equals(paramTypeNames, rpcReq.paramTypeNames) &&
                Arrays.equals(params, rpcReq.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(className, methodName);
        result = 31 * result + Arrays.hashCode(paramTypeNames);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toString() {
        return "RpcReq{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypeNames=" + Arrays.toString(paramTypeNames) +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
