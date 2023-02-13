package com.herenpeng.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author herenpeng
 * @since 2021-09-07 23:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcConfig {

    private RpcClientConfig client;

    private RpcServerConfig server;

}
