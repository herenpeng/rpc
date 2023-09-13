package com.herenpeng.rpc.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.herenpeng.rpc.bean.User;
import org.junit.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.Arrays;
import java.util.Date;

/**
 * @author herenpeng
 * @since 2023-05-28 11:15
 */
public class KryoSerializerTest {

    private static final User user = new User(17, "小雷", true, 25, new Date(), new Date(), new Date());

    @Test
    public void test01() {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        //设置是否注册全限定名，
        kryo.setRegistrationRequired(false);
        //设置初始化策略，如果没有默认无参构造器，那么就需要设置此项,使用此策略构造一个无参构造器
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

        // 序列化
        Output opt = new Output(1024, -1);
        kryo.writeClassAndObject(opt, user);
        opt.flush();
        byte[] buffer = opt.getBuffer();
        System.out.println(Arrays.toString(buffer));

        // 反序列化
        Input input = new Input(buffer);
        Object object = kryo.readClassAndObject(input);
        System.out.println(object);
    }

}
