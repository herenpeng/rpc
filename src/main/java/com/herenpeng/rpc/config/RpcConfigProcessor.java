package com.herenpeng.rpc.config;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author herenpeng
 * @since 2023-02-09 21:51
 */
@Data
@Slf4j
public class RpcConfigProcessor {

    private RpcConfig rpc;

    public RpcConfigProcessor(String configFile) {
        init(configFile);
    }

    private void init(String configFile) {
        // 配置文件优先级：代码配置 > rpc.yaml > 默认配置
        try {
            InputStream yamlStream = RpcConfigProcessor.class.getClassLoader().getResourceAsStream(configFile);
            if (yamlStream == null) {
                this.rpc = new RpcConfig();
                return;
            }
            ObjectMapper objectMapper = new YAMLMapper();
            ObjectReader objectReader = objectMapper.readerFor(RpcConfig.class).withRootName("rpc");
            this.rpc = objectReader.readValue(yamlStream);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[RPC配置]配置解析失败，请检查rpc.yaml文件的配置格式");
        }
    }
}
