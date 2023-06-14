package com.herenpeng.rpc.service;

import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.kit.RpcCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author herenpeng
 * @since 2021-08-30 23:05
 */
@RpcService
public class UserServiceImpl implements UserService {


    @Override
    public User getUserInfo(String name) {
        try {
            Thread.sleep(10000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new User(15, name, true, 18, new Date(), new Date(), new Date());
    }

    @Override
    public User getUserInfo(String name, RpcCallback<User> callback) {
        return getUserInfo(name);
    }

    @Override
    public List<User> getUserList() {
        List<User> list = new ArrayList<>();
        list.add(new User(15, "小明", true, 18, new Date(), new Date(), new Date()));
        list.add(new User(16, "小红", false, 21, new Date(), new Date(), new Date()));
        list.add(new User(17, "小雷", true, 25, new Date(), new Date(), new Date()));
        list.add(new User(18, "小刚", true, 29, new Date(), new Date(), new Date()));
        list.add(new User(19, "小李", true, 42, new Date(), new Date(), new Date()));
        list.add(new User(20, "小王", false, 28, new Date(), new Date(), new Date()));
        list.add(new User(21, "小周", false, 35, new Date(), new Date(), new Date()));
        return list;
    }

    @Override
    public List<User> getUserList(RpcCallback<List<User>> callback) {
        return getUserList();
    }

    @Override
    public User updateUser(User user) {
        user.setUsername("修改后的用户名");
        return user;
    }

    @Override
    public List<User> updateUsers(List<User> users) {
        List<User> list = new ArrayList<>();
        list.add(new User(15, "小明2", true, 18, new Date(), new Date(), new Date()));
        list.add(new User(16, "小红2", false, 21, new Date(), new Date(), new Date()));
        list.add(new User(17, "小雷2", true, 25, new Date(), new Date(), new Date()));
        list.add(new User(18, "小刚3", true, 29, new Date(), new Date(), new Date()));
        list.add(new User(19, "小李4", true, 42, new Date(), new Date(), new Date()));
        list.add(new User(20, "小王5", false, 28, new Date(), new Date(), new Date()));
        list.add(new User(21, "小周6", false, 35, new Date(), new Date(), new Date()));
        return list;
    }
}
