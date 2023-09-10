package com.herenpeng.rpc.kit.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.herenpeng.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author herenpeng
 * @since 2023-05-07 21:57
 */
@Slf4j
public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> KEYOS = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 在此处配置kryo对象的使用示例，如循环引用等
        kryo.setReferences(false);
        // 设置是否注册全限定名，
        kryo.setRegistrationRequired(false);
        // 设置初始化策略，如果没有默认无参构造器，那么就需要设置此项，使用此策略构造一个无参构造器
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo;
    });

    @Override
    public byte getId() {
        return KRYO;
    }

    @Override
    public byte[] serialize(Object data) throws RpcException {
        try {
            Kryo kryo = KEYOS.get();
            // 使用 Output 对象池会导致序列化重复的错误（getBuffer返回了Output对象的buffer引用）
            Output opt = new Output(1024, -1);
            kryo.writeClassAndObject(opt, data);
            opt.flush();
            return opt.getBuffer();
        } catch (Exception e) {
            log.error("[RPC工具]Kryo序列化错误：{}，错误信息：{}", data, e.getMessage());
            throw new RpcException("[RPC工具]Kryo序列化错误：" + data);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Type valueType) throws RpcException {
        try (Input input = new Input(bytes)) {
            Kryo kryo = KEYOS.get();
            return (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            log.error("[RPC工具]Kryo反序列化错误：{}，反序列化类型：{}，错误信息：{}", bytes, valueType, e.getMessage());
            throw new RpcException("[RPC工具]Kryo反序列化错误：" + Arrays.toString(bytes) + "，反序列化类型：" + valueType);
        }
    }
}
