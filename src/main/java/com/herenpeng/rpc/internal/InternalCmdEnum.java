package com.herenpeng.rpc.internal;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public enum InternalCmdEnum {

    RPC_TABLE(1, "RPC列表"),
    MONITOR(2, "性能监控");

    private int cmd;
    private String describe;


}
