package com.herenpeng.rpc;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author herenpeng
 */
public class RpcRsp implements Serializable {

    private Object returnData;

    private String exception;

    public Object getReturnData() {
        return returnData;
    }

    public void setReturnData(Object returnData) {
        this.returnData = returnData;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public RpcRsp() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcRsp rpcRsp = (RpcRsp) o;
        return Objects.equals(returnData, rpcRsp.returnData) &&
                Objects.equals(exception, rpcRsp.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnData, exception);
    }

    @Override
    public String toString() {
        return "RpcRsp{" +
                "returnData=" + returnData +
                ", exception='" + exception + '\'' +
                '}';
    }
}
