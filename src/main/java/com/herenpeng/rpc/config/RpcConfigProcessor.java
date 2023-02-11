package com.herenpeng.rpc.config;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author herenpeng
 * @since 2023-02-09 21:51
 */
@Data
public class RpcConfigProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RpcConfigProcessor.class);

    private RpcConfig rpc;

    public RpcConfigProcessor() {
        init();
    }

    private void init() {
        // 配置文件优先级：代码配置 > rpc.yaml > 默认配置
        try {
            InputStream yamlStream = RpcConfigProcessor.class.getClassLoader().getResourceAsStream("rpc.yaml");
            if (yamlStream == null) {
                this.rpc = new RpcConfig();
                return;
            }
            ObjectMapper objectMapper = new YAMLMapper();
            ObjectReader objectReader = objectMapper.readerFor(RpcConfig.class).withRootName("rpc");
            this.rpc = objectReader.readValue(yamlStream);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[RPC客户端]配置解析失败，请检查rpc.yaml文件的配置格式");
        }
    }
}