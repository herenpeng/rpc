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
    public byte[] serialize(final Object data) throws RpcException {
        ByteArrayOutputStream os;
        Hessian2Output out = null;
        try {
            os = new ByteArrayOutputStream();
            out = new Hessian2Output(os);
            out.writeObject(data);
            out.flush();
            return os.toByteArray();
        } catch (IOException e) {
            log.error("[RPC工具]Hessian序列化错误：{}", data);
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
    public <T> T deserialize(final byte[] bytes, Type valueType) throws RpcException {
        ByteArrayInputStream is;
        Hessian2Input in = null;
        try {
            is = new ByteArrayInputStream(bytes);
            in = new Hessian2Input(is);
            return (T) in.readObject();
        } catch (Exception ex) {
            log.error("[RPC工具]Hessian反序列化错误：{}, 反序列化类型：{}", bytes, valueType);
            throw new RpcException("[RPC工具]Hessian反序列化错误：" + Arrays.toString(bytes) + ", 反序列化类型：" + valueType);
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
