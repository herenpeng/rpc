package com.herenpeng.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author herenpeng
 * @since 2021-09-07 23:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcConfig implements Serializable {

    private RpcClientConfig client;

    private List<RpcClientConfig> clients;

    private RpcServerConfig server;

    private List<RpcServerConfig> servers;

}
