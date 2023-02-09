package com.herenpeng.rpc.proto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.kit.RpcCallback;
import lombok.*;

import java.io.Serializable;

/**
 * @author herenpeng
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
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

    public RpcReq(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }


}
