package com.herenpeng.rpc.kit.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.herenpeng.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author herenpeng
 * @since 2023-03-01 21:52
 */
@Slf4j
public class HessianSerializer implements Serializer {


    @Override
    public byte getId() {
        return HESSIAN;
    }

    @Override
    public byte[] serialize(final Object data) {
        Hessian2Output out = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            out = new Hessian2Output(os);
            out.writeObject(data);
            out.flush();
            return os.toByteArray();
        } catch (Exception e) {
            log.error("[RPC工具]Hessian序列化错误：{}，错误信息：{}", data, e.getMessage());
            throw new RpcException("[RPC工具]Hessian序列化错误：" + data);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public <T> T deserialize(final byte[] bytes, Type valueType) {
        Hessian2Input in = null;
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            in = new Hessian2Input(is);
            return (T) in.readObject();
        } catch (Exception e) {
            log.error("[RPC工具]Hessian反序列化错误，反序列化类型：{}，错误信息：{}", valueType, e.getMessage());
            throw new RpcException("[RPC工具]Hessian反序列化错误，反序列化类型：" + valueType);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
