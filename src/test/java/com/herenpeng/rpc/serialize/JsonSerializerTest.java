package com.herenpeng.rpc.serialize;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.kit.ValueType;
import com.herenpeng.rpc.service.UserServiceImpl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author herenpeng
 * @since 2023-02-26 20:43
 */
public class JsonSerializerTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    static List<User> list = new ArrayList<>();

    static {
        list.add(new User(15, "小明", true, 18, new Date(), new Date(), new Date()));
        list.add(new User(16, "小红", false, 21, new Date(), new Date(), new Date()));
        list.add(new User(17, "小雷", true, 25, new Date(), new Date(), new Date()));
        list.add(new User(18, "小刚", true, 29, new Date(), new Date(), new Date()));
        list.add(new User(19, "小李", true, 42, new Date(), new Date(), new Date()));
        list.add(new User(20, "小王", false, 28, new Date(), new Date(), new Date()));
        list.add(new User(21, "小周", false, 35, new Date(), new Date(), new Date()));
    }

    public static void main(String[] args) throws Exception {
        String json = objectMapper.writeValueAsString(list);
        System.out.println(json);

        List<User> list = objectMapper.readValue(json, new TypeReference<>() {});


        Method getUserList = UserServiceImpl.class.getDeclaredMethod("getUserList");
        Type type = getUserList.getGenericReturnType();
        System.out.println(type);

        Method getUserInfo = UserServiceImpl.class.getDeclaredMethod("getUserInfo", String.class);
        Type type2 = getUserInfo.getGenericReturnType();
        System.out.println(type2);

        Type type3 = User.class.getGenericSuperclass();
        System.out.println(type3);

    }

}
