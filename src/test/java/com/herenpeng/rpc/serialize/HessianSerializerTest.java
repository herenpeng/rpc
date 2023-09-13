package com.herenpeng.rpc.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.common.RpcMethodLocator;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * @author herenpeng
 * @since 2023-03-01 23:22
 */
public class HessianSerializerTest {

    @Test
    public void test01() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(os);
        out.writeObject(new RpcMethodLocator("com.herenpeng.rpc.service.UserService", "getUserInfo", null, new String[]{"java.lang.String"}, true));
        out.flush();
        byte[] bytes = os.toByteArray();
        System.out.println(Arrays.toString(bytes));

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input in = new Hessian2Input(is);
        RpcMethodLocator locator = (RpcMethodLocator) in.readObject();
        System.out.println(locator.getClassName());


        User[] users = new User[]{
                new User(15, "小明", true, 18, new Date(), new Date(), new Date()),
                new User(16, "小红", false, 21, new Date(), new Date(), new Date())
        };
        ByteArrayOutputStream os2 = new ByteArrayOutputStream();
        Hessian2Output out2 = new Hessian2Output(os2);
        out2.writeObject(users);
        out2.flush();
        byte[] bytes2 = os2.toByteArray();
        System.out.println(Arrays.toString(bytes2));

        ByteArrayInputStream is2 = new ByteArrayInputStream(bytes2);
        Hessian2Input in2 = new Hessian2Input(is2);
        User[] list = (User[]) in2.readObject();
        for (User user : list) {
            System.out.println(user.getUsername());
        }
    }
}
