package com.herenpeng.rpc.reflect;

import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.service.UserServiceImpl;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author herenpeng
 * @since 2023-02-27 21:09
 */
public class RpcMethodTest {

    @Test
    public void test01() throws Exception {
        Method getUserList = UserServiceImpl.class.getDeclaredMethod("getUserList");
        Object[] param = new Object[]{};
        Object userInfo = getUserList.invoke(new UserServiceImpl(), param);
        System.out.println(userInfo);
    }


    @Test
    public void test02() throws Exception {
        Method updateUser = UserServiceImpl.class.getDeclaredMethod("updateUser", User.class);
        Type[] updateUserGenericParameterTypes = updateUser.getGenericParameterTypes();
        for (Type type : updateUserGenericParameterTypes) {
            System.out.println(type.getClass() + "===>" + type.getTypeName());
        }

        Method updateUsers = UserServiceImpl.class.getDeclaredMethod("updateUsers", List.class);
        Type[] updateUsersGenericParameterTypes = updateUsers.getGenericParameterTypes();
        for (Type type : updateUsersGenericParameterTypes) {
            System.out.println(type.getClass() + "===>" + type.getTypeName());
        }
    }
}
