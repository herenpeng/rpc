package com.herenpeng.rpc;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author herenpeng
 */
public class RpcRsp implements Serializable {

    private final long id;

    private Object returnData;

    private String exception;

    public long getId() {
        return id;
    }

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

    public RpcRsp(RpcReq rpcReq) {
        this.id = rpcReq.getId();
    }

    @Override
    public String toString() {
        return "RpcRsp{" +
                "id=" + id +
                ", returnData=" + returnData +
                ", exception='" + exception + '\'' +
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
        RpcRsp rpcRsp = (RpcRsp) o;
        return id == rpcRsp.id && Objects.equals(returnData, rpcRsp.returnData) && Objects.equals(exception, rpcRsp.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, returnData, exception);
    }

}
