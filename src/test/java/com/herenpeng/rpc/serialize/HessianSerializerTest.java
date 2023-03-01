package com.herenpeng.rpc.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.herenpeng.rpc.common.RpcMethodLocator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author herenpeng
 * @since 2023-03-01 23:22
 */
public class HessianSerializerTest {

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(os);
        out.writeObject(new RpcMethodLocator("com.herenpeng.rpc.service.UserService", "getUserInfo", new String[]{"java.lang.String"}, true));
        out.flush();
        byte[] bytes2 = os.toByteArray();
        System.out.println(Arrays.toString(bytes2));

        ByteArrayInputStream is = new ByteArrayInputStream(bytes2);
        Hessian2Input in = new Hessian2Input(is);
        RpcMethodLocator locator = (RpcMethodLocator) in.readObject();
        System.out.println(locator.getClassName());
    }
}
